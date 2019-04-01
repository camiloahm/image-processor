
package com.image.type.handler;

import com.image.handler.image.Image;

import java.io.IOException;

public enum ImageInputType {
    FILE(new FileImageTypeHandler()),
    BASE_64(new Base64ImageTypeHandler()),
    URL((new UrlImageTypeHandler())),
    SVG(new SvgImageTypeHandler());

    private final ImageTypeHandler imageTypeHandler;

    ImageInputType(ImageTypeHandler imageTypeHandler) {
        this.imageTypeHandler = imageTypeHandler;
    }

    public boolean isValidDataType(Object data) {
        return imageTypeHandler.isValidData(data);
    }

    public Image getImage(Object data, String client, String imageName) throws IOException {
        return imageTypeHandler.createImage(data, client, imageName);
    }

}
