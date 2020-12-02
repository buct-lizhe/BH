package com.bh.faultcode.utils;

import com.bh.faultcode.controller.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpClient {
    public static final Logger logger = LoggerFactory.getLogger(MainController.class);
    public String Client(String url, HttpMethod method, MultiValueMap<String, String> params) {
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response1 = null;
        try {
            response1 = template.getForEntity(url, String.class);
        }
        catch ( Exception e ){
            logger.error("http请求错误，无法访问到{}的内容   ",url , e);
        }
        return response1.getBody();
    }
}