
package com.image.handler.s3.writer;

public interface FileWriter {

    boolean write(byte[] content, String key);

    boolean write(byte[] content, String key, String contentType);

}