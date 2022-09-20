# blogAPI
오픈 API를 이용한 "블로그 검색 서비스"

## API 명세

### 1. 블로그 검색 (/v1/search/blog)
  - Request Parameter
    - query (String) : 검색을 원하는 질의어, Required(O)
    - apiName (String) : 검색 오픈 서버 모듈명, 기본값-kakao, (e.g) naver, Required(X)
    - sort (String) : 결과 문서 정렬 방식, A - 정확도순, T - 최신순, 기본값-A, Required(X)
    - page (Integer) : 결과 페이지 번호, 기본값은 오픈 API의 기본값, Required(X)
    - size (Integer) : 한 페이지에 보여질 문서 수, 기본값은 오픈 API의 기본값, Required(X)
    
    Name | Type | Description | Default Value | Required
    ---|---|---|---|---|
    query | String | 검색을 원하는 질의어 | | O
    apiName | String | 검색 오픈 서버 모듈명 (e.g) kakao, naver | kakao | X
    sort | String | 결과 문서 정렬 방식 (A - 정확도순, T - 최신순) | A | X
    page | Integer | 결과 페이지 번호 | 오픈 API의 page 기본값 | X
    size | Integer | 한 페이지에 보여질 문서 수 | 오픈 API의 size 기본값 | X
    

  - Response header
    - code 
      - 0 : 성공
      - 1 : validation 오류
      - 2 : 권한 오류 (API KEY)
      - 3 : Internal 서버 오류
    - message
      - 성공 및 오류 메세지
  
  - Response Body : resultList
    - title (String) : 블로그 글 제목
    - contents (String) : 블로그 글 요약
    - url (String) : 블로그 글 URL
    - blogname (String) : 블로그의 이름
    - thumbnail (String) : 검색 시스템에서 추출한 대표 미리보기 이미지 URL
    - datetime (Datetime) : 블로그 글 작성시간
    
  - 오픈 API 서버 통신 방식
    - apiName으로 전달 받은 모듈명 있으면 해당 모듈로 통신 (없을 경우 기본 kakao API 조회)
    - API 오류가 발생할 경우 등록된 다른 모듈 API로 통신
      - API 통신 성공할 경우 결과 리턴하고 종료
      - API 오류가 발생할 경우 등록된 또 다른 API로 통신   


### 2. 인기검색어 목록 (/v1/search/popularKeyword)
  - Request Parameter : 없음
  - Response Body
    - rank (Integer) : 많이 검색된 순위
    - query (String) : 검색된 키워드
    - cnt (Integer) : 검색된 횟수
