package com.carenexus.common.config;

import com.carenexus.file.FileStorageProperties;
import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class NoteMediaConfig implements WebMvcConfigurer {
    private final FileStorageProperties properties;

    public NoteMediaConfig(FileStorageProperties properties) { this.properties = properties; }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(properties.getRootPath()).toAbsolutePath().normalize().toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/note-media/**").addResourceLocations(location);
    }
}
