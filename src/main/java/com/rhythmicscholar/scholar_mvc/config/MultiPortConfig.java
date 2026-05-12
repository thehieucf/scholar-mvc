package com.rhythmicscholar.scholar_mvc.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiPortConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> connectorCustomizer() {
        return (tomcat) -> {
            // Thêm cổng 8081 cho Admin
            Connector adminConnector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
            adminConnector.setPort(8081);
            tomcat.addAdditionalTomcatConnectors(adminConnector);
        };
    }
}
