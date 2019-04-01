
package com.image.handler.image;

import com.image.handler.s3.reader.FileReader;
import com.image.handler.s3.writer.FileWriter;
import com.image.resize.ImageResizer;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class DefaultHandlerServiceTest {

    @Mock
    private ImageResizer imageResizer;

    @Mock
    private FileReader fileReader;

    @Mock
    private FileWriter fileWriter;

    @InjectMocks
    private DefaultHandlerService handlerService;

    private Image image;
    private String IMAGE_NAME = "_KEY_.jpg";
    private String CLIENT = "_CLIENT_";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        image = Image.builder().key("_KEY_.jpg").extension("jpg").build();
    }

    @Test
    public void storeImageTest() {
        when(fileWriter.write(any(), anyString())).thenReturn(true);
        Try<String> response = handlerService.storeImage(image, IMAGE_NAME, CLIENT);
        assertFalse(response.isFailure());
        assertEquals(IMAGE_NAME, response.get());
    }

    @Test
    public void storeImageFailedTest() {
        when(fileWriter.write(any(), anyString())).thenReturn(false);
        Try<String> response = handlerService.storeImage(image, IMAGE_NAME, CLIENT);
        assertTrue(response.isFailure());
    }

    @Test
    public void processImageTest() {
        final String size = "100x100";
        when(fileWriter.write(any(), anyString())).thenReturn(true);
        when(fileReader.read(anyString())).thenReturn(Try.of(() -> image));
        when(imageResizer.resize(image, size)).thenReturn(Try.of(() -> image));
        Try<Image> processedImage = handlerService.processImage(CLIENT, size, IMAGE_NAME);
        assertEquals(IMAGE_NAME, processedImage.get().getKey());
    }


}
