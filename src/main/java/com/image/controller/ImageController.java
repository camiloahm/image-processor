
package com.image.controller;

import com.google.common.base.Preconditions;
import com.image.handler.image.ImageHandler;
import com.image.response.ImageKeyResponse;
import com.image.type.handler.ImageInputType;
import com.image.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@RestController
@EnableWebMvc
public class ImageController extends RestResponseExceptionHandler {

    private final ImageHandler imageHandler;
    private final static Pattern sizePattern = Pattern.compile("((\\d+)x(\\d+))");
    private final Pattern fileNamePattern;

    @Autowired
    ImageController(ImageHandler imageHandler, @Value("${images.allowed.formats}") String imageFormats) {

        this.imageHandler = imageHandler;
        this.fileNamePattern = Pattern.compile("\\w+[.]" + "(" + imageFormats + ")");
    }

    @GetMapping(
            path = "/image/health",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("UP555555");
    }

    @GetMapping(
            path = "/image",
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> processImage(@RequestParam("key") String key) {

        String[] keyParams = key.split("/");
        String client = keyParams[0];
        String size = keyParams[1];
        String imageId = keyParams[2];

        Preconditions.checkArgument(sizePattern.matcher(size).matches(), "Size is not correct");

        return imageHandler
                .processImage(client, size, imageId)
                .map(image -> ResponseEntity
                        .ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="
                                + (ImageUtil.getImageName(imageId) + "." + image.getExtension()))
                        .body(image.getContent()))
                .onFailure(ex -> log.error("process image failed", ex))
                .recover(x -> Match(x).of(
                        Case($(instanceOf(FileNotFoundException.class)),
                                t -> new ResponseEntity<>(HttpStatus.BAD_REQUEST)),
                        Case($(instanceOf(Exception.class)),
                                t -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR))))
                .get();
    }

    @PostMapping(path = "/image",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImageKeyResponse> saveImage(@RequestParam("data") Object imageInput, @RequestParam("type") ImageInputType imageType,
            @RequestParam("client") String client, @RequestParam("imageName") String imageName) throws IOException {

        Preconditions.checkArgument(fileNamePattern.matcher(imageName).matches(), "Filename is not correct");

        Preconditions.checkArgument(imageType.isValidDataType(imageInput), "Image input is not matching to type.");

        return imageHandler.storeImage(imageType.getImage(imageInput, client, imageName), imageName, client)
                .map(key -> ImageKeyResponse.builder().key(key).build())
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .onFailure(ex -> log.error("Image save failed"))
                .getOrElse(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping(
            path = "/image/id/generate",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> getImageId(@RequestParam(value = "client") String client,
            @RequestParam(value = "size", required = false, defaultValue = "") String size,
            @RequestParam("id") String imageId) {

        return ResponseEntity.ok(ImageUtil.generateUuid(client, imageId, size));
    }

}