package com.sambi.app.rest;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
@ComponentScan
public class RestApplication {

    private static final Logger logger = LoggerFactory.getLogger(RestApplication.class);

    public static void main(String[] args) {
        String appHome = System.getProperty("app.home", ".");

        String logFileName = FilenameUtils.concat(appHome, "LOG"); 
        logFileName = FilenameUtils.concat(logFileName, "Logfile");
        MDC.put("logFileName", logFileName);

        logger.debug("Resolved logFileName: {}", logFileName);

        System.setProperty("spring.profiles.default", System.getProperty("spring.profiles.default", "test"));

        logger.debug("Starting REST web services");
        SpringApplication.run(RestApplication.class, args);
    }
}
