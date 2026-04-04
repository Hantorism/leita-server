# Git 도메인 (Git 연동)

## 설명
Git 도메인은 레포지토리 관리, 설치 추적, 커밋 및 트리 업데이트와 같은 Git 작업을 수행하며 GitHub 연동을 담당합니다.

## 서비스 정책
- **클라이언트 통신**: `external.github.GithubClient` 및 `external.github.GithubUtil`을 통해 GitHub API와 통신합니다.
- **설치(Installations)**: GitHub App 설치를 추적하고 관련 레포지토리를 관리합니다.
- **Git 작업**: 자동 추적을 위해 로우레벨 Git 작업(Blobs, Trees, Commits, Refs)을 지원합니다.
- **레포지토리 메타데이터**: `RepositoryResponse`를 통해 레포지토리 정보를 가져오고 표시합니다.
- **자동 커밋**: 사용자가 문제를 해결했을 때 `autoCommit` 기능을 통해 풀이 코드와 리뷰 문서를 자동으로 GitHub에 커밋합니다.
- **DTO**: 도메인과 API 간의 결합도를 낮추기 위해 특정 Git 관련 DTO를 사용합니다.
