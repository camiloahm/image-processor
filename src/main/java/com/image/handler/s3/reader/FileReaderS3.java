
package com.image.handler.s3.reader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.image.handler.image.Image;
import com.image.util.ImageUtil;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;

@Component
class FileReaderS3 implements FileReader {

    private final AmazonS3 s3Client;

    private final String bucket;

    @Autowired
    FileReaderS3(@Value("${images.s3.region}") String region,
            @Value("${images.s3.bucket}") String bucket) {

        this.s3Client = AmazonS3Client.builder().withRegion(region).build();
        this.bucket = bucket;
    }

    @Override
    public Try<Image> read(final String key) {

        return Try.of(() -> readFileFromS3(key));
    }

    private Image readFileFromS3(String key) throws FileNotFoundException {

        if (Strings.isNullOrEmpty(key)) {
            return new Image(new byte[0]);
        }
        Try<String> s3Key = getObjectKey(ImageUtil.getImageName(key));
        if (s3Key.isFailure()) {
            throw new FileNotFoundException(
                    MessageFormat.format("Image \"{0}\" not found in S3 bucket", key));
        }
        final String s3KeyValue = s3Key.get();
        try (S3Object object = s3Client.getObject(new GetObjectRequest(bucket, s3KeyValue))) {

            byte[] content = ByteStreams.toByteArray(object.getObjectContent());
            String extension = ImageUtil.getExtension(s3KeyValue);

            return new Image(content, extension, s3KeyValue);
        } catch (AmazonS3Exception ex) {
            throw new FileNotFoundException(
                    MessageFormat.format("Image \"{0}\" not found in S3 bucket", s3KeyValue));
        } catch (IOException ex) {
            throw new FileNotFoundException(
                    MessageFormat.format("Falied to read image \"{0}\" in S3 bucket", s3KeyValue));
        }
    }

    private Try<String> getObjectKey(String key) {
        final ObjectListing listing = s3Client.listObjects(bucket, key);
        return Try.of(() -> listing.getObjectSummaries().isEmpty() ? key : listing.getObjectSummaries().get(0).getKey());
    }
}
