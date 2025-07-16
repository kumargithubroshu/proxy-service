package com.proxy.example.gateway_api.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletResponse;

@Component
public class ErrorFilter extends ZuulFilter{
	private static Logger log= LoggerFactory.getLogger(ErrorFilter.class);

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public String filterType() {
		return "error";
	}

	@Override
	public int filterOrder() {
		return 1;
	}
	
	@Override
	public Object run() {
		HttpServletResponse response = RequestContext.getCurrentContext().getResponse();
		log.info("ErrorFilter: "+ String.format("response status is %d", response.getStatus()));
		
		RequestContext ctx= RequestContext.getCurrentContext();
		
		String responseBody = ctx.getResponseBody();
		log.info("Error occurred, Response = {} ", responseBody);
		return null;
	}
}
