# 🏥 Welfaring Backend

의료혜택을 찾아주는 웹사이트의 백엔드 API 서버입니다.

## 🚀 기능

- 사용자 조건 기반 혜택 매칭
- OpenAI를 활용한 AI 요약문 생성
- PostgreSQL 데이터베이스 연동
- RESTful API 제공

## 🛠️ 기술 스택

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Data JPA**
- **PostgreSQL**
- **OpenAI API**
- **Gradle**
- **Docker**

## 📋 API 엔드포인트

### POST /api/match/ai
사용자 정보를 기반으로 매칭된 혜택을 반환합니다.

**Request Body:**
```json
{
  "age": 78,
  "gender": "F",
  "region": "서울특별시",
  "insuranceType": "의료급여",
  "isBasicRecipient": true,
  "diseases": ["치매"],
  "isHospitalized": true,
  "hospitalType": "요양병원"
}
```

**Response:**
```json
{
  "matchedBenefits": [
    {
      "benefitId": 1,
      "benefitName": "노인장기요양보험",
      "category": "장기요양",
      "provider": "국민건강보험공단",
      "benefitDescription": "65세 이상 또는 치매...",
      "applicationMethod": "공단에 장기요양인정 신청...",
      "targetCriteria": {
        "age_min": 65,
        "diseases": ["치매"]
      }
    }
  ],
  "aiSummary": "치매로 요양병원에 입원한 78세 여성은...",
  "totalCount": 1
}
```

### GET /api/match/health
서버 상태를 확인합니다.

**Response:**
```
Welfaring Backend is running!
```

## 🚀 로컬 실행

### 1. 환경 설정
```bash
# OpenAI API Key 설정
export OPENAI_API_KEY="your-openai-api-key-here"
```

<<<<<<< HEAD
### 2. Gradle로 실행
=======
### 2. 데이터베이스 설정
PostgreSQL 데이터베이스가 실행 중이어야 합니다.

### 3. 애플리케이션 실행
>>>>>>> ba47bd149bb4d7995895ed1c285d04317cfcb66b
```bash
./gradlew bootRun
```

<<<<<<< HEAD
### 3. Docker로 실행
```bash
# 환경변수 설정
cp env.example .env
# .env 파일에서 OPENAI_API_KEY 수정

# Docker Compose로 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f welfaring-backend

# 서비스 중지
docker-compose down
```

=======
>>>>>>> ba47bd149bb4d7995895ed1c285d04317cfcb66b
애플리케이션이 `http://localhost:8080`에서 실행됩니다.

## 🌐 배포

<<<<<<< HEAD
### Docker 배포 (권장)

#### 1. 로컬 Docker 실행
```bash
# 환경변수 설정
cp env.example .env
# .env 파일에서 OPENAI_API_KEY 수정

# Docker Compose로 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f welfaring-backend

# 서비스 중지
docker-compose down
```

#### 2. Render Docker 배포
- **Environment**: `docker`
- **Dockerfile**: `./Dockerfile`
- **Environment Variables**: `OPENAI_API_KEY`

### Gradle 배포 (기존)
=======
이 프로젝트는 Render에서 배포됩니다.

### Render 배포 설정
>>>>>>> ba47bd149bb4d7995895ed1c285d04317cfcb66b
- **Build Command**: `./gradlew build`
- **Start Command**: `java -jar build/libs/welfaring-0.0.1-SNAPSHOT.jar`
- **Environment Variables**: `OPENAI_API_KEY`

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/com/demo/welfaring/
│   │   ├── controller/          # API 컨트롤러
│   │   ├── service/             # 비즈니스 로직
│   │   ├── repository/          # 데이터 접근
│   │   ├── domain/              # 엔티티
│   │   ├── dto/                 # 데이터 전송 객체
│   │   ├── config/              # 설정
│   │   └── utils/               # 유틸리티
│   └── resources/
│       ├── application.properties
<<<<<<< HEAD
│       ├── application-production.properties
│       └── application-docker.properties
=======
│       └── application-production.properties
>>>>>>> ba47bd149bb4d7995895ed1c285d04317cfcb66b
└── test/
```

## 👥 팀원

- **혜빈**: Controller, OpenAI Service, DTO
- **우민**: Service, Repository, Domain, Utils

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 있습니다.
