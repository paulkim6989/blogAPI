package com.example.blog.util;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.example.blog.model.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.example.blog.model.Apikey;
import com.example.blog.model.Header;
import com.example.blog.model.ResponseData;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Service
@Slf4j
public class RestClient {
    
    @Autowired
    private RestTemplateBuilder restTemplateBuilder; 
    
    /**
     * 
     * Statements
     *
     * @param <E>
     * @param <T>
     * @param restURL
     * @param config
     * @param method
     * @param request
     * @param classOfResponse
     * @param mediaType
     * @return
     */
    public <E, T> ResponseEntity<E> request(String restURL, Config config, HttpMethod method, T request, Class<E> classOfResponse, MediaType mediaType )
    {
        RestTemplate restTemplate = restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(4)).build();
        ResponseEntity<E> response                      = null;
        ResponseData resData   							= null;
        
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setAccept(Collections.singletonList(new org.springframework.http.MediaType("application","json")));
            
            if(config.getApikey()!=null && config.getApikey().size() > 0) {
            	for (Apikey a : config.getApikey()) {
            		log.info("Apikey -> {} : {}",a.getKey(),a.getValue());
            		headers.set(a.getKey(), a.getValue());
            	}
            }
            
            HttpEntity<?> requestEntity                 = null;
            if( request == null )
                requestEntity                           = new HttpEntity<T>(headers);
            else
                requestEntity                           = new HttpEntity<T>(request, headers);
            
            log.info("restURL -> {}",restURL);
            ResponseEntity<Map> responseEntity		= restTemplate.exchange(restURL, method, requestEntity, Map.class);
            Header header                           	= Header.builder().code(0).message("API 통신에 성공하였습니다.").build();
            resData 									= ResponseData.builder().header(header).body(responseEntity.getBody().get(config.getDataField())).build();
            response                                    = new ResponseEntity<E>((E)resData, HttpStatus.OK);
        
        }catch(HttpClientErrorException | HttpServerErrorException e) {
            log.info("err getStatusCode::::::::::{}",e.getStatusCode());
            log.info("err getResponseBodyAsString::::::::::{}",e.getResponseBodyAsString());
            
            if( e.getStatusCode() != HttpStatus.UNAUTHORIZED )
            {
                resData                                 = Util.JsonTobean(e.getResponseBodyAsString(), ResponseData.class);
            }
            else
            {
                Header header                           = Header.builder().code(2).message(e.getResponseBodyAsString()).build();                         
                resData                                 = ResponseData.builder().header(header).build();
            }
            
            response                                    = new ResponseEntity<E>((E)resData, e.getStatusCode());
        }catch(HttpStatusCodeException e) {
            log.info("err getStatusCode::::::::::{}",e.getStatusCode());
            log.info("err getResponseBodyAsString::::::::::{}", e.getResponseBodyAsString());
            
            if( e.getStatusCode() != HttpStatus.UNAUTHORIZED )
            {
                resData                                 = Util.JsonTobean(e.getResponseBodyAsString(), ResponseData.class);
            }
            else
            {
                Header header                           = Header.builder().code(2).message(e.getResponseBodyAsString()).build();                         
                resData                                 = ResponseData.builder().header(header).build();
            }
            
            response                                    = new ResponseEntity<E>((E)resData, e.getStatusCode());
        }catch(Exception e) {
            log.error("Exception::{}", e);
            Header header                               = Header.builder().code(3).message(e.getMessage()).build();                          
            resData                        				= ResponseData.builder().header(header).build();
            
            response                                    = new ResponseEntity<E>((E)resData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return response;
        
    }
}
