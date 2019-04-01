
package com.image.handler.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Image {

    private byte[] content;
    private String extension;
    private String contentType;
    private String key;

    public Image(byte[] content) {
        this.content = content;
    }

    public Image(byte[] content, String extension) {
        this.content = content;
        this.extension = extension;
    }

    public Image(byte[] content, String extension, String key) {
        this.content = content;
        this.extension = extension;
        this.key = key;
    }
}
