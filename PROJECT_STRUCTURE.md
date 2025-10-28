# Welfaring Backend Project Structure

## 프로젝트 개요
사용자 정보를 입력받아 DB에 있는 해당하는 의료혜택과 매칭해주는 백엔드 서비스

## 프로젝트 구조

```
src/
├─ main/
│   ├─ java/com/demo/welfaring/
│   │   ├─ controller/
│   │   │   ├─ MatchController.java           # 우민 - 혜택 매칭 API 엔드포인트
│   │   │   └─ NormalizeController.java       # 혜빈 - 사용자 데이터 정규화 API
│   │   │
│   │   ├─ service/
│   │   │   ├─ MatchingService.java           # 우민 - 혜택 매칭 비즈니스 로직
│   │   │   ├─ MatchingRuleEngine.java        # 우민 - 매칭 규칙 엔진
│   │   │   └─ OpenAIService.java             # 혜빈 - OpenAI API 연동 서비스
│   │   │
│   │   ├─ repository/
│   │   │   └─ BenefitRepository.java         # 우민 - 혜택 데이터 JPA Repository
│   │   │
│   │   ├─ domain/  (JPA Entity)
│   │   │   ├─ Benefit.java                   # 우민 - 혜택 엔티티
│   │   │   └─ UserProfile.java               # 우민 - 사용자 프로필 엔티티
│   │   │
│   │   ├─ dto/
│   │   │   ├─ UserProfileRequestDTO.java     # 우민 - 사용자 입력 요청 DTO
│   │   │   ├─ UserProfileDTO.java            # 우민 - 정규화된 사용자 프로필 DTO
│   │   │   ├─ MatchingRequestDTO.java        # 우민 - 매칭 요청 DTO
│   │   │   ├─ MatchingResponseDTO.java       # 혜빈 - 매칭 응답 DTO
│   │   │   └─ BenefitDTO.java                # 우민 - 혜택 정보 DTO
│   │   │
│   │   ├─ config/
│   │   │   └─ OpenAIConfig.java              # 혜빈 - OpenAI API 설정
│   │   │
│   │   └─ utils/
│   │       ├─ EligibilityParser.java         # 우민 - 자격요건 파싱 유틸
│   │       └─ ConditionEvaluator.java        # 우민 - 조건 평가 유틸
│   │
│   └─ resources/
│       ├─ application.properties             # 우민 - 애플리케이션 설정
│       ├─ data.sql                          # 우민 - 초기 혜택 데이터 삽입
│       └─ schema.sql                        # 우민 - DB 스키마 정의
│
└─ test/java/...
```

## 역할 분담

### 고우민 (혜택 정규화, 매칭 로직)
- **Controller**: `MatchController.java` - 혜택 매칭 API 엔드포인트
- **Service**: `MatchingService.java`, `MatchingRuleEngine.java` - 매칭 비즈니스 로직
- **Repository**: `BenefitRepository.java` - 혜택 데이터 접근
- **Domain**: `Benefit.java`, `UserProfile.java` - JPA 엔티티
- **DTO**: `UserProfileRequestDTO.java`, `UserProfileDTO.java`, `MatchingRequestDTO.java`, `BenefitDTO.java`
- **Utils**: `EligibilityParser.java`, `ConditionEvaluator.java` - 매칭 로직 유틸리티
- **Resources**: `application.properties`, `data.sql`, `schema.sql` - 설정 및 초기 데이터

### 혜빈 (OpenAI API 연동)
- **Controller**: `NormalizeController.java` - 사용자 데이터 정규화 API
- **Service**: `OpenAIService.java` - OpenAI API 연동 서비스
- **DTO**: `MatchingResponseDTO.java` - 매칭 결과 응답 DTO
- **Config**: `OpenAIConfig.java` - OpenAI API 설정

## API 엔드포인트

### 1. 사용자 데이터 정규화 (혜빈)
- **POST** `/api/normalize`
- **입력**: 프론트엔드 사용자 입력 폼 JSON
- **출력**: 정규화된 사용자 프로필 JSON

### 2. 혜택 매칭 (우민)
- **POST** `/api/match`
- **입력**: 정규화된 사용자 프로필 JSON
- **출력**: 매칭된 혜택 목록 및 AI 요약

## 데이터 흐름

1. **프론트엔드** → 사용자 입력 폼 데이터 전송
2. **NormalizeController** → OpenAI API로 데이터 정규화 요청
3. **OpenAIService** → 정규화된 사용자 프로필 반환
4. **MatchController** → 정규화된 데이터로 혜택 매칭 요청
5. **MatchingService** → DB에서 조건에 맞는 혜택 검색
6. **MatchingRuleEngine** → 자격요건 평가 및 매칭
7. **OpenAIService** → 매칭 결과 AI 요약 생성
8. **프론트엔드** ← 매칭된 혜택 목록 및 요약 반환

## 기술 스택

- **Framework**: Spring Boot 3.5.7
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA
- **API**: REST API
- **External API**: OpenAI API
- **Build Tool**: Gradle
- **Java Version**: 17

## 주요 기능

1. **사용자 데이터 정규화**: OpenAI API를 통한 입력 데이터 표준화
2. **혜택 매칭**: 사용자 조건에 맞는 의료혜택 검색 및 매칭
3. **자격요건 평가**: 복잡한 혜택 자격요건 조건 평가
4. **AI 요약**: 매칭 결과에 대한 자연어 요약 제공
