# 채점 시스템 Message Queue 연동 규격서

본 문서는 백엔드 서버와 채점 서버 간의 Message Queue(RabbitMQ) 기반 비동기 채점 프로세스 및 통신 규격을 정의합니다.

## 1. 채점 프로세스 플로우

1.  **사용자**: 풀이 코드 제출 (`POST /judge/submit/{problemId}`)
2.  **백엔드**:
    *   채점 기록을 DB에 저장 (상태: `PENDING`)
    *   채점 요청 메시지를 `judge.exchange` (Routing Key: `judge.request`)로 발행
    *   사용자에게 즉시 응답 반환 (비동기 처리 시작)
3.  **RabbitMQ**: 메시지를 `judge.request.queue`로 전달
4.  **채점 서버**:
    *   `judge.request.queue`로부터 메시지를 수신(Consume)
    *   코드 채점 수행
    *   채점 결과를 `judge.exchange` (Routing Key: `judge.result`)로 발행
5.  **백엔드**:
    *   `judge.result.queue`로부터 결과를 수신
    *   `submitId`를 기반으로 해당 채점 기록 및 문제 해결 상태 업데이트

## 2. Message Queue 설정 정보

*   **Exchange**: `judge.exchange` (type: direct)
*   **Request Queue**: `judge.request.queue` (Routing Key: `judge.request`)
*   **Result Queue**: `judge.result.queue` (Routing Key: `judge.result`)
*   **Content-Type**: `application/json`

## 3. 데이터 규격 (Payload)

### 3.1. 채점 요청 (Backend -> Judge Server)
**Queue**: `judge.request.queue`

```json
{
  "submitId": 12345,
  "code": "base64_encoded_code",
  "language": "PYTHON" // C, CPP, JAVA, PYTHON, JAVASCRIPT, GO, KOTLIN, SWIFT
}
```

### 3.2. 채점 결과 (Judge Server -> Backend)
**Queue**: `judge.result.queue`

```json
{
  "submitId": 12345,
  "result": "CORRECT", // CORRECT, WRONG, COMPILE_ERROR, RUNTIME_ERROR, TIME_OUT, MEMORY_OUT, UNKNOWN
  "error": "detailed_error_message_if_any",
  "usedMemory": 1024, // 단위: KB
  "usedTime": 150     // 단위: ms
}
```

## 4. 채점 서버(Judge Server) 수정 필요 사항

채점 서버가 Message Queue 시스템에 통합되기 위해 다음 작업이 필요합니다.

1.  **MQ 클라이언트 구현**: RabbitMQ(AMQP) 연동 모듈을 추가합니다.
2.  **소비자(Consumer) 설정**:
    *   `judge.request.queue`를 구독하도록 설정합니다.
    *   수신된 JSON 메시지를 파싱하여 `submitId`, `code`, `language`를 추출합니다.
3.  **채점 로직 연동**:
    *   기존 HTTP 엔드포인트 방식 대신, MQ에서 수신된 데이터를 바탕으로 채점 엔진을 실행하도록 변경합니다.
4.  **발행자(Producer) 구현**:
    *   채점 완료 후, 위의 **3.2. 채점 결과** 규격에 맞춰 JSON 메시지를 생성합니다.
    *   `judge.exchange`에 `judge.result` 라우팅 키를 사용하여 메시지를 발행합니다.
5.  **에러 처리**:
    *   채점 중 예외 발생 시에도 반드시 `result: UNKNOWN` 등의 상태로 응답을 발행하여 백엔드의 대기 상태를 해제해야 합니다.
    *   메시지 처리 실패 시 RabbitMQ의 Ack/Nack 메커니즘을 적절히 사용하여 메시지 유실을 방지합니다.
