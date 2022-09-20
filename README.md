# blogAPI
오픈 API를 이용한 "블로그 검색 서비스"

API 명세

1. 블로그 검색 (/v1/search/blog)
  - Request Parameter
    - query (String) : 검색을 원하는 질의어, Required(O)
    - sort (String) : 결과 문서 정렬 방식, A - 정확도순, T - 최신순, Required(X)
    - page (Integer) : 결과 페이지 번호, Required(X)
    - size (Integer) : 한 페이지에 보여질 문서 수, Required(X)

  - Response header
    - code 
      - 0 : 성공
      - 1 : Validation 오류
      - 2 : API KEY 오류
      - 3 : 서버 오류
    - message
      - 성공 및 오류 메세지
  
  - Response Body : resultList
    - title (String) : 블로그 글 제목
    - contents (String) : 블로그 글 요약
    - url (String) : 블로그 글 URL
    - blogname (String) : 블로그의 이름
    - thumbnail (String) : 검색 시스템에서 추출한 대표 미리보기 이미지 URL
    - datetime (Datetime) : 블로그 글 작성시간


2. 인기검색어 목록 (/v1/search/popularKeyword)
  - Request Parameter : 없음
  - Response Body
    - rank (Integer) : 많이 검색된 순위
    - query (String) : 검색된 키워드
    - cnt (Integer) : 검색된 횟수
