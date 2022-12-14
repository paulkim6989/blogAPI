package com.example.blog.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.blog.model.ApiRequest;
import com.example.blog.model.Config;
import com.example.blog.model.Header;
import com.example.blog.model.History;
import com.example.blog.model.ResponseData;
import com.example.blog.repository.HistoryRepository;
import com.example.blog.util.RestClient;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/v1/search")
@ConfigurationProperties(prefix="api")
public class BlogController {
	
	private Map<String,Config> configMap;
	
	public void setConfig(final Map<String,Config> configMap) {
       this.configMap = configMap;
    }
	
	private static final String defaultApiName = "kakao"; // apiName 없을 때 기본값
	
	@Autowired
	private HistoryRepository historyRepository;
	
	@Autowired
	private RestClient restClient;

	/*
	 * blog 검색 API
	 */
	@RequestMapping("/blog")
	@ResponseBody
	public ResponseEntity<ResponseData> SearchBlog(@RequestParam(value="query", required = false) String query,
									@RequestParam(value="sort", required = false) String sort,
									@RequestParam(value="page", required = false) Integer page,
									@RequestParam(value="size", required = false) Integer size,
									@RequestParam(value="apiName", required = false, defaultValue=defaultApiName) String apiName) throws Exception {
		ResponseEntity<ResponseData> response = null;
		try {

			ApiRequest request = ApiRequest.builder().query(query).sort(sort).page(page).size(size).apiName(apiName).build(); // Request parameter 객체화
			response = this.validCheck(request); // Request parameter Validation Check
	
			if (response.getBody().getHeader().getCode() == 0) {
					response = this.callApi(request); // Open API Call
					if (response.getBody().getHeader().getCode() == 0) {
						// 검색 이력 저장
						History history = History.builder().query(request.getQuery()).build();
						historyRepository.save(history);
					} else {
						// 실패하였을 경우 다른 검색 API 추가로 조회
						for(String key : configMap.keySet()) {
							if (!key.equals(request.getApiName())) {
								response = this.callApi(request); // Open API Call
								if (response.getBody().getHeader().getCode() == 0) {
									// 검색 이력 저장
									History history = History.builder().query(request.getQuery()).build();
									historyRepository.save(history);
									break;
								}
							}
						}
					}
	
			}

		} catch (Exception e) {
			log.error("Exception::{}", e);
			Header header = Header.builder().code(3).message(e.getMessage()).build();
			ResponseData resData = ResponseData.builder().header(header).build();
			response = new ResponseEntity<ResponseData>(resData, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return response;
	}

	/*
	 * 인기검색어 목록 조회 API
	 */
	@RequestMapping("/popularKeyword")
	@ResponseBody
    public ResponseEntity<ResponseData> searchPopularKeyword() {
		ResponseEntity<ResponseData> response = null;
		try {
			List<History> searchPopularKeyword = historyRepository.searchPopularKeyword();
			Map<String,Object> resultMap = new HashMap<String,Object>();
			resultMap.put("resultList", searchPopularKeyword);
			Header header = Header.builder().code(0).message("조회에 성공하였습니다.").build();
			ResponseData resData = ResponseData.builder().header(header).body(resultMap).build();
			response = new ResponseEntity<ResponseData>(resData, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception::{}", e);
			Header header                               = Header.builder().code(3).message(e.getMessage()).build();
			ResponseData resData                        = ResponseData.builder().header(header).build();
			response                                    = new ResponseEntity<ResponseData>(resData, HttpStatus.INTERNAL_SERVER_ERROR);
		}
        return response;
    }

	/*
	 * API 호출 메소드 (yml에 정의된 config 정보 기반으로 호출)
	 */
	@Async
	public ResponseEntity<ResponseData> callApi(ApiRequest request) {

		Config config = configMap.get(request.getApiName());
		String url = config.getUrl();
		Map<String,Map<String,String>>param = config.getParam();
		UriComponentsBuilder reqUrl = UriComponentsBuilder.fromHttpUrl(url);
		reqUrl.queryParam(param.get("query").get("name"), request.getQuery());
		if (request.getSort() != null && param.get("sort").get(request.getSort()) !=null) reqUrl.queryParam(param.get("sort").get("name"), param.get("sort").get(request.getSort()));
		if (request.getPage() != null) reqUrl.queryParam(param.get("page").get("name"), request.getPage());
		if (request.getSize() != null) reqUrl.queryParam(param.get("size").get("name"), request.getSize());
		return restClient.request(reqUrl.build().toUriString(), config, HttpMethod.GET, null, ResponseData.class, MediaType.APPLICATION_JSON);
    }

	/*
	 * Request Parameter Validation Check 메소드
	 */
	public ResponseEntity<ResponseData> validCheck(ApiRequest request) {
		Config config = configMap.get(request.getApiName());
		Map<String,Map<String,String>>param = config.getParam();
		Header header = Header.builder().code(0).message("Validation Check 정상").build();

		if (request.getQuery() == null ) {
			header = Header.builder().code(1).message("검색어(query)는 필수값 입니다.").build();
		} else if (request.getQuery().trim().isEmpty()) {
			header = Header.builder().code(1).message("검색어(query)는 값이 반드시 있어야 합니다.").build();
		} else if (request.getSort() != null && !"A".equals(request.getSort()) && !"T".equals(request.getSort())) {
			header = Header.builder().code(1).message("정렬 순서(sort)를 정확하게 입력하세요.").build();
		} else if (request.getPage() != null && request.getPage() > Integer.parseInt(param.get("page").get("max"))) {
			header = Header.builder().code(1).message("결과 페이지 번호(page)는 " + param.get("page").get("max") + "보다 같거나 작아야 합니다.").build();
		} else if (request.getSize() != null && request.getSize() > Integer.parseInt(param.get("size").get("max"))) {
			header = Header.builder().code(1).message("한 페이지에 보여질 문서 수(size)는 "+param.get("size").get("max")+"보다 같거나 작아야 합니다.").build();
		}

		ResponseData resData = ResponseData.builder().header(header).build();
		return new ResponseEntity<ResponseData>(resData, HttpStatus.OK);
	}

}
