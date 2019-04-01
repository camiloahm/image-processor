
package com.image.handler.image;

import com.image.handler.s3.reader.FileReader;
import com.image.handler.s3.writer.FileWriter;
import com.image.resize.ImageResizer;
import com.image.util.ImageUtil;
import io.vavr.Tuple;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

@Service
class DefaultHandlerService implements ImageHandler {

    private final ImageResizer imageResizer;
    private final FileReader fileReader;
    private final FileWriter fileWriter;

    DefaultHandlerService(ImageResizer imageResizer, FileReader fileReader, FileWriter fileWriter) {
        this.imageResizer = imageResizer;
        this.fileReader = fileReader;
        this.fileWriter = fileWriter;
    }

    @Override
    public Try<Image> processImage(String client, String size, String imageId) {

        return fileReader
                .read(getBaseImagePath(client, imageId))
                .flatMap(image -> resizeImage(image, size))
                .andThen(resizedImage -> saveImage(resizedImage, getNewKey(client, size,
                        ImageUtil.getImageName(imageId), resizedImage.getExtension())));
    }

    private Try<Boolean> saveImage(Image image) {
        return saveImage(image, image.getKey());
    }

    private Try<Boolean> saveImage(Image image, String key) {
        return Try.of(() -> fileWriter.write(image.getContent(), key));
    }

    private Try<Image> resizeImage(Image image, String size) {
        return imageResizer.resize(image, size);
    }

    private String getNewKey(String client, String size, String imageId, String extension) {
        return client + "/" + size + "/" + imageId + "." + extension;
    }

    private String getBaseImagePath(String client, String imageKey) {
        return client + "/" + imageKey;
    }

    @Override
    public Try<String> storeImage(Image image, String imageName, String client) {

        return Try.of(() -> Tuple.of(image, saveImage(image).get()))
                .filter(tuple -> tuple._2())
                .map(tuple -> tuple._1().getKey());
    }
}
