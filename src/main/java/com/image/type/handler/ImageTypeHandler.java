
package com.image.type.handler;

import com.image.handler.image.Image;
import com.image.util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface ImageTypeHandler {

    boolean isValidData(Object data);

    Image createImage(Object data, String client, String imageKey) throws IOException;

    static final String BASE = "base";

    default String getBaseImagePath(String client, String imageKey) {
        return client + "/" + imageKey;
    }

    default byte[] getByteArrayFromBufferedImage(BufferedImage bufferedImage, String fileName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, ImageUtil.getExtension(fileName), baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

    default Image getImage(final byte[] data, final String fileName, final String client) {
        final String extension = ImageUtil.getExtension(fileName);
        return Image
                .builder()
                .content(data)
                .extension(extension)
                .key(getBaseImagePath(client, ImageUtil.generateImageId(client, ImageUtil.getImageName(fileName), extension, BASE)))
                .build();
    }
}
