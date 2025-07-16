package com.proxy.example.gateway_api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer{
	
	private static Logger logger = LogManager.getLogger(ServletInitializer.class);
	
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		logger.info("ServletInitializer Class");
		return application.sources(GatewayApiApplication.class);
	}

}
