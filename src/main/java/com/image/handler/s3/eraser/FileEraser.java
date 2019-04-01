
package com.image.handler.s3.eraser;

import io.vavr.control.Try;

public interface FileEraser {

    Try<Integer> deleteFilesByRegEx(String regEx);

}
