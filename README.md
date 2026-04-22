# HR Bank

HR Bank는 기업의 인사 정보를 관리하기 위한 백엔드 시스템입니다.  
직원, 부서, 직원 정보 변경 이력, 데이터 백업, 파일 다운로드 기능을 제공하며, OpenAPI 명세 기반의 REST API로 구성되어 있습니다.

## 주요 기능

- 직원 등록, 조회, 수정, 삭제
- 직원 프로필 이미지 업로드 및 다운로드
- 직원 수 통계 조회
- 직원 분포 및 추이 조회
- 부서 등록, 조회, 수정, 삭제
- 직원 정보 변경 이력 조회
- 데이터 백업 생성 및 백업 이력 조회
- 정기 백업 스케줄링
- 커서 기반 페이지네이션
- 공통 예외 응답 처리

## 기술 스택

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- Spring Validation
- PostgreSQL
- QueryDSL
- MapStruct
- Lombok
- Springdoc OpenAPI
- Gradle

## 프로젝트 구조

```text
src/main/java/com/sb11/hr_bank
├── domain
│   ├── employee      # 직원 관리
│   ├── department    # 부서 관리
│   ├── changelogs    # 직원 정보 수정 이력
│   ├── backup        # 데이터 백업
│   └── file          # 파일 관리
└── global
    ├── base          # 공통 엔티티
    ├── config        # 전역 설정
    ├── dto           # 공통 응답 DTO
    └── exception     # 예외 처리
```

## 실행 환경

### 필수 요구사항

- Java 17
- PostgreSQL
- Gradle Wrapper 사용 가능 환경

### 환경 변수

애플리케이션 실행 전 다음 환경 변수를 설정해야 합니다.

```bash
export DB_URL=jdbc:postgresql://localhost:5432/mydb
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```

`DB_URL`을 지정하지 않으면 기본값으로 다음 주소를 사용합니다.

```text
jdbc:postgresql://localhost:5432/mydb
```

### 데이터베이스 초기화

초기 테이블 생성 SQL은 다음 파일에 정의되어 있습니다.

```text
src/main/resources/v1.0.0_hr_bank.sql
```

PostgreSQL 데이터베이스에 해당 SQL을 실행한 뒤 애플리케이션을 실행합니다.


## API 문서

Springdoc OpenAPI를 통해 API 문서를 확인할 수 있습니다.

```text
/swagger-ui/index.html
/v3/api-docs
```

배포 서버 기준 API Base URL은 다음과 같습니다.

```text
http://sprint-project-1196140422.ap-northeast-2.elb.amazonaws.com/sb/hrbank
```

로컬 실행 시에는 일반적으로 다음 주소를 사용합니다.

```text
http://localhost:8080
```

## API 개요

### 직원 관리

| Method | Endpoint | 설명 |
| --- | --- | --- |
| GET | `/api/employees` | 직원 목록 조회 |
| POST | `/api/employees` | 직원 등록 |
| GET | `/api/employees/{id}` | 직원 상세 조회 |
| PATCH | `/api/employees/{id}` | 직원 수정 |
| DELETE | `/api/employees/{id}` | 직원 삭제 |
| GET | `/api/employees/count` | 직원 수 조회 |
| GET | `/api/employees/stats/distribution` | 직원 분포 조회 |
| GET | `/api/employees/stats/trend` | 직원 수 추이 조회 |

직원 등록과 수정은 `multipart/form-data` 형식을 사용합니다.

- `employee`: 직원 요청 JSON
- `profile`: 프로필 이미지 파일, 선택값

직원 상태는 다음 값을 사용합니다.

```text
ACTIVE
ON_LEAVE
RESIGNED
```

### 부서 관리

| Method | Endpoint | 설명 |
| --- | --- | --- |
| GET | `/api/departments` | 부서 목록 조회 |
| POST | `/api/departments` | 부서 등록 |
| GET | `/api/departments/{id}` | 부서 상세 조회 |
| PATCH | `/api/departments/{id}` | 부서 수정 |
| DELETE | `/api/departments/{id}` | 부서 삭제 |

소속 직원이 존재하는 부서는 삭제할 수 없습니다.

### 직원 정보 수정 이력 관리

| Method | Endpoint | 설명 |
| --- | --- | --- |
| GET | `/api/change-logs` | 직원 정보 수정 이력 목록 조회 |
| GET | `/api/change-logs/{id}` | 직원 정보 수정 이력 상세 조회 |
| GET | `/api/change-logs/count` | 수정 이력 건수 조회 |

직원 생성, 수정, 삭제 시 변경 이력이 기록됩니다.  
상세 조회에서는 변경된 필드의 이전 값과 이후 값을 확인할 수 있습니다.

이력 유형은 다음 값을 사용합니다.

```text
CREATED
UPDATED
DELETED
```

### 데이터 백업 관리

| Method | Endpoint | 설명 |
| --- | --- | --- |
| GET | `/api/backups` | 데이터 백업 목록 조회 |
| POST | `/api/backups` | 데이터 백업 생성 |
| GET | `/api/backups/latest` | 최근 백업 정보 조회 |

백업 상태는 다음 값을 사용합니다.

```text
IN_PROGRESS
COMPLETED
FAILED
SKIPPED
```

백업은 수동 생성뿐 아니라 스케줄러를 통해 주기적으로 실행됩니다.  
기본 백업 주기는 `application.yaml`의 다음 설정으로 관리됩니다.

```yaml
backup:
  interval-ms: 3600000
```

### 파일 관리

| Method | Endpoint | 설명 |
| --- | --- | --- |
| GET | `/api/files/{id}/download` | 파일 다운로드 |

직원 프로필 이미지와 백업 파일은 파일 엔티티로 관리되며, 파일 ID를 통해 다운로드할 수 있습니다.

## 페이지네이션

목록 조회 API는 커서 기반 페이지네이션을 사용합니다.

공통 요청 파라미터는 다음과 같습니다.

| 파라미터 | 설명 |
| --- | --- |
| `cursor` | 다음 페이지 조회를 위한 커서 |
| `idAfter` | 이전 페이지 마지막 요소 ID |
| `size` | 페이지 크기 |
| `sortField` | 정렬 필드 |
| `sortDirection` | 정렬 방향 |

공통 응답 형식은 다음과 같습니다.

```json
{
  "content": [],
  "nextCursor": "eyJpZCI6MjB9",
  "nextIdAfter": 20,
  "size": 10,
  "totalElements": 100,
  "hasNext": true
}
```

## 에러 응답

API 오류는 공통 에러 응답 형식으로 반환됩니다.

```json
{
  "timestamp": "2025-03-06T05:39:06.152068Z",
  "status": 400,
  "message": "잘못된 요청입니다.",
  "details": "부서 코드는 필수입니다."
}
```

주요 상태 코드는 다음과 같습니다.

| Status | 설명 |
| --- | --- |
| 400 | 잘못된 요청 |
| 404 | 리소스를 찾을 수 없음 |
| 409 | 충돌 상태 |
| 500 | 서버 오류 |

## 주요 도메인

### Department

부서 정보를 관리합니다.

- 부서명
- 부서 설명
- 설립일
- 소속 직원 수

### Employee

직원 정보를 관리합니다.

- 이름
- 이메일
- 사원 번호
- 부서
- 직함
- 입사일
- 재직 상태
- 프로필 이미지

### ChangeLog

직원 정보 변경 이력을 관리합니다.

- 변경 유형
- 대상 직원 사번
- 메모
- IP 주소
- 변경 일시
- 상세 변경 내역

### Backup

데이터 백업 이력을 관리합니다.

- 작업자
- 시작 시간
- 종료 시간
- 백업 상태
- 백업 파일

### File

업로드 및 생성된 파일 메타데이터를 관리합니다.

- 파일명
- Content-Type
- 파일 크기

## 정렬 기준

### 직원 목록

사용 가능한 정렬 필드:

```text
name
employeeNumber
hireDate
```

### 부서 목록

사용 가능한 정렬 필드:

```text
name
establishedDate
```

### 수정 이력 목록

사용 가능한 정렬 필드:

```text
ipAddress
at
```

### 백업 목록

사용 가능한 정렬 필드:

```text
startedAt
endedAt
status
```

## 통계 API

### 직원 수 조회

`/api/employees/count`

상태와 입사일 기간을 기준으로 직원 수를 조회할 수 있습니다.

### 직원 분포 조회

`/api/employees/stats/distribution`

부서별 또는 직무별 직원 분포를 조회합니다.

사용 가능한 그룹 기준:

```text
department
position
```

### 직원 수 추이 조회

`/api/employees/stats/trend`

기간과 단위를 기준으로 직원 수 추이를 조회합니다.  
파라미터를 지정하지 않으면 최근 12개월 데이터를 월 단위로 반환합니다.

사용 가능한 단위:

```text
day
week
month
quarter
year
```

## 파일 저장

파일은 로컬 `uploads` 디렉터리에 저장됩니다.  
파일 다운로드 시 저장된 파일 ID를 기준으로 실제 파일을 찾아 응답합니다.

## 라이선스

본 프로젝트는 HR Bank 백엔드 시스템 구현을 위한 프로젝트입니다.
