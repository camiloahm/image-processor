
package com.image.handler.s3.writer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.base.Strings;
import com.image.handler.s3.S3Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response.Status;
import java.io.ByteArrayInputStream;
import java.text.MessageFormat;

@Component
class FileWriterS3 implements FileWriter {

    private final AmazonS3 s3Client;

    private final String bucket;

    @Autowired
    FileWriterS3(@Value("${images.s3.region}") String region,
            @Value("${images.s3.bucket}") String bucket) {

        this.s3Client = AmazonS3Client.builder().withRegion(region).build();
        this.bucket = bucket;
    }

    @Override
    public boolean write(final byte[] content, final String key) {

        return this.write(content, key, null);
    }

    @Override
    public boolean write(final byte[] content, final String key, final String contentType) {

        try {
            if (content == null || content.length == 0) {
                return false;
            }

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(content.length);
            if (!Strings.isNullOrEmpty(contentType)) {
                metadata.setContentType(contentType);
            }

            s3Client.putObject(new PutObjectRequest(bucket, key, new ByteArrayInputStream(content), metadata));
            return true;
        } catch (AmazonServiceException ase) {
            String errorMessage = MessageFormat.format("Request made with: Bucket: {0}, Key: {1} has failed", bucket, key);
            throw new S3Exception(errorMessage, Status.fromStatusCode(ase.getStatusCode()), ase);
        } catch (AmazonClientException ace) {
            String errorMessage = MessageFormat.format("The AWS S3 client encountered an internal" +
                    " error trying to communicate with S3. Bucket: {0}, Key: {1}", bucket, key);
            throw new S3Exception(errorMessage, ace);
        }
    }

}