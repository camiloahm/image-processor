
package com.image.resize;

import com.image.handler.image.Image;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ImageResizer {

    Map<ResizeImageType, ImageCropper> availableTypeImageCroppers;

    @Autowired
    public ImageResizer(Collection<ImageCropper> availableImageCroppers) {
        this.availableTypeImageCroppers = availableImageCroppers
                .stream()
                .collect(Collectors.toMap(ImageCropper::supportedImageType, Function.identity()));
    }

    public Try<Image> resize(Image image, String newSize) {
        ImageCropper imageCropper = availableTypeImageCroppers
                .get(ResizeImageType.getResizeImageTypeForImage(image));
        return imageCropper.crop(image, newSize);
    }
}
