package com.glovodelivery.project.config;

import com.glovodelivery.project.config.properties.RsaKeyProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        RsaKeyProperties.class
})
public class BeanConfig {
}
