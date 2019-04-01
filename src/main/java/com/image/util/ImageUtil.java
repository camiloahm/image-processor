
package com.image.util;

import com.google.common.base.Preconditions;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ImageUtil {

    private ImageUtil() {
    }

    public static String getExtension(final String completeImageName) {
        return StringUtils.substringAfterLast(completeImageName, ".");
    }

    public static String getImageName(final String completeImageName) {
        return StringUtils.substringBeforeLast(completeImageName, ".");
    }

    public static String generateImageId(String client, String imageName, String extension, String size) {
        return generateUuid(client, imageName, size) + "." + extension;
    }

    public static String generateUuid(String client, String imageName, String size) {

        Preconditions.checkArgument(client != null && imageName != null,
                "Client and Image can't be null");

        StringBuilder data = new StringBuilder(client)
                .append("_")
                .append(imageName);

        if (!isNullOrEmpty(size)) {
            data.append("_").append(size);
        }

        String md5 = DigestUtils.md5Hex(data.toString());

        return data.append("_").append(md5).toString();
    }
}
