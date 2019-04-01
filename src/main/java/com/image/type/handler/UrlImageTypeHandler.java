
package com.image.type.handler;

import com.image.handler.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class UrlImageTypeHandler implements ImageTypeHandler {

    @Override
    public boolean isValidData(Object data) {
        return data.getClass().equals(String.class);
    }

    @Override
    public Image createImage(final Object data, final String client, final String fileName) throws IOException {
        URL url = new URL((String) data);
        BufferedImage bufferedImage = ImageIO.read(url);
        return getImage(getByteArrayFromBufferedImage(bufferedImage, fileName),
                fileName, client);
    }

}
