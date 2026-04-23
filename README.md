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
* 이 서비스는 전국 축제 정보를 편리하게 탐색하고, 사용자 경험을 리뷰와 찜으로 관리할 수 있도록 하는 것을 목표로 합니다.
* 축제 데이터는 공공데이터포털 관광공사 API를 통해 가져오고, 서비스 DB에 저장하여 관리합니다.
* 축제 검색은 지역, 진행 상태, 월, 키워드, 내 위치 기반 조건을 지원합니다.
* 회원 인증은 JWT 기반으로 구현하였으며, Access Token과 Refresh Token을 분리하여 인증과 재발급을 처리합니다.
* 리뷰, 좋아요, 신고, 관리자 블라인드 처리 등 사용자 참여와 운영 관리를 위한 기능을 제공합니다.

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
        │    ├─ 회원가입
        │    ├─ 로그인
        │    ├─ JWT 인증
        │    ├─ 토큰 재발급
        │    └─ 마이페이지
        │
        ├─ Festival
        │    ├─ 축제 목록 조회
        │    ├─ 축제 상세 조회
        │    ├─ QueryDSL 기반 검색
        │    ├─ 주변 축제 조회
        │    └─ 공공데이터 API 동기화
        │
        ├─ Review
        │    ├─ 리뷰 작성
        │    ├─ 리뷰 조회
        │    ├─ 리뷰 수정
        │    ├─ 리뷰 삭제
        │    ├─ 리뷰 좋아요
        │    └─ 리뷰 신고
        │
        ├─ Bookmark
        │    ├─ 축제 찜 등록
        │    └─ 축제 찜 취소
        │
        ├─ Admin
        │    ├─ 회원 관리
        │    ├─ 신고 리뷰 관리
        │    └─ 축제 데이터 동기화 관리
        │
        └─ Global
             ├─ Security
             ├─ JWT
             ├─ Exception Handler
             ├─ Scheduler
             └─ WebMvc
```

> TODO: 시스템 아키텍처 이미지 추가

---

## 🎉 설치 및 실행 방법

### 1. Backend 리포지토리 클론

```bash
git clone https://github.com/prgrms-be-devcourse/NBE9-11-2-Team03.git
cd NBE9-11-2-Team03
```

### 2. 환경 설정

`src/main/resources/application.yaml` 또는 IntelliJ 실행 환경에 필요한 값을 설정합니다.

```yaml
jwt:
  secret: local-development-jwt-secret-key

api:
  public-data:
    key: 발급받은_공공데이터_API_KEY
    base-url: https://apis.data.go.kr/B551011/KorService2

file:
  upload-dir: uploads
```

### 3. H2 기준 실행

```bash
./gradlew bootRun
```

브라우저에서 접속합니다.

```text
http://localhost:8080
http://localhost:8080/h2-console
http://localhost:8080/swagger-ui/index.html
```

### 4. Docker MySQL 실행

```bash
docker compose up -d
```

MySQL 프로필로 실행합니다.

```bash
SPRING_PROFILES_ACTIVE=mysql ./gradlew bootRun
```

> Docker Compose의 MySQL 포트와 `application-mysql.yaml`의 DB 포트가 다를 경우 둘 중 하나를 맞춰야 합니다.

### 5. Frontend 실행

Frontend 리포지토리를 클론합니다.

```bash
git clone https://github.com/prgrms-be-devcourse/NBE9-11-2-Team03-frontend.git
cd NBE9-11-2-Team03-frontend
npm install
npm run dev
```

브라우저에서 접속합니다.

```text
http://localhost:3000
```

---

## 📂 프로젝트 구조

```text
src/
├─ main/
│  ├─ java/com/example/
│  │  ├─ FestivalApplication.java
│  │  ├
