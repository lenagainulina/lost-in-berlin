package de.berlin.lostberlin.service.fileUpload.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
@Getter
@Setter
public class FileStorageConfiguration {
    /**
     * Folder location for storing files
     */
    private String location = "upload-dir";
}
