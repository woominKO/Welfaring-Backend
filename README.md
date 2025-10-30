# 🏥 Welfaring Backend

의료·복지 혜택을 사용자 조건과 규칙에 따라 매칭하고, 결과를 AI로 한 문장 요약해주는 Spring Boot 백엔드입니다.

---

## 🚀 주요 기능
- 사용자 조건 기반 혜택 매칭
- OpenAI를 활용한 AI 요약(실패 시 안전한 대체 요약)
- PostgreSQL + Spring Data JPA
- 환경별 설정(docker, production)
- OAuth 설계 포함(현재 기본 비활성)

---

## 🔄 전체 흐름
1) 프론트가 단순 JSON 입력 전송(혼합 키/오타 허용 가능 설계)
2) 정규화(옵션) → 표준 DTO(`UserProfileDTO`)로 매핑
3) `MatchingService`가 혜택 전수 조회 → `MatchingRuleEngine`이 조건별 평가
4) 매칭 결과를 `OpenAIService`가 한 문장으로 요약(키 없거나 실패 시 fallback)
5) `MatchingResponseDTO`로 반환

---

## 📦 API

### 1) 헬스 체크
- GET `/api/match/health`
- 200 OK + 문자열

### 2) 혜택 매칭(표준 DTO)
- POST `/api/match/ai`
- Request (예):
```json
{
  "age": 78,
  "gender": "F",
  "region": "서울특별시",
  "insuranceType": "의료급여",
  "isBasicRecipient": true,
  "isLowIncome": false,
  "longTermCareGrade": 4,
  "diseases": ["치매"],
  "chronicDiseases": ["치매"],
  "isDisabled": false,
  "isPregnant": false,
  "isHospitalized": true,
  "hospitalType": "요양병원",
  "occupation": null,
  "income": 1200000,
  "propertyValue": 50000000,
  "familyMembers": 1,
  "dailyLifeDifficulty": "6개월 이상 일상생활 수행 어려움"
}
```
- Response (요약):
```json
{
  "matchedBenefits": [ { /* BenefitDTO */ } ],
  "aiSummary": "...",
  "totalCount": 8
}
```

(옵션) 단순 입력(JSON) → 정규화 → 매칭 엔드포인트(`/api/match/ai/simple`)는 필요 시 추가 가능합니다.

---

## 🧠 매칭 로직
- `MatchingService`: 혜택 전수 조회 → 규칙 엔진 평가 → DTO 변환/응답
- `MatchingRuleEngine`: `EligibilityParser`(jsonb 파싱) + `ConditionEvaluator`(조건별 평가)
- 주요 조건: 나이, 질병/만성질병, 보험유형, 장기요양등급, 소득/재산, 가족구성원, 기초수급/저소득, 입원/병원유형, 임신/장애, 지역, 성별, 일상생활 어려움

---

## 🤖 OpenAI 요약
- 입력: 사용자 프로필 + 매칭 혜택 리스트(JSON)
- 출력: 인과관계를 담은 한국어 한 문장
- 실패/401/타임아웃: fallback 요약 사용
- 키 주입: `openai.api.key=${OPENAI_API_KEY}`
  - 환경변수에는 따옴표/공백 없이 값만 입력

---

## 🗂️ 패키지 구조
```
src/main/java/com/demo/welfaring/
├─ controller/   # MatchController 등
├─ service/      # MatchingService, MatchingRuleEngine, OpenAIService
├─ repository/   # BenefitRepository
├─ domain/       # Benefit, UserProfile
├─ dto/          # UserProfileDTO, BenefitDTO, MatchingResponseDTO
├─ utils/        # EligibilityParser, ConditionEvaluator
└─ config/       # OpenAIConfig, SecurityConfig
```

---

## 🔐 보안/프로필
- default/docker: `anyRequest().permitAll()` (개발 편의)
- production: 헬스체크만 허용, 그 외 인증 필요(설계)
- CORS: 개발 `*` / 운영 화이트리스트 권장

---

## 💾 데이터 모델(요약)
- `Benefit`(JPA): `benefitName, category, provider, benefitDescription, applicationMethod, lawReference, targetCriteria(jsonb), dataSource(jsonb), lastUpdated`
- `UserProfileDTO`: `age, gender(M/F), region, insuranceType, isBasicRecipient, isLowIncome, longTermCareGrade, diseases[], chronicDiseases[], isDisabled, isPregnant, isHospitalized, hospitalType, occupation, income, propertyValue, familyMembers, dailyLifeDifficulty`

---

## ⚙️ 환경변수
- 공통: `OPENAI_API_KEY`(선택), DB 접속정보
- 운영 권장: `OPENAI_API_KEY` 설정, CORS 도메인 제한
- 프로필: 기본 `docker`, 운영 `production`

---

## 🧪 로컬 실행
```bash
# (선택) AI 요약 사용 시
export OPENAI_API_KEY=sk-xxxx

./gradlew bootRun

# 헬스
curl http://localhost:8080/api/match/health

# 매칭 예시
curl -X POST http://localhost:8080/api/match/ai \
  -H "Content-Type: application/json" \
  -d '{
    "age":72,
    "gender":"M",
    "region":"서울특별시 강북구",
    "insuranceType":"건강보험",
    "diseases":["치매","고혈압"],
    "isDisabled":false
  }'
```

---

## 🛠️ 배포(Render)
- Environment에 `OPENAI_API_KEY`/DB 설정 → Redeploy
- `render.yaml` 참고

---

## 📌 트러블슈팅
- OpenAI 401: 키 오타/따옴표/공백 여부 확인, 재배포 필요
- 로그인 HTML 응답: 인증 필요한 엔드포인트에 토큰 없이 호출(개발 프로필은 permitAll)

---

## 👥 담당
- 매칭/도메인/유틸: 우민
- 컨트롤러/AI 연동/DTO: 혜빈
