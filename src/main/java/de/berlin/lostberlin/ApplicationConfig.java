package de.berlin.lostberlin;

import de.berlin.lostberlin.service.fileUpload.FileService;
import de.berlin.lostberlin.service.fileUpload.config.FileStorageConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EntityScan(
        basePackageClasses = {LostberlinApplication.class, Jsr310JpaConverters.class})
@EnableJpaAuditing
@EnableConfigurationProperties(FileStorageConfiguration.class)
public class ApplicationConfig {

   /* @Bean
    CommandLineRunner init(FileService fileService) {
        return (args) -> {
            fileService.deleteAllFiles();
            fileService.initFileStorage();
        };
    }*/
}
