
package com.image.handler.image;

import io.vavr.control.Try;

public interface ImageHandler {

    Try<Image> processImage(String client, String size, String imageId);

    Try<String> storeImage(Image image, String imageName, String client);
}
