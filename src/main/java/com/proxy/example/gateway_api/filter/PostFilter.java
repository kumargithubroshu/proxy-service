package com.proxy.example.gateway_api.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/** 
 * @author Roshan Kumar
 * @date   14-11-2025
 * @for    Learning purpose
 */

@Component
public class PostFilter extends ZuulFilter{
	private static Logger log= LoggerFactory.getLogger(PostFilter.class);
	
	private static final List<String> FILE_ENDPOINTS = Arrays.asList("/pdf/generate", "/InvoiceUrl");

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 1;
	}
	
	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletResponse response = ctx.getResponse();
		
		//Ensure MDC context is maintained from Prefilter
		String requestId = (String) ctx.get("requestId");
		if(requestId != null && MDC.get("requestId") == null) {
			MDC.put("requestId", requestId);
		}
		
		log.info("PostFilter: response status = {}", response.getStatus());
		
		URL routeHost = ctx.getRouteHost();
		String path = routeHost != null ? routeHost.getPath() : "";
		
		log.info("URL Path : {}", path);
		
		// check if the request is for file/pdf-type endpoints
		boolean isFileEndPoints = FILE_ENDPOINTS.stream().anyMatch(path::endsWith);
		
		if(isFileEndPoints) {
			log.info("skipping response body logging for file endpoints: {}", path);
			response.setCharacterEncoding(CharEncoding.UTF_8);
		}else {
			try (InputStream is = ctx.getResponseDataStream()){
				if(is != null) {
					String responseBody = CharStreams.toString(new InputStreamReader(is, CharEncoding.UTF_8));
					log.info("response data = {}", responseBody);
					ctx.setResponseBody(responseBody);
				}
				}catch (IOException e) {
					log.error("error reading response stream", e);
			}
		}
		
		return null;
	}
}


//		HttpServletResponse response= RequestContext.getCurrentContext().getResponse();
//		log.info("PostFilter: " + String.format("response's content type is %s", response.getStatus()));
//		log.info("url====>" +RequestContext.getCurrentContext().getRouteHost().getPath());
//		if(RequestContext.getCurrentContext().getRouteHost()!=null
////				&& (RequestContext.getCurrentContext().getRouteHost().getPath().contentEquals("/pdf/generate") 
////				|| RequestContext.getCurrentContext().getRouteHost().getPath().contentEquals("/InvoiceUrl"))
//				) {
//			log.info("if");
//			try {
//				response.setCharacterEncoding(CharEncoding.UTF_8);
//			}catch(Exception e) {
//				e.printStackTrace();
//			}
//		}else {
//			log.info("else");
//			RequestContext ctx = RequestContext.getCurrentContext();
//			try (InputStream is = ctx.getResponseDataStream()){
//				String resData = CharStreams.toString(new InputStreamReader(is, CharEncoding.UTF_8));
//				log.info("Response Data = {}", resData);
//				ctx.setResponseBody(resData);
//			}catch(IOException ioe) {
//				ioe.printStackTrace();
//			}
//		}
//		return null;
//	}