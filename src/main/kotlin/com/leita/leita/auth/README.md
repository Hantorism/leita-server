# Auth 도메인 (인증)

## 설명
인증(Auth) 도메인은 주로 Google OAuth2 및 JWT 기반 세션 관리를 통해 사용자 인증 및 인가(Authorization)를 처리합니다.

## 서비스 정책
- **토큰 처리**: 세션 관리를 위해 JWT를 사용합니다. 성공적인 OAuth 로그인 시 액세스 토큰이 제공됩니다.
- **제공자(Provider)**: Google OAuth를 기본 ID 제공자로 사용합니다.
- **보안**: `ApiPaths.kt`에 별도로 지정되지 않은 한 모든 API 경로는 보안이 적용됩니다.
- **매퍼(Mappers)**: DTO와 내부 엔티티 간의 변환에는 `AuthMapper`를 사용합니다.
- **로그인 로직**: `AuthService`는 Google 사용자 정보를 기반으로 신규 사용자를 가입시키거나 기존 사용자를 로그인시킵니다.
