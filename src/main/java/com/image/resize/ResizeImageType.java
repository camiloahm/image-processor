
package com.image.resize;

import com.image.handler.image.Image;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ResizeImageType {
    svg,
    other;

    private static boolean isSpecificResizingAvailable(String imageExtension) {
        return Stream
                .of(ResizeImageType.values())
                .map(ResizeImageType::name)
                .collect(Collectors.toList())
                .contains(imageExtension);
    }

    public static ResizeImageType getResizeImageTypeForImage(final Image image) {
        if (isSpecificResizingAvailable(image.getExtension().toLowerCase())) {
            return ResizeImageType.valueOf(image.getExtension().toLowerCase());
        } else {
            return ResizeImageType.other;
        }
    }

}
