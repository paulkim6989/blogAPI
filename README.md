# blogAPI
오픈 API를 이용한 "블로그 검색 서비스"

## API 명세

### 1. 블로그 검색 (/v1/search/blog)
  - #### Request 
    - ##### Parameter
      Name | Type | Description | Default Value | Required
      ---|---|---|---|---|
      query | String | 검색을 원하는 질의어 | | O
      apiName | String | 검색 오픈 서버 API명 (e.g) kakao, naver | kakao | X
      sort | String | 결과 문서 정렬 방식 (A - 정확도순, T - 최신순) | A | X
      page | Integer | 결과 페이지 번호 | 오픈 API의 page 기본값 | X
      size | Integer | 한 페이지에 보여질 문서 수 | 오픈 API의 size 기본값 | X    

  - #### Response 
    - ##### header
      - ##### code 
        - 0 : 성공
        - 1 : validation 오류
        - 2 : 권한 오류 (API KEY)
        - 3 : Internal 서버 오류
      - ##### message
        - 성공 및 오류 메세지

    - ##### Body
      - ##### resultList
        Name | Type | Description
        ---|---|---|
        title | String | 블로그 글 제목
        contents | String | 블로그 글 요약
        url | String | 블로그 글 URL
        blogname | String | 블로그의 이름
        datetime | Datetime | 블로그 글 작성시간    
    
  - #### 다중 검색 API 설정
    - ##### 다중 검색 API 관련 필요 정보 (application.yml 파일)
      - api > config 하단에 추가
      - API name (e.g kakao, naver)
      - name : API명
      - url : API URL
      - apikey : header에 추가할 apikey list 형태로 추가 (key : value)        
      - param : API request parameter 정보
        - query: "검색 키워드" 관련 정보
          - name: API에서 "검색 키워드" param 명
        - sort: "정렬순서" 관련 정보
          - name: API에서 "정렬순서" param 명
          - A: API에서 정확도순으로 정렬할 때 보내야 하는 data (예 : kakao에서는 accuracy)
          - T: API에서 최신순으로 정렬할 때 보내야 하는 data (예 : kakao에서는 recency)
        - page: "페이지 번호" 관련 정보
          - name: API에서 "페이지 번호" param 명
          - max: "페이지 번호" 최대값
        - size: "결과 레코드 수" 관련 정보
          - name: API에서 "결과 레코드 수" param 명
          - max: "결과 레코드 수" 최대값
      - dataField: Response json에서 데이터 키값 (예 : kakao에서는 documents)
      - response: 결과 데이터 관련 필요 정보
        - title: "제목"을 나타내는 데이터 header
        - contents: "내용"을 나타내는 데이터 header
        - url: "해당 블로드 게시글 URL"을 나타내는 데이터 header
        - blogname: "블로그명"을 나타내는 데이터 header
        - datetime: "작성시간"을 나타내는 데이터 header
  
    - ##### application.yml 파일 예시
      ```yml
        api:
          config:
            kakao:
              name: kakao 
              url: https://dapi.kakao.com/v2/search/blog
              apikey:
                - key: Authorization
                  value: KakaoAK 3966cb8befe5e16c3f0b62d153eebcd2
              param:
                query:
                  name: query
                sort:
                  name: sort
                  A: accuracy
                  T: recency
                page:
                  name: page
                  max: 50
                size:
                  name: size
                  max: 50
              dataField: documents
              response:
                title: title
                contents: contents
                url: url
                blogname: blogname
                datetime: datetime

            naver:      
              name: naver
              url: https://openapi.naver.com/v1/search/blog.json  
              apikey: 
                - key: X-Naver-Client-Id
                  value: JKGScC4IEgDbbu8Dn0Ci
                - key: X-Naver-Client-Secret
                  value: 7jgV9EbcGR
              param:
                query:
                  name: query
                sort:
                  name: sort
                  A: sim
                  T: date
                page:
                  name: start
                  max: 1000
                size:
                  name: display
                  max: 100
              dataField: items
              response:
                title: title
                contents: description
                url: link
                blogname: bloggername
                datetime: postdate
        ```
        
    - ##### 다중 검색 API 장애 시 통신 방식
      - apiName으로 전달 받은 API명 있으면 해당 API로 통신 (없을 경우 기본 kakao API 조회)
      - API 오류가 발생할 경우 → 등록된 다른 API로 통신 (순서는 yml파일 등록한 순서대로)
        - API 통신 성공할 경우 → 결과 리턴하고 종료
        - API 오류가 발생할 경우 → 등록된 또 다른 API로 통신   


### 2. 인기 검색어 목록 (/v1/search/popularKeyword)
  - #### Request 
    - ##### Parameter : 없음
  - #### Response
	- ##### header
		- ##### code 
			- 0 : 성공
			- 3 : Internal 서버 오류
		- ##### message
			- 성공 및 오류 메세지
    - ##### Body
      - ##### resultList
        Name | Type | Description
        ---|---|---|
        rank | Integer | 많이 검색된 순위
        query | String | 검색된 키워드
        cnt | Integer | 검색된 횟수

## API 설명
        
### 1. 실행환경
  - #### jdk 버전 : 17.0.2
  - #### server port : 8081
  - #### API 호출 URL
    - ##### 블로그 검색 API : http://localhost:8081/v1/search/blog
    - ##### 인기검색어 목록 조회 API : http://localhost:8081/v1/search/popularKeyword
  - #### jar 다운로드 링크
    - ##### [Executable Jar] (https://github.com/paulkim6989/blogAPI/blob/main/blog-0.0.1-SNAPSHOT.jar)

### 2. API 특징
  - #### 다중 검색 API 구성
  	- ##### config 정보 파일(yml)로 구성 (변경, 추가 가능)
	- ##### response data header 통일
	- ##### 특정 API 장애 시 타 API 호출 가능
  - #### Header 정보 코드 및 체계화
  - #### 비동기 처리를 통한 대량 트래픽 처리
      
### 3. 테스트 케이스
  - #### 테스트 케이스 링크
  	- ##### [Test Case] (https://github.com/paulkim6989/blogAPI/blob/main/blogAPI_TestCase_0921.xlsx)

### 4. 외부 라이브러리
  - #### gson
  	- ##### json을 bean형태로 변경해주는 util 메소드에서 사용 (Util.java > JsonTobean)