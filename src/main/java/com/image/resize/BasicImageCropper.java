
package com.image.resize;

import com.image.handler.image.Image;
import com.twelvemonkeys.image.ResampleOp;
import io.vavr.control.Try;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
class BasicImageCropper implements ImageCropper {

    private Image cropImage(Image image, int width, int height) throws IOException {

        final BufferedImage sourceImage = ImageIO.read(new ByteArrayInputStream(image.getContent()));
        final BufferedImageOp resampler = new ResampleOp(width, height, ResampleOp.FILTER_LANCZOS);
        final BufferedImage outputImage = resampler.filter(sourceImage, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(outputImage, image.getExtension(), baos);

        return Image.builder()
                .content(baos.toByteArray())
                .extension(image.getExtension())
                .contentType(image.getContentType())
                .build();

    }

    @Override
    public Try<Image> crop(Image image, String newSize) {
        String[] size = newSize.split("x");
        return Try.of(() -> cropImage(image, Integer.parseInt(size[0]), Integer.parseInt(size[1])));
    }

    @Override
    public ResizeImageType supportedImageType() {

        return ResizeImageType.other;
    }
}
