package com.n26.statistics.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class AppConfiguration {

    @Bean
    @ConfigurationProperties("window")
    public Window windowConfiguration() {
        return new Window();
    }

    @Setter
    @Getter
    public class Window {
        int sizeSeconds;
    }
}
