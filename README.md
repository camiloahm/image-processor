# IMAGE PROCESSOR

This project is a smart imaging service. It enables on-demand crop, resizing of images, images are stored in S3 bucket by different clients. 

### Environment Variables

| Variable Name  | Value |
|--------------| -------------|
|images.allowed.formats| img formats valid  |
|images.size| max image size  |
|images.s3.region| S3 region for image bucket|
|images.s3.bucket|S3 bucket name   |

### Command to run the project

```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006 \
    -Dspring.config.name=dev-local
    -Dimages.s3.region=[IMAGE-REGION] \
    -Dimages.s3.bucket=[IMAGE-BUCKET] \
    -jar target/*.jar
```



