## 🎆 오늘의 축제
* 전국 축제 정보를 한눈에 확인하고, 리뷰와 찜을 통해 나만의 축제 경험을 관리하는 서비스입니다.

---

## 🥤 프로젝트 소개
* 공공데이터포털 관광공사 API를 활용하여 전국 축제 정보를 제공합니다.
* 사용자는 축제 목록/상세 조회, 지역/상태/월별 검색, 내 주변 축제 조회를 할 수 있습니다.
* 로그인한 사용자는 축제를 찜하거나 리뷰를 작성하고, 마이페이지에서 활동 내역을 확인할 수 있습니다.
* 관리자는 축제 데이터 동기화, 신고 리뷰 관리, 신고 회원 관리 기능을 사용할 수 있습니다.

---

## 📚 프로젝트 개요
* 축제 정보를 수동으로 찾기 어려운 사용자를 위해 축제 탐색, 리뷰, 찜 기능을 제공합니다.
* 외부 공공데이터 API의 축제 목록/상세 정보를 DB에 동기화하여 서비스 데이터로 관리합니다.
* JWT 기반 인증을 사용하며, Access Token과 Refresh Token을 분리해 인증과 재발급을 처리합니다.
* QueryDSL을 활용하여 축제 검색 조건을 동적으로 처리합니다.
* 리뷰 신고, 관리자 블라인드 처리, 회원 탈퇴 등 서비스 운영에 필요한 관리 기능을 포함합니다.

---

## ⚙️ 기술 스택

### 🔙 Backend
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.5.13-6DB33F?logo=springboot&logoColor=white)
![Spring Web](https://img.shields.io/badge/Spring%20Web-6DB33F?logo=spring)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?logo=spring)
![Gradle](https://img.shields.io/badge/Gradle-Kotlin%20DSL-02303A?logo=gradle)

### 🔐 Security
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?logo=jsonwebtokens&logoColor=white)
![Validation](https://img.shields.io/badge/Jakarta%20Validation-FF6F00)

### 🗄 Database
![H2](https://img.shields.io/badge/H2-Database-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)

### 🔎 Query
![QueryDSL](https://img.shields.io/badge/QueryDSL-0769AD)

### 📄 API 문서화
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?logo=swagger&logoColor=black)

### 🐳 Infra
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-2496ED?logo=docker&logoColor=white)

### 🌐 Frontend
![Next.js](https://img.shields.io/badge/Next.js-black?logo=nextdotjs)
![React](https://img.shields.io/badge/React-61DAFB?logo=react&logoColor=black)

---

## 🕸️ 아키텍처

```text
Client
  └─> Spring Boot API Server
        ├─ Auth / Member
        ├─ Festival
        ├─ Review
        ├─ Bookmark
        ├─ Admin
        └─ Global Security / Exception / Scheduler
              ├─ H2 / MySQL
              └─ 공공데이터포털 관광공사 API
TODO: 시스템 아키텍처 이미지 추가

🎉 설치 및 실행 방법
1. Backend 리포지토리 클론
git clone https://github.com/prgrms-be-devcourse/NBE9-11-2-Team03.git
cd NBE9-11-2-Team03
2. 환경 설정
src/main/resources/application.yaml 또는 IntelliJ 실행 환경에 필요한 값을 설정합니다.

jwt:
  secret: local-development-jwt-secret-key

api:
  public-data:
    key: 발급받은_공공데이터_API_KEY
    base-url: https://apis.data.go.kr/B551011/KorService2

file:
  upload-dir: uploads
3. H2 기준 실행
./gradlew bootRun
브라우저에서 접속:

http://localhost:8080
http://localhost:8080/h2-console
http://localhost:8080/swagger-ui/index.html
4. Docker MySQL 실행
docker compose up -d
MySQL 프로필 실행 시:

SPRING_PROFILES_ACTIVE=mysql ./gradlew bootRun
docker-compose 포트와 application-mysql.yaml의 DB 포트가 다르면 둘 중 하나를 맞춰야 합니다.

5. Frontend 실행
Frontend 리포지토리:

git clone https://github.com/prgrms-be-devcourse/NBE9-11-2-Team03-frontend.git
cd NBE9-11-2-Team03-frontend
npm install
npm run dev
브라우저에서 접속:

http://localhost:3000
📂 프로젝트 구조
src/
├─ main/
│  ├─ java/com/example/
│  │  ├─ FestivalApplication.java
│  │  ├─ domain/
│  │  │  ├─ admin/          # 관리자 회원/리뷰 관리
│  │  │  ├─ bookmark/       # 축제 찜
│  │  │  ├─ festival/       # 축제 조회, 검색, 동기화
│  │  │  ├─ member/         # 회원, 인증, 마이페이지
│  │  │  ├─ review/         # 리뷰 작성/조회/수정/삭제
│  │  │  ├─ reviewlike/     # 리뷰 좋아요
│  │  │  └─ reviewreport/   # 리뷰 신고
│  │  └─ global/
│  │     ├─ config/
│  │     ├─ entity/
│  │     ├─ exception/
│  │     ├─ exceptionHandler/
│  │     ├─ init/
│  │     ├─ jwt/
│  │     ├─ response/
│  │     ├─ rsData/
│  │     ├─ scheduler/
│  │     ├─ security/
│  │     └─ webMvc/
│  └─ resources/
│     ├─ application.yaml
│     ├─ application-mysql.yaml
│     └─ application-test.yaml
└─ test/
   └─ java/com/example/
🧩 주요 기능
👤 Member / Auth
회원가입
로그인
JWT Access Token 발급
Refresh Token HttpOnly Cookie 저장
Access Token 재발급
로그아웃
Access Token blacklist 처리
회원 탈퇴 및 탈퇴 회원 비식별화
마이페이지 조회
내가 작성한 리뷰 조회
내가 찜한 축제 조회
🎆 Festival
축제 목록 조회
축제 상세 조회
지역/월/상태/키워드 기반 검색
내 위치 기반 주변 축제 조회
공공데이터 API 축제 목록 동기화
축제 상세 정보 보강
동기화 실패/미처리 대상 관리
✍️ Review
축제 리뷰 작성
축제별 리뷰 목록 조회
리뷰 수정
리뷰 삭제
리뷰 좋아요
리뷰 신고
🔖 Bookmark
축제 찜 등록
축제 찜 취소
마이페이지 찜 목록 조회
🛠 Admin
회원 목록 조회
신고 누적 회원 조회
신고 리뷰 조회
리뷰 블라인드 처리
관리자 회원 탈퇴 처리
축제 데이터 동기화 실행
축제 상세 보강 실행
축제 동기화 상태 조회
🔐 인증 흐름
로그인
  └─> 아이디/비밀번호 검증
        └─> Access Token 발급
        └─> Refresh Token 발급
        └─> Refresh Token DB 저장/갱신
        └─> Refresh Token HttpOnly Cookie 저장

API 요청
  └─> Authorization: Bearer {Access Token}
        └─> Spring Security Filter에서 JWT 검증
              └─> 유효한 토큰 → 요청 처리
              └─> 만료/유효하지 않은 토큰 → 401 응답

토큰 재발급
  └─> /api/auth/reissue 요청
        └─> Refresh Token Cookie 검증
              └─> 성공 → 새 Access Token 발급
              └─> 실패 → 재로그인 필요

로그아웃
  └─> Refresh Token 비활성화
  └─> Access Token blacklist 저장
  └─> Refresh Token Cookie 삭제
🗄️ ERD
TODO: ERD 이미지 추가

📄 API 문서
Swagger 접속:

http://localhost:8080/swagger-ui/index.html
Auth API
기능	Method	URL
회원가입	POST	/api/auth/signup
로그인	POST	/api/auth/login
토큰 재발급	POST	/api/auth/reissue
로그아웃	POST	/api/auth/logout
Festival API
기능	Method	URL
축제 목록/검색	GET	/api/festivals
축제 상세	GET	/api/festivals/{id}
주변 축제 조회	GET	/api/festivals/nearby
MyPage API
기능	Method	URL
내 정보 조회	GET	/api/users/me
내가 쓴 리뷰 조회	GET	/api/users/me/reviews
내가 찜한 축제 조회	GET	/api/users/me/bookmarks
회원 탈퇴	DELETE	/api/users/me/withdraw
Review API
기능	Method	URL
리뷰 작성	POST	/api/festivals/{festivalId}/reviews
리뷰 목록 조회	GET	/api/festivals/{festivalId}/reviews
리뷰 수정	PATCH	/api/reviews/{reviewId}
리뷰 삭제	DELETE	/api/reviews/{reviewId}
리뷰 좋아요	POST	/api/reviews/{reviewId}/like
리뷰 좋아요 취소	DELETE	/api/reviews/{reviewId}/like
리뷰 신고	POST	/api/reviews/{reviewId}/reports
Bookmark API
기능	Method	URL
축제 찜	POST	/api/festivals/{festivalId}/bookmark
축제 찜 취소	DELETE	/api/festivals/{festivalId}/bookmark
Admin API
기능	Method	URL
회원 목록 조회	GET	/api/admin/members
신고 회원 조회	GET	/api/admin/members/reported
신고 리뷰 조회	GET	/api/admin/reviews/reported
리뷰 블라인드 처리	PATCH	/api/admin/reviews/{reviewId}/status
회원 강제 탈퇴	PATCH	/api/admin/members/{memberId}/withdraw
축제 목록+상세 동기화	POST	/api/admin/festivals/sync-and-enrich
축제 목록 동기화	POST	/api/admin/festivals/sync-list
미처리 상세 보강	POST	/api/admin/festivals/enrich-pending
단건 상세 보강	POST	/api/admin/festivals/{contentId}/enrich
동기화 상태 조회	GET	/api/admin/festivals/sync-status
🧪 테스트
./gradlew test
주요 테스트 범위:

Auth API
Festival API
Festival 동기화/재시도
QueryDSL 검색
Bookmark
Review
Review Like
Review Report
Admin
Global Exception
🎬 프로젝트 기능 구현 영상
TODO: 기능 시연 GIF 추가

주요 기능
축제 목록/상세 조회
지역/상태/월별 축제 검색
내 주변 축제 조회
회원가입/로그인/로그아웃
축제 찜 등록/취소
리뷰 작성/수정/삭제
리뷰 좋아요/신고
관리자 신고 리뷰 관리
관리자 축제 데이터 동기화
🧭 와이어프레임
와이어프레임: Figma
📌 Commit Message Convention
type	description
feat	새로운 기능 추가
fix	버그 수정
docs	문서 수정
style	코드 포맷팅, 세미콜론 누락 등 코드 변경 없음
refactor	코드 리팩토링
test	테스트 코드 추가 및 수정
chore	빌드 설정, 패키지 매니저 설정 등 기타 작업
👨‍💻 팀원 소개
이름	GitHub	역할
TODO	TODO	Festival 도메인
TODO	TODO	Member/Auth 도메인
TODO	TODO	Review 도메인
TODO	TODO	Bookmark 도메인
TODO	TODO	Admin 도메인
