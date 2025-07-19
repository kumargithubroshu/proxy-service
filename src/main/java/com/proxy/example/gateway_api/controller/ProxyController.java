package com.proxy.example.gateway_api.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proxy")
public class ProxyController {
	private Logger log=LoggerFactory.getLogger(getClass());
	
	@Autowired
	private Environment env;
	
	public ResponseEntity<?> craeteAudit(HttpServletRequest request, String path) throws Exception{
		log.info("ProxyController getConfig path====>" +path);
		
		if(path != null && path.length() > 0) {
			String getUrlByPath = getUrlByPath("/" +path+ "/**");
			if(getUrlByPath!=null && getUrlByPath.length()>0) {
				log.info("ProxyController getConfig response actual path====>" +getUrlByPath);
				return ResponseEntity.status(HttpStatus.OK).body(getUrlByPath);
			}else {
				log.info("ProxyController getConfig response actual path===> Not found");
				return ResponseEntity.status(HttpStatus.OK).body("Not found");
			}
		}else {
			log.info("ProxyController getConfig response actual path=====> Empty input path");
			return ResponseEntity.status(HttpStatus.OK).body("Empty input path");
		}
			
	}

	private String getUrlByPath(String inputPath) {
		String returnValue=null;
		MutablePropertySources propertySources = null;
		Map<String, String> pathToUrlMap = null;
		
		try {
			propertySources = ((AbstractEnvironment) env).getPropertySources();
			pathToUrlMap =new HashMap<String, String>();
			
			for(PropertySource<?> source : propertySources) {
				if(source.getSource() instanceof Map) {
					Map<String, Object> map = (Map<String, Object>) source.getSource();
					for(String key :map.keySet()) {
						if(key.startsWith("zuul.routes.") && key.endsWith(".path")) {
							String routeName = key.substring("zuul.routes.".length(), key.length() - ".path".length());
							String path = env.getProperty(key);
							String urlKey="zuul.routes."+routeName+".url";
							String url= env.getProperty(urlKey);
							if(path !=null && url != null) {
								pathToUrlMap.put(path, url);
							}
						}
					}
				}
			}
			
			returnValue = pathToUrlMap.get(inputPath);
		}catch (Exception e) {
			log.error("Exception Occurred : " +e.getMessage());
		}finally {
			if(propertySources!=null) {
				propertySources=null;
			}
			if(pathToUrlMap!=null) {
				pathToUrlMap=null;
			}
		}
		return returnValue;
	}

}
