# User 도메인 (사용자)

## 설명
사용자(User) 도메인은 핵심 신원 정보와 연결된 GitHub 계정 상세 정보를 포함한 사용자 프로필 정보를 관리합니다.

## 서비스 정책
- **핵심 신원**: 기본 정보와 보안 역할을 포함하는 `User` 엔티티로 사용자를 표현합니다.
- **GitHub 연동**: `GithubInfo`를 통해 사용자별 GitHub 정보를 저장하고 관리합니다. (예: `installationId`, `githubUserName`)
- **감사(Auditing)**: 생성 및 수정 시간 자동 추적을 위해 `BaseEntity`를 상속받습니다.
- **저장소**: `UserRepository`를 통해 데이터베이스에 사용자 데이터를 유지합니다.
- **상태 관리**: GitHub 연동을 위한 상태(state) 생성 및 검증 로직을 포함합니다.
