package com.example.blog.util;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private Config globalConfig;
    
    /**
     * 
     * 블로그 오픈 API Rest 통신 메소드
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
    	this.globalConfig = config;
        RestTemplate restTemplate = restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(4)).build();
        ResponseEntity<E> response                      = null;
        Header header 	 								= null;
        ResponseData resData   							= null;
        
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setAccept(Collections.singletonList(new MediaType("application","json")));
            
            // apikey data header에 추가
            if(config.getApikey()!=null && config.getApikey().size() > 0) {
            	for (Apikey a : config.getApikey()) {
            		headers.set(a.getKey(), a.getValue());
            	}
            }
            
            HttpEntity<?> requestEntity                 = null;
            if( request == null )
                requestEntity                           = new HttpEntity<T>(headers);
            else
                requestEntity                           = new HttpEntity<T>(request, headers);
            
            ResponseEntity<Map> responseEntity			= restTemplate.exchange(restURL, method, requestEntity, Map.class);
            List<Map<String,String>> list 				= (List<Map<String,String>>)responseEntity.getBody().get(config.getDataField());
            
            Map<String,Object> resultMap = new HashMap<String,Object>();
			resultMap.put("resultList", list.stream().map(this::translate).collect(Collectors.toList())); // json header 통일 
            
            header 										= Header.builder().code(0).message("API 통신에 성공하였습니다.").build();
            resData 									= ResponseData.builder().header(header).body(resultMap).build();
            response 									= new ResponseEntity<E>((E)resData, HttpStatus.OK);
        
        }catch(HttpClientErrorException | HttpServerErrorException e) {
            log.info("err getStatusCode::::::::::{}",e.getStatusCode());
            log.info("err getResponseBodyAsString::::::::::{}",e.getResponseBodyAsString());
            
            if( e.getStatusCode() != HttpStatus.UNAUTHORIZED )
            {
                resData                                 = Util.JsonTobean(e.getResponseBodyAsString(), ResponseData.class);
            }
            else
            {
                header   		                        = Header.builder().code(2).message(e.getResponseBodyAsString()).build();                         
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
                header    		                       	= Header.builder().code(2).message(e.getResponseBodyAsString()).build();                         
                resData                                 = ResponseData.builder().header(header).build();
            }
            
            response                                    = new ResponseEntity<E>((E)resData, e.getStatusCode());
        }catch(Exception e) {
            log.error("Exception::{}", e);
            header     			                        = Header.builder().code(3).message(e.getMessage()).build();                          
            resData                        				= ResponseData.builder().header(header).build();
            
            response                                    = new ResponseEntity<E>((E)resData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return response;
        
    }
    
    /*
     * 오픈 소스 결과 데이터 헤더를 하나로 변환해주는 메소드 (translate)
     * @param map
     */
    public Map<String, String> translate(Map<String, String> map) {
        HashMap<String, String> translatedMap = new HashMap<String, String>();
        Map<String, String> responseMap = globalConfig.getResponse();
		for (String key : responseMap.keySet()) {
			translatedMap.put(key, map.get(responseMap.get(key)));
		}
        return translatedMap;
    }
}
