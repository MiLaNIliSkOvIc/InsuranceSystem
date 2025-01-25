package com.example.insurance_system.security;


import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpToHttpsRedirectConfig {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            TomcatServletWebServerFactory tomcat = (TomcatServletWebServerFactory) factory;
            tomcat.addAdditionalTomcatConnectors(createHttpConnector());
        };
    }

    private org.apache.catalina.connector.Connector createHttpConnector() {
        org.apache.catalina.connector.Connector connector = new org.apache.catalina.connector.Connector();
        connector.setScheme("http");
        connector.setPort(8080);  // HTTP port
        connector.setSecure(true);
        connector.setRedirectPort(443);  // HTTPS port
        return connector;
    }
}
