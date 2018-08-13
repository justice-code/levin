package org.eddy.rest;

import org.eddy.rest.injector.RestImportBeanDefinitionRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Import(RestImportBeanDefinitionRegistrar.class)
public class RestApplication {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Bean
    public RestTemplate restTemplate() {
        return restTemplateBuilder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }
}
