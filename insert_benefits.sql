-- benefits 테이블 생성
CREATE TABLE benefits (
    benefit_id SERIAL PRIMARY KEY,
    benefit_name VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    target_criteria JSONB NOT NULL,
    application_method TEXT,
    law_reference TEXT,
    data_source JSONB,
    benefit_description TEXT,
    provider VARCHAR(200),
    last_updated DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- benefits 테이블에 데이터 삽입
INSERT INTO benefits (benefit_id, benefit_name, category, target_criteria, application_method, law_reference, data_source, benefit_description, provider, last_updated) VALUES
(1, '노인장기요양보험 재가급여', '장기요양', 
'{"age_min": 65, "age_max": null, "diseases": ["치매", "뇌혈관질환", "파킨슨병", "노인성질환", "진전"], "long_term_care_grade": [1, 2, 3, 4, 5, "인지지원등급"], "income_max": null, "family_members": null, "daily_life_difficulty": null, "insurance_type": ["건강보험", "의료급여"], "is_basic_recipient": null, "is_low_income": null, "is_hospitalized": false, "hospital_type": null, "occupation": null, "income": null, "property_value": null, "is_pregnant": false, "chronic_diseases": ["치매", "뇌혈관질환", "파킨슨병"], "region": null, "gender": null, "is_disabled": null}'::jsonb,
'장기요양인정신청서(별지 제1호의2서식)에 의사소견서(별지 제2호서식) 첨부하여 국민건강보험공단에 제출. 65세 미만의 경우 노인성 질병 진단서 등 증명서류 필요. 의사소견서 발급비용은 본인 20%, 공단 80% 부담.',
'노인장기요양보험법 시행령(대통령령 제35626호, 2025.7.1.) 제7조(등급판정기준 등) ① 법 제15조제2항에 따른 등급판정기준은 다음 각 호와 같다. 1. 장기요양 1등급: 심신의 기능상태 장애로 일상생활에서 전적으로 다른 사람의 도움이 필요한 자로서 장기요양인정 점수가 95점 이상인 자 2. 장기요양 2등급: 심신의 기능상태 장애로 일상생활에서 상당 부분 다른 사람의 도움이 필요한 자로서 장기요양인정 점수가 75점 이상 95점 미만인 자 3. 장기요양 3등급: 심신의 기능상태 장애로 일상생활에서 부분적으로 다른 사람의 도움이 필요한 자로서 장기요양인정 점수가 60점 이상 75점 미만인 자 4. 장기요양 4등급: 심신의 기능상태 장애로 일상생활에서 일정부분 다른 사람의 도움이 필요한 자로서 장기요양인정 점수가 51점 이상 60점 미만인 자 5. 장기요양 5등급: 치매(제2조에 따른 노인성 질병에 해당하는 치매로 한정한다)환자로서 장기요양인정 점수가 45점 이상 51점 미만인 자 6. 장기요양 인지지원등급: 치매(제2조에 따른 노인성 질병에 해당하는 치매로 한정한다)환자로서 장기요양인정 점수가 45점 미만인 자 ② 제1항에 따른 장기요양인정 점수는 장기요양이 필요한 정도를 나타내는 점수로서 보건복지부장관이 정하여 고시하는 심신의 기능 저하 상태를 측정하는 방법에 따라 산정한다. 노인장기요양보험법 시행규칙(보건복지부령 제1118호, 2025.6.21.) 제2조(장기요양인정 신청 및 의사소견서 제출) ① 법 제13조제1항에 따라 장기요양인정을 신청하려는 자(이하 "신청인"이라 한다)는 별지 제1호의2서식의 장기요양인정신청서에 별지 제2호서식에 따른 의사 또는 한의사의 소견서를 첨부하여 제출하여야 한다. 제4조(의사소견서 발급비용 등) ② 신청인이 제2조제4항에 따라 발급의뢰서를 통하여 의사소견서를 발급받는 경우 그 발급비용은 다음 각 호와 같이 부담한다. 1. 65세 이상의 노인이나 65세 미만의 자로서 노인성 질병을 가진 자 : 100분의 20은 본인이, 100분의 80은 공단이 부담한다.',
'{"type": "법령", "source_name": "노인장기요양보험법 시행령(제35626호, 2025.7.1)", "last_updated": "2025-07-01", "file_reference": "노인장기요양보험법 시행령(대통령령)(제35626호)(20250701)_fulltext.txt"}'::jsonb,
'65세 이상 노인 또는 치매 등 노인성 질병을 가진 자에게 신체활동 및 가사활동 지원 등의 재가급여 제공. 방문요양, 방문간호, 방문목욕, 주야간보호, 단기보호, 기타재가급여(용구 제공·대여) 서비스를 제공합니다.',
'국민건강보험공단',
'2025-07-01'),

(2, '노인장기요양보험 시설급여', '장기요양',
'{"age_min": 65, "age_max": null, "diseases": ["치매", "뇌혈관질환", "노인성질환", "진전"], "long_term_care_grade": [1, 2, 3, 4, 5, "인지지원등급"], "income_max": null, "family_members": null, "daily_life_difficulty": null, "insurance_type": ["건강보험", "의료급여"], "is_basic_recipient": null, "is_low_income": null, "is_hospitalized": false, "hospital_type": null, "occupation": null, "income": null, "property_value": null, "is_pregnant": false, "chronic_diseases": ["치매", "뇌혈관질환"], "region": null, "gender": null, "is_disabled": null}'::jsonb,
'장기요양인정 신청 후 등급판정을 통해 1~5등급 또는 인지지원등급 결정 시 노인요양시설 또는 노인요양공동생활가정에 입소하여 급여 제공. 시설급여 제공기관은 법 제31조에 따라 지정받은 장기요양기관이어야 함.',
'노인장기요양보험법 시행령(대통령령 제35626호, 2025.7.1.) 제10조(장기요양기관의 종류 및 기준) 법 제23조제1항제1호 및 제2호에 따라 장기요양급여를 제공할 수 있는 장기요양기관의 종류 및 기준은 다음 각 호의 구분에 따른다. 2. 시설급여를 제공할 수 있는 장기요양기관 가. 「노인복지법」 제34조제1항제1호에 따른 노인요양시설로서 법 제31조에 따라 지정받은 장기요양기관 나. 「노인복지법」 제34조제1항제2호에 따른 노인요양공동생활가정으로서 법 제31조에 따라 지정받은 장기요양기관',
'{"type": "법령", "source_name": "노인장기요양보험법 시행령(제35626호, 2025.7.1)", "last_updated": "2025-07-01", "file_reference": "노인장기요양보험법 시행령(대통령령)(제35626호)(20250701)_fulltext.txt"}'::jsonb,
'65세 이상 노인 등에게 노인요양시설 또는 노인요양공동생활가정에서 제공하는 시설급여. 등급별로 일정 수준의 케어 서비스를 제공하며, 신체활동 지원 및 심신기능의 유지·향상을 위한 교육·훈련 등을 제공합니다.',
'국민건강보험공단',
'2025-07-01'),

(3, '장기요양본인부담금 감경', '의료지원',
'{"age_min": null, "age_max": null, "insurance_type": ["의료급여"], "income_max": null, "is_low_income": true, "property_value": null, "diseases": null, "long_term_care_grade": null, "family_members": null, "daily_life_difficulty": null, "is_basic_recipient": true, "is_hospitalized": false, "hospital_type": null, "occupation": null, "income": null, "is_pregnant": false, "chronic_diseases": null, "region": null, "gender": null, "is_disabled": null}'::jsonb,
'의료급여증 또는 의료급여증명서를 첨부하여 장기요양기관에 제출. 건강보험공단으로부터 본인부담액 경감 인정을 받은 경우 자동 적용.',
'노인장기요양보험법 제40조(본인부담금) 제1항: 제23조에 따른 장기요양급여(특별현금급여는 제외한다)를 받는 자는 대통령령으로 정하는 바에 따라 비용의 일부를 본인이 부담한다. 장기요양 본인부담금 감경에 관한 고시(일부개정 20211125) 제2조(감경대상 및 감경률) 제1항: 다음 각 호의 어느 하나에 해당하는 자는 본인부담금의 100분의 60을 감경한다. 1. 「의료급여법」제3조제1항제2호부터 제9호까지의 규정에 따른 수급권자 2. 「국민건강보험법 시행규칙」제15조에 따라 국민건강보험공단으로부터 건강보험 본인부담액 경감 인정을 받은 자',
'{"type": "고시", "source_name": "장기요양 본인부담금 감경에 관한 고시", "last_updated": "2021-11-25", "file_reference": "merged_with_dividers.txt"}'::jsonb,
'의료급여수급권자 및 저소득층에게 장기요양본인부담금의 최대 60% 감경. 기초생활수급자는 본인부담금 전액 면제, 의료급여 2호~9호는 60% 감경됩니다.',
'국민건강보험공단',
'2021-11-25'),

(4, '장기요양보험료 경감', '의료지원',
'{"age_min": null, "age_max": null, "diseases": ["장애", "희귀난치성질환"], "is_disabled": true, "long_term_care_grade": null, "income_max": null, "family_members": null, "daily_life_difficulty": null, "insurance_type": ["건강보험", "의료급여"], "is_basic_recipient": null, "is_low_income": null, "is_hospitalized": false, "hospital_type": null, "occupation": null, "income": null, "property_value": null, "is_pregnant": false, "chronic_diseases": ["장애", "희귀난치성질환"], "region": null, "gender": null}'::jsonb,
'장애인증 또는 희귀난치성질환 진단서를 제출하여 공단에 경감신청. 심한 장애인 또는 희귀난치성질환자의 경우 보험료 30% 경감.',
'노인장기요양보험법 제10조(장애인 등에 대한 장기요양보험료의 감면): 공단은 「장애인복지법」에 따른 장애인 또는 이와 유사한 자로서 대통령령으로 정하는 자가 장기요양보험가입자 또는 그 피부양자인 경우 제15조제2항에 따른 수급자로 결정되지 못한 때 대통령령으로 정하는 바에 따라 장기요양보험료의 전부 또는 일부를 감면할 수 있다. 시행령 제5조(장애인 등에 대한 장기요양보험료의 경감) 제1항: 법 제10조에서 "대통령령으로 정하는 자"란 다음 각 호의 어느 하나에 해당하는 자를 말한다. 1. 「장애인복지법」 제32조에 따라 등록한 장애인 중 장애의 정도가 심한 장애인',
'{"type": "법령", "source_name": "노인장기요양보험법 시행령", "last_updated": "2025-07-01", "file_reference": "노인장기요양보험법 시행령(대통령령)(제35626호)(20250701)_fulltext.txt"}'::jsonb,
'심한 장애인 또는 희귀난치성질환자의 장기요양보험료 30% 경감. 수급자로 결정되지 않은 경우에도 경감 혜택 제공.',
'국민건강보험공단',
'2025-07-01'),

(5, '가족요양비', '의료지원',
'{"age_min": 65, "age_max": null, "long_term_care_grade": [1, 2], "income_max": null, "diseases": null, "family_members": null, "daily_life_difficulty": null, "insurance_type": ["건강보험", "의료급여"], "is_basic_recipient": null, "is_low_income": null, "is_hospitalized": false, "hospital_type": null, "occupation": null, "income": null, "property_value": null, "is_pregnant": false, "chronic_diseases": null, "region": null, "gender": null, "is_disabled": null}'::jsonb,
'장기요양 1~2등급 수급자 가족 중 요양을 제공하는 자에게 지급. 신청서 작성 후 관련 서류 제출.',
'노인장기요양보험법 제24조(가족요양비) 제1항: 공단은 다음 각 호의 어느 하나에 해당하는 수급자가 가족 등으로부터 제23조제1항제1호가목에 따른 방문요양에 상당한 장기요양급여를 받은 때 대통령령으로 정하는 기준에 따라 해당 수급자에게 가족요양비를 지급할 수 있다.',
'{"type": "법령", "source_name": "노인장기요양보험법", "last_updated": "2024-12-20", "file_reference": "merged_with_dividers.txt"}'::jsonb,
'장기요양 1등급 또는 2등급 수급자의 가족이 직접 요양을 제공하는 경우 지급하는 현금급여. 가족의 부담을 덜어주고 가족 수발을 지원합니다.',
'국민건강보험공단',
'2024-12-20'),

(6, '치료재료 급여', '의료지원',
'{"age_min": null, "age_max": null, "insurance_type": ["건강보험"], "diseases": null, "long_term_care_grade": null, "income_max": null, "family_members": null, "daily_life_difficulty": null, "is_basic_recipient": null, "is_low_income": null, "is_hospitalized": false, "hospital_type": null, "occupation": null, "income": null, "property_value": null, "is_pregnant": false, "chronic_diseases": null, "region": null, "gender": null, "is_disabled": null}'::jsonb,
'의료기관에서 치료에 필요한 재료 사용 시 급여 적용. 의사 처방에 따라 급여대상 치료재료 사용.',
'건강보험 행위 급여·비급여 목록표 및 급여 상대가치점수 (일부개정 2025-10-01): 건강보험 적용 행위, 치료재료, 약제에 대한 급여 여부 및 급여 상대가치점수를 정한 고시. 치료재료는 급여목록에 포함된 재료를 사용하는 경우 건강보험 급여가 적용되며, 본인부담금은 일반적으로 20%를 부담합니다.',
'{"type": "고시", "source_name": "건강보험 행위 급여·비급여 목록표 및 급여 상대가치점수", "last_updated": "2025-10-01", "file_reference": "(2025-148) 건강보험 행위 급여·비급여 목록표 및 급여 상대가치점수 일부개정안_정신수가_fulltext.txt"}'::jsonb,
'의료행위에 사용되는 치료재료에 대한 건강보험 급여. 급여상한금액 범위 내에서 본인부담금을 낮추어 부담을 줄여줍니다.',
'건강보험심사평가원',
'2025-10-01'),

(7, '의료급여', '의료지원',
'{"age_min": null, "age_max": null, "insurance_type": ["의료급여"], "income_max": null, "is_basic_recipient": true, "diseases": null, "long_term_care_grade": null, "family_members": null, "daily_life_difficulty": null, "is_low_income": true, "is_hospitalized": false, "hospital_type": null, "occupation": null, "income": null, "property_value": null, "is_pregnant": false, "chronic_diseases": null, "region": null, "gender": null, "is_disabled": null}'::jsonb,
'기초생활수급자 또는 차상위계층 자격 확인 후 의료급여증 발급. 의료기관 방문 시 의료급여증 제시.',
'의료급여법 제3조(수급권자): 수급권자는 다음 각 호의 어느 하나에 해당하는 자로서 대통령령으로 정하는 기준에 해당하는 자로 한다. 1. 「국민기초생활 보장법」에 따른 수급권자 2. 「국민기초생활 보장법」에 따른 차상위계층. 의료급여법 제10조(본인부담): 수급권자가 의료급여기관에서 의료급여를 받은 경우에는 대통령령으로 정하는 금액을 본인부담으로 부담한다. 다만, 국민기초생활보장법 제7조제1항제1호에 따른 수급자(이하 "기초생활수급자"라 한다)는 본인부담금을 부담하지 아니한다.',
'{"type": "법령", "source_name": "의료급여법", "last_updated": null, "file_reference": "merged_with_dividers.txt"}'::jsonb,
'생활이 어려운 국민에게 의료비를 지원하는 제도. 1종 의료급여는 본인부담 없이 전액 지원, 2종은 일부 본인부담이 있습니다.',
'보건복지부, 지방자치단체',
null);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX idx_benefits_category ON benefits(category);
CREATE INDEX idx_benefits_target_criteria ON benefits USING GIN(target_criteria);
CREATE INDEX idx_benefits_provider ON benefits(provider);

-- JSONB 쿼리 예시
-- SELECT * FROM benefits WHERE target_criteria @> '{"insurance_type": ["의료급여"]}';
-- SELECT * FROM benefits WHERE target_criteria @> '{"diseases": ["치매"]}';
-- SELECT * FROM benefits WHERE target_criteria @> '{"age_min": 65}';
