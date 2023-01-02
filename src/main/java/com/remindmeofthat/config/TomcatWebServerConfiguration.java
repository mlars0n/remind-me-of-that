package com.remindmeofthat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatWebServerConfiguration implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private static Logger logger = LoggerFactory.getLogger(TomcatWebServerConfiguration.class);

    //These values will be set for deployments but will default to http://localhost:8080 for local development

    //The HTTP protocol (https for any deployments past the local development one)
    @Value("${custom.http.protocol.scheme:http}")
    private String scheme;

    //This is for the hostname (default localhost)
    @Value("${custom.http.proxy.name:localhost}")
    private String proxyName;

    //This is for the port (local default for the port, proxy port is expected to be 443 in live deployments)
    @Value("${custom.http.proxy.port:8080}")
    private int proxyPort;

    //This is for the actual port that the server will listen on
    @Value("${custom.http.server.port:8080}")
    private int serverPort;

    /**
     * Method to customize the Tomcat configuration, called automatically within Spring Boot
     * See https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-embedded-web-servers.html
     * @param factory
     */
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        logger.debug("Configuring Tomcat webserver with values scheme=[{}], proxyName=[{}], proxyPort=[{}]", scheme, proxyName, proxyPort);

        // customize the factory here
        factory.addConnectorCustomizers(connector -> {
            connector.setScheme(scheme);
            connector.setProxyName(proxyName);
            connector.setProxyPort(proxyPort);
            connector.setPort(serverPort);
        });
    }
}
