package de.berlin.lostberlin.service;

import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.exception.ResourceNotSavedException;
import de.berlin.lostberlin.service.fileUpload.FileServiceImpl;
import de.berlin.lostberlin.service.fileUpload.config.FileStorageConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileServiceTest {

    private FileStorageConfiguration properties = new FileStorageConfiguration();
    private FileServiceImpl service;

    @Before
    public void initFileStorage() throws Exception {
        properties.setLocation("target/upload-dir/");
        service = new FileServiceImpl(properties);
        service.initFileStorage();
    }

    @Test
    public void loadFileAsResourceTest() {
        service.storeFile(new MockMultipartFile("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()));
        Resource file= service.loadFileAsResource("foo.txt");

        assertTrue(file.exists());
    }
    @Test(expected = ResourceNotFoundException.class)
    public void loadNonExistent() {
        Resource file= service.loadFileAsResource("boo.txt");
        assertFalse(file.exists());
    }

    @Test(expected = ResourceNotSavedException.class)
    public void storeFileNotSavedTest() {
        service.storeFile(new MockMultipartFile("foo", "../foo.txt",
                MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes()));
    }

    @Test
    public void storeFileTest() {
        service.storeFile(new MockMultipartFile("foo", "bar/../foo.txt",
                MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes()));
    }

}