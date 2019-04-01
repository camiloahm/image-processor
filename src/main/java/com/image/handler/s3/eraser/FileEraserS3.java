
package com.image.handler.s3.eraser;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.base.Strings;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
class FileEraserS3 implements FileEraser {

    private final AmazonS3 s3Client;

    private final String bucket;

    @Autowired
    FileEraserS3(@Value("${images.s3.region}") String region,
            @Value("${images.s3.bucket}") String bucket) {

        this.s3Client = AmazonS3Client.builder().withRegion(region).build();
        this.bucket = bucket;
    }

    @Override
    public Try<Integer> deleteFilesByRegEx(String regEx) {

        return Try.of(() -> deleteFileInS3(regEx));
    }

    private int deleteFileInS3(String regEx) {
        String finalRegEx = regEx;
        if (Strings.isNullOrEmpty(finalRegEx)) {
            finalRegEx = ".*";
        }
        List<String> keysToDelete = getKeysToDelete(finalRegEx);
        int i = 0;
        for (i = 0; i < keysToDelete.size(); i++) {
            s3Client.deleteObject(new DeleteObjectRequest(bucket, keysToDelete.get(i)));
        }
        return i;
    }

    private List<String> getKeysToDelete(final String regEx) {

        ObjectListing listing = s3Client.listObjects(bucket);
        List<S3ObjectSummary> summaries = listing.getObjectSummaries();

        while (listing.isTruncated()) {
            listing = s3Client.listNextBatchOfObjects(listing);
            summaries.addAll(listing.getObjectSummaries());
        }

        List<String> s3Objects = summaries.stream()
                .filter(s3Ob -> s3Ob.getKey().matches(regEx))
                .filter(Objects::nonNull)
                .map(s3Object -> s3Object.getKey())
                .collect(Collectors.toList());

        return s3Objects != null && !s3Objects.isEmpty() ? s3Objects : new ArrayList<>();
    }

}
