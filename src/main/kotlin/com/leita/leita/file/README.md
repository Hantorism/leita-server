# File 도메인 (파일)

## 설명
파일(File) 도메인은 주로 OCI(Oracle Cloud Infrastructure) Object Storage를 사용하여 파일 업로드 및 관리 기능을 제공합니다.

## 서비스 정책
- **객체 스토리지(Object Storage)**: `OracleStorageUtil`을 통해 Oracle 스토리지를 활용합니다.
- **사전 인증된 요청(PAR)**: 보안 파일 업로드를 위한 PAR 생성을 지원합니다.
- **조직화**: 스토리지 버킷 내에서 구조화된 파일 계층 구조를 유지합니다.
- **DTO**: API 통신에는 `GeneratePARRequest` 및 `GeneratePARResponse`를 사용합니다.
- **파일 다운로드**: 채점 시스템 등에서 파일을 다운로드할 때 `OracleStorageUtil`을 사용합니다.
