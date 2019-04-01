
package com.image.handler.s3.reader;

import com.image.handler.image.Image;
import io.vavr.control.Try;

public interface FileReader {

    Try<Image> read(String uuid);

}
