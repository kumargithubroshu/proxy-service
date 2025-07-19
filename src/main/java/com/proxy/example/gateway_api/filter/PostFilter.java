package com.proxy.example.gateway_api.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class PostFilter extends ZuulFilter{
	private static Logger log= LoggerFactory.getLogger(PostFilter.class);

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		HttpServletResponse response= RequestContext.getCurrentContext().getResponse();
		log.info("PostFilter: " + String.format("response's content type is %s", response.getStatus()));
		log.info("url====>" +RequestContext.getCurrentContext().getRouteHost().getPath());
		if(RequestContext.getCurrentContext().getRouteHost()!=null
//				&& (RequestContext.getCurrentContext().getRouteHost().getPath().contentEquals("/pdf/generate") 
//				|| RequestContext.getCurrentContext().getRouteHost().getPath().contentEquals("/InvoiceUrl"))
				) {
			log.info("if");
			try {
				response.setCharacterEncoding(CharEncoding.UTF_8);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}else {
			log.info("else");
			RequestContext ctx = RequestContext.getCurrentContext();
			try (InputStream is = ctx.getResponseDataStream()){
				String resData = CharStreams.toString(new InputStreamReader(is, CharEncoding.UTF_8));
				log.info("Response Data = {}", resData);
				ctx.setResponseBody(resData);
			}catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

}
