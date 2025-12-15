package com.proxy.example.gateway_api.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/** 
 * @author Roshan Kumar
 * @date   14-11-2025
 * @for    Learning purpose
 */

@Component
public class PreFilter extends ZuulFilter{
	private static Logger log= LoggerFactory.getLogger(PreFilter.class);

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		String requestId = UUID.randomUUID().toString();
		MDC.put("requestId", requestId);
		
		log.info("Pre filter=====>: ");
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		
		log.info("PreFilter: " +String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
		try {
			if(request.getContentType()!=null && request.getContentType().contains("multipart/form-data")) {
				log.info("File upload Request=====>: ");
			}else {
				log.info("Request=====>: " + String.format("request body %s", request.getReader().lines().reduce("", String::concat).replaceAll("\\s+", "").toString()));
			}
			getHeadersInfo(request);
		}catch (IOException e) {
			log.error("Exception Occurred : =====> {}", e.getMessage());
		}
		return null;
	}

	private Map<String, String> getHeadersInfo(HttpServletRequest request) {
		Map<String, String> map= new HashMap<String, String>();
		
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			
			log.info("Header=====>: "+key+" "+"["+value+"]");
			map.put(key, value);
		}
		return map;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

}
