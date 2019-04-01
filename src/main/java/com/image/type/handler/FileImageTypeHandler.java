
package com.image.type.handler;

import com.image.handler.image.Image;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.IOException;

class FileImageTypeHandler implements ImageTypeHandler {

    @Override
    public boolean isValidData(Object data) {
        return StandardMultipartHttpServletRequest.class.equals(data.getClass().getDeclaringClass());
    }

    @Override
    public Image createImage(Object data, String client, String imageKey) throws IOException {
        MultipartFile imageFile = (MultipartFile) data;
        return getImage(imageFile.getBytes(), imageFile.getOriginalFilename(), client);
    }

}
