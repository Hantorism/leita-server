# External 패키지 (공통 유틸리티)

## 설명
`external` 패키지는 프로젝트 전반에서 재사용되는 공통 기능을 제공합니다. 이메일 전송, 외부 서비스(Google OAuth, Slack) 연동, 캐싱 등 도메인에 종속되지 않는 독립적인 유틸리티들로 구성되어 있습니다.

## 주요 구성 요소

### 1. Cache (캐시)
- **CacheUtil**: 간단한 In-memory 캐시 기능을 제공합니다.
- **기능**:
    - `get(key)`: 키에 해당하는 값을 조회합니다.
    - `set(key, value)`: 키-값 쌍을 저장합니다.
    - `remove(key)`: 특정 키의 데이터를 삭제합니다.
- **용도**: GitHub OAuth 연동 시 `state` 값 검증 등을 위해 임시 데이터를 저장하는 용도로 사용됩니다.

### 2. Google OAuth (구글 인증)
- **GoogleOAuthUtil**: Google OAuth API와의 통신을 담당합니다.
- **기능**:
    - `getUserInfo(accessToken)`: 제공된 액세스 토큰을 사용하여 Google 사용자 정보를 가져옵니다.
- **참고**: 내부적으로 `GoogleOAuthClient`를 통해 외부 API를 호출합니다.

### 3. Mail (메일)
- **MailUtil**: 시스템 알림 및 사용자 안내를 위한 이메일 전송 기능을 제공합니다.
- **기능**:
    - `send(mailType, email, args)`: 단일 사용자에게 비동기 방식으로 메일을 전송합니다.
    - `sendAll(mailType, emails, args)`: 여러 사용자에게 동시에 비동기 메일을 전송합니다.
- **MailType**: 메일의 제목과 본문 템플릿을 관리하는 Enum입니다 (예: 스터디 가입 신청 알림).

### 4. Slack (슬랙 연동)
...
- **구성**: `SlackLogLevel`(로그 수준), `SlackLabel`(알림 아이콘 및 제목) 등을 사용하여 메시지를 시각화합니다.

### 5. Message Queue (메시지 큐)
- **설정**: `com.leita.leita.external.judge.messagequeue` 패키지에 위치합니다.
- **기능**: 채점 요청 발행 및 결과 수신 등 신뢰성이 필요한 비동기 처리에 사용됩니다.
- **Consumer**: `RabbitListener`를 통해 메시지를 수신하고 관련 서비스를 호출합니다.

### 6. HTTP Client (OpenFeign)
- **표준**: 프로젝트의 모든 외부 HTTP 요청은 `FeignClient` 사용을 지향합니다.
- **예시**: `GithubClient`, `GoogleOAuthClient`, `JudgeClient`

## 사용 가이드
- 모든 유틸리티는 `@Component` 또는 `@FeignClient`로 등록되어 있으므로 필요한 서비스에서 의존성을 주입받아 사용할 수 있습니다.
- Message Queue를 통한 처리는 트랜잭션 범위와 메시지 수신 성공 여부(Ack)를 고려하여 설계하십시오.
