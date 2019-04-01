
package com.image.type.handler;

import com.image.handler.image.Image;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Base64;

class Base64ImageTypeHandler implements ImageTypeHandler {

    private static String BASE64_IMAGE_PARTS_SEPARATOR = ",";

    @Override
    public boolean isValidData(Object data) {
        return data.getClass().equals(String.class);
    }

    @Override
    public Image createImage(Object data, String client, String name) throws IOException {
        String base64Image = (String) data;
        if (base64Image.contains(BASE64_IMAGE_PARTS_SEPARATOR)) {
            base64Image = StringUtils.substringAfter(base64Image, BASE64_IMAGE_PARTS_SEPARATOR);
        }
        return getImage(Base64.getDecoder().decode(base64Image), name, client);
    }

}