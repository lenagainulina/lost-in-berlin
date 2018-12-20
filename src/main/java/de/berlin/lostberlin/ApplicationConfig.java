package de.berlin.lostberlin;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EntityScan(
        basePackageClasses = {LostberlinApplication.class, Jsr310JpaConverters.class})
@EnableJpaAuditing
public class ApplicationConfig {

}
