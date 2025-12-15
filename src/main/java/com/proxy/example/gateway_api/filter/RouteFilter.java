package com.proxy.example.gateway_api.filter;

import java.net.URL;

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
public class RouteFilter extends ZuulFilter{
	private static Logger log= LoggerFactory.getLogger(RouteFilter.class);

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		
		String requestId = (String) ctx.get("requestId");
		if(requestId != null && MDC.get("requestId") == null) {
			MDC.put("requestId", requestId);
		}
		
		log.info("METHOD:====> " +request.getMethod());
		printSourceUrl(request);
		printDestinationUrl(ctx);
		
		return null;
	}

	private void printDestinationUrl(RequestContext ctx) {
		URL requestUrl = ctx.getRouteHost();
		
		if(ctx.getRequest().getQueryString()!=null) {
			log.info("Destination URL:===> "+ctx.getRouteHost() +"?"+ ctx.getRequest().getQueryString());
		}else {
			log.info("Destination URL:===> "+ctx.getRouteHost());
		}
		
		if(requestUrl!=null) {
			requestUrl=null;
		}
	}

	private void printSourceUrl(HttpServletRequest request) {
		StringBuffer requestUrl = new StringBuffer();
		requestUrl = request.getRequestURL();
		if(request.getQueryString()!=null) {
			requestUrl.append("?"+request.getQueryString());
		}
		log.info("Source URL:=====> " +requestUrl);
		
		if(requestUrl!=null) {
			requestUrl=null;
		}
	}

	@Override
	public String filterType() {
		return "route";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

}
