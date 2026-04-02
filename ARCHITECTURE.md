# Leita 백엔드 아키텍처 및 컨벤션

## 프로젝트 소개
Leita는 코딩 스터디 세션을 활성화하고 문제 풀이 현황을 추적하기 위해 설계된 플랫폼입니다. GitHub와 연동하여 레포지토리 관리 및 자동 커밋 추적 기능을 제공하며, 다양한 프로그래밍 언어에 대한 채점 시스템을 갖추고 있습니다.

## 아키텍처
본 프로젝트는 **도메인 주도 디렉토리 구조(Domain-Driven Directory Structure)**를 따릅니다. 각 도메인은 독립적으로 구성되며, 자체적인 컨트롤러, 서비스, 레포지토리 및 도메인 모델을 가집니다.

### 도메인 구조
각 도메인 패키지(예: `com.leita.leita.problem`)는 다음과 같이 구성됩니다:
- `controller/`: REST API 컨트롤러 및 매퍼(Mapper).
- `service/`: 비즈니스 로직 및 서비스 정책.
- `repository/`: 데이터 액세스 계층 (Spring Data JPA).
- `domain/`: 엔티티 모델 및 값 객체(Value Object).
- `dto/`: 요청(Request) 및 응답(Response) 데이터 전송 객체.
- `util/`: 도메인 특화 유틸리티.

### 공통 컴포넌트
- `common/`: 보안(Security), 예외 처리(Exception), 로깅(Logging) 등 횡단 관심사.
- `util/`: 공통 유틸리티 (이메일, Slack, Google OAuth 등).
- `config/`: Spring Boot 설정 클래스.

## 코드 컨벤션
- **언어**: Kotlin
- **스타일 가이드**: 표준 Kotlin 코딩 컨벤션을 준수합니다.
- **네이밍**:
    - 클래스: `PascalCase`
    - 함수/변수: `camelCase`
    - 엔티티: 감사(Auditing)를 위해 반드시 `BaseEntity`를 상속해야 합니다.
- **예외 처리**: `CustomException`과 `GlobalExceptionHandler`를 사용합니다.
- **API 응답**: 모든 응답은 `BaseResponse`로 감싸서 반환합니다.
- **문서화**: API 문서화를 위해 Swagger/SpringDoc을 사용합니다.
