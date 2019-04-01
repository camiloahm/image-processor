
package com.image.handler.s3;

import lombok.Getter;

import javax.ws.rs.core.Response.Status;

@Getter
public class S3Exception extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Status httpStatus;

    public S3Exception(final String message, final Status httpStatus) {

        super(message);
        this.httpStatus = httpStatus;
    }

    public S3Exception(final String message, final Status httpStatus, final Throwable exception) {

        super(message, exception);
        this.httpStatus = httpStatus;
    }

    public S3Exception(final String message, final Throwable exception) {

        super(message, exception);
    }

}
