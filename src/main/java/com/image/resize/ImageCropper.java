package com.image.resize;

import com.image.handler.image.Image;
import io.vavr.control.Try;

public interface ImageCropper {

    Try<Image> crop(Image image, String newSize);

    ResizeImageType supportedImageType();

}
