package com.image.resize;

import com.image.handler.image.Image;
import io.vavr.control.Try;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
class SvgImageCropper implements ImageCropper {

    private static final Color ALPHA = new Color(0, 0, 0, 1);
    private static final int X_0 = 0;
    private static final int Y_0 = 0;
    private static final String FORMAT = "png";

    private Image cropImage(Image image, String newSize) throws IOException, TranscoderException {
        final SVGImageDataExtractor extractor = new SVGImageDataExtractor(new String(image.getContent()));
        final SizeCalculator size = new SizeCalculator(newSize);
        final BufferedImage background = background(size.getBgWidth(), size.getBgHeight(), extractor.bgColor());
        final BufferedImage svg = svg(extractor.svgInputStream(), size.getSvgWidth(), size.getSvgHeight());
        final BufferedImage combined = compound(background, svg, size);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(combined, FORMAT, baos);

        return Image
                .builder()
                .content(baos.toByteArray())
                .extension(FORMAT)
                .build();
    }

    @Override
    public Try<Image> crop(Image image, String newSize) {
        return Try.of(() -> cropImage(image, newSize));
    }

    @Override
    public ResizeImageType supportedImageType() {
        return ResizeImageType.svg;
    }

    private BufferedImage compound(final BufferedImage background, final BufferedImage svg,
            final SizeCalculator size) {
        final BufferedImage combined = new BufferedImage(size.getBgWidth(), size.getBgHeight(),
                BufferedImage.TYPE_INT_ARGB);
        final Graphics g = combined.getGraphics();
        g.drawImage(background, X_0, Y_0, null);
        g.drawImage(svg, size.xPadding(), size.yPadding(), null);
        return combined;
    }

    private BufferedImage svg(final InputStream inputStream, final int svgWidth, final int svgHeight)
            throws TranscoderException, IOException {
        final OutputStream outputStream = new ByteArrayOutputStream();
        final TranscoderInput inputSvgImage = new TranscoderInput(inputStream);
        final TranscoderOutput outputPngImage = new TranscoderOutput(outputStream);
        final PNGTranscoder pngTranscoder = new PNGTranscoder();
        pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, ALPHA);
        pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, Float.valueOf(svgWidth));
        pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, Float.valueOf(svgHeight));
        pngTranscoder.transcode(inputSvgImage, outputPngImage);
        ByteArrayOutputStream arrayOutputStream = (ByteArrayOutputStream) outputPngImage.getOutputStream();
        final byte[] byteArray = arrayOutputStream.toByteArray();
        final InputStream input = new ByteArrayInputStream(byteArray);
        outputStream.flush();
        outputStream.close();
        return ImageIO.read(input);
    }

    private BufferedImage background(final int bgWidth, final int bgHeight, final Color color) {
        final BufferedImage background = new BufferedImage(bgWidth, bgHeight, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = background.createGraphics();
        graphics.setBackground(color);
        graphics.clearRect(X_0, Y_0, bgWidth, bgHeight);
        return background;
    }

    private static class SVGImageDataExtractor {
        private static final String TAB = "\t";
        private static final String NEW_LINE = "\n";
        private static final String CARRY_RETURN = "\r";
        private static final String SHORT_QUOTES = "/\"";
        private static final String QUOTES = " \"";
        private static final String EMPTY = StringUtils.EMPTY;

        private final InputStream inputStream;
        private final Color color;

        SVGImageDataExtractor(String source) {
            try {
                this.color = Color.WHITE;
                this.inputStream = new ByteArrayInputStream(
                        sanitize(source).getBytes(StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException | IndexOutOfBoundsException e) {
                // If some exception occurs, indicate that the needed information to process the svg
                // is incomplete so we are going to an IllegalArgumentException.
                throw new IllegalArgumentException(e);
            }
        }

        private String sanitize(final String svgSource) {
            return svgSource
                    .replaceAll(NEW_LINE, EMPTY)
                    .replaceAll(TAB, EMPTY)
                    .replaceAll(CARRY_RETURN, EMPTY)
                    .replaceAll(SHORT_QUOTES, QUOTES);
        }

        InputStream svgInputStream() {
            return inputStream;
        }

        Color bgColor() {
            return color;
        }
    }

    /**
     * This component is the responsible to calculate the different size for the compound image. The
     * SVG icon should be a 65% of the width by definition. For example: if the image has the size
     * of 106px X 160px then the svg should be 104px X 104px, a 65% of the 160px.
     */
    private static class SizeCalculator {
        private static final double SVG_RATE = .65;

        private final int targetWidth;
        private final int targetHeight;

        SizeCalculator(String size) {
            String[] sizeParams = size.split("x");
            targetWidth = Integer.parseInt(sizeParams[0]);
            targetHeight = Integer.parseInt(sizeParams[1]);
        }

        /**
         * The entire image width.
         *
         * @return a integer that represent the final image width.
         */
        int getBgWidth() {
            return targetWidth;
        }

        /**
         * The entire image height.
         *
         * @return a integer that represent the final image height.
         */
        int getBgHeight() {
            return targetHeight;
        }

        /**
         * The SVG should be a 65% if the width.
         *
         * @return a integer that represent the svg real width.
         */
        int getSvgWidth() {
            return (int) (targetWidth * SVG_RATE);
        }

        /**
         * The SVG should be a 65% if the height.
         *
         * @return a integer that represent the svg real height
         */
        int getSvgHeight() {
            return (int) (targetHeight * SVG_RATE);
        }

        /**
         * @return an integer that represent the needed padding to center the svg on the final image
         * on Y axis.
         */
        int yPadding() {
            return (getBgHeight() - getSvgHeight()) / 2;
        }

        /**
         * @return an integer that represent the needed padding to center the svg on the final image
         * on X axis.
         */
        int xPadding() {
            return (getBgWidth() - getSvgWidth()) / 2;
        }
    }
}
