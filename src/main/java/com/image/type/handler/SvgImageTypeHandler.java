
package com.image.type.handler;

import com.image.handler.image.Image;

import java.io.IOException;

class SvgImageTypeHandler implements ImageTypeHandler {

    @Override
    public boolean isValidData(Object data) {
        return data.getClass().equals(String.class) || ImageInputType.FILE.isValidDataType(data);
    }

    @Override
    public Image createImage(Object data, String client, String fileName) throws IOException {

        if (ImageInputType.FILE.isValidDataType(data)) {
            return ImageInputType.FILE.getImage(data, client, fileName);
        } else {
            return getImage(((String) data).getBytes(), fileName, client);
        }
    }
}
