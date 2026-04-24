# HR Bank

- 기업의 핵심 자산인 인적 자원 데이터를 안전하게 저장하고, 대량의 데이터를 주기적으로 백업 및 관리하는 서비스 입니다.
- 프로젝트 기간 : 2026.04.15 ~ 2026.04.25

## 기술 스택

- Java 17
- Spring Boot 3.5.13
- Gradle 8.14.4
- Spring Web
- Spring Validation
- Spring Data JPA
- QueryDSL
- PostgreSQL
- MapStruct
- Lombok
- Springdoc OpenAPI

# 팀 정보

| 이름 | 주요 담당 기능 |
| --- | --- |
| [최우준](https://github.com/wuuujuuun) | 부서 관리 |
| [김태훈](https://github.com/taehk23) | 직원 정보 관리 |
| [김지성](https://github.com/jsKim1219) | 파일 관리 |
| [최광호](https://github.com/LaeHee99) | 직원 정보 수정 이력 관리 |
| [김명근](https://github.com/DonToong2) | 데이터 백업 관리 |
| [최정윤](https://github.com/Jeongyun-Choi62) | 팀장, 공통 영역 구현 및 처리 |

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

## 아키텍쳐

![image.png](attachment:1f695281-9983-4ebf-9a8c-53c19ead2e63:image.png)

## ERD

![image.png](attachment:124bbdb8-45fd-453e-ac7f-fb1979513b3c:image.png)

## 프로젝트 구조

```

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

## 자동 백업 시퀀스 다이어그램

![image.png](attachment:b7223fe6-408b-4268-b836-b68733a15c29:image.png)

## API 개요

https://github.com/sb11-part2-4team/sb11-hrbank-team4/wiki/API-%EA%B0%9C%EC%9A%94

## 실행 환경 세팅

https://github.com/sb11-part2-4team/sb11-hrbank-team4/wiki/HR-Bank-%EC%8B%A4%ED%96%89%ED%99%98%EA%B2%BD-%EC%84%B8%ED%8C%85

## 에러 응답

https://github.com/sb11-part2-4team/sb11-hrbank-team4/wiki/%EC%97%90%EB%9F%AC-%EC%9D%91%EB%8B%B5-%ED%98%95%EC%8B%9D

## 파일 저장

https://github.com/sb11-part2-4team/sb11-hrbank-team4/wiki/%ED%8C%8C%EC%9D%BC-%EC%A0%80%EC%9E%A5

## 이슈/트러블 슈팅

자유롭게 추가해주세요!

### 김명근

- DB timestamptz 타입에 Instant.MIN()이 들어갈 경우에 대한 이슈
    - 상황
        
        `BackupService`의 `startBackup`메서드는 백업 여부를 판단하여 백업이 필요할 시 백업 데이터를 생성하는 메서드입니다.
        먼저 백업 여부를 판단하여 백업을 건너뛸지, 백업을 수행할 지 결정해야하는데
        가장 최근 백업의 시간과 직원 정보 수정 이력의 시간을 비교하는 방법을 택했습니다.
        이때 최근 백업 시간을 가져오는 `lastBackupTime` 변수를 처음에 `Instant.MIN()`으로 설정하였습니다.
        
        - Instant.MIN()은 대략 1,000,000,000(10억)년 전의 날짜를 가져옵니다.
            
            ![출처) Instant.java](attachment:6fc14c43-5d1b-456d-a46d-fd2a007509cd:image.png)
            
            출처) Instant.java
            
        - 하지만 PostgresSQL의 `timestamptz` 타입은 대략 기원전 4,713년~서기 294,276년대의 시간을 지원합니다.
            
            ![출처) https://www.postgresql.org/docs/current/datatype-datetime.html](attachment:94d88a2e-efc4-4a0e-b85c-ac258fbbe6a5:image.png)
            
            출처) https://www.postgresql.org/docs/current/datatype-datetime.html
            
        
        즉, `Instant.MIN()`은 PostgreSQL의 `timestamptz` 타입이 지원하는 범위를 초과하여 정상적으로 메서드를 수행할 수 없게 되는 문제가 발생하였습니다.
        
    - 해결방법
        
        이를 해결하고자 다음의 방법들을 생각하였습니다.
        
        - `lastBakupTime`의 초기값을 설정❌
            
            백업이 존재하지 않을 경우, `lastBakupTime`에 `Instant.EPOCH`로 초기 백업 여부값을 설정하여 해결하는 방법도 있었지만..
            누군가 일부러 로컬 시간을 1970년 이전의 시간으로 조작 하여 직원 이력을 수정하고 진행할 경우 정상적으로 백업을 수행하지 않고 백업을 건너뛰게 됩니다.
            `Instant.now()`는 로컬 기준 현재 시간값이며 시간 수정 시 `Instant.now()`도 값이 바뀌게 됩니다.
            
            - 예시(엣지 케이스)
                1. 로컬에서 1960년 1월 1일로 시간을 수정
                2. 직원 이력 수정 후 백업 시도
                3. 1960년에 백업을 수행했지만 초기값은 1970년, 초기값 이전에 수행하여
                백업이 스킵..
            
            그래서 아예 lastBakupTime에 초기값을 주지않는, 백업이 존재하지 않을 때는 백업을 수행하도록 방향을 바꾸었습니다.
            
        - `lastBackupTime`에 `Optional`을 주고 Optional.empty일 경우 백업을 처리하도록 설정✅
            
            백업 여부를 판단하는 `needBackup` 변수에서
            
            `lastBackupTime`가 `Optional.empty()`일 경우,
            
            `.orElse(true)`로 넘어가서 백업을 수행하도록 개선하였습니다.
            
- QueryDSL을 활용하기 위한 JPAQueryFactory 생성자 문제
    - 상황
        
        `QueryDSL`을 사용하기 위해 `JPAQueryFactory`를 `@RequiredArgsConstructor`로
        생성자를 주입하여 매개변수가 `JPAQueryFactory`이지만
        Spring은 내부적으로 빈 생성을 하기 위해 매개변수 `EntityManager`을 필요로합니다
        
        코드로 보는 예시)
        `QueryDSL`을 사용하는 `BackupRepositoryImpl` 레포지토리가 있습니다.
        이 레포지토리의 final 변수는 `QueryDSL`을 사용하기 위한 `JPAQueryFactory`, `QBackup` 타입 변수입니다.
        
        ```java
        @RequiredArgsConstructor
        public class BackupRepositoryImpl implements BackupRepositoryCustom {
        
        private final JPAQueryFactory queryFactory;
        private final QBackup b = QBackup.backup;
        }
        ```
        
        lombok의 `@RequiredArgsConstructor`을 사용하여 기본 생성자를 생성하였습니다.
        QBackup은 초기화를 하였기때문에 `@RequiredArgsConstructor` 대상에서 제외됩니다.
        
        `@RequiredArgsConstructor`는 자동으로
        
        ```java
        public BackupRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
        }
        ```
        
        를 만들어줍니다.
        
        하지만 Spring에서 빈을 생성할 때 매개변수를 `EntityManager`타입을 필요로 합니다.
        
        ```java
        new BackupRepositoryImpl(EntityManager entityManager)
        ```
        
        필요로 하는 타입이 다르기 때문에 빈이 생성되지 않고, `QueryDSL`을 제대로 활용할 수 없습니다.
        
    - 해결방법
        
        `@RequiredArgsConstructor`을 사용하지 않고 직접 생성자를 구현하였습니다.
        
        ```java
        public BackupRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        }
        ```
        
- 백업 생성 요청시 데이터 정합성 문제
    - 상황
        
        백업을 생성하는 `startBackup` 메서드의 과정은 다음과 같습니다.(여기서는 백업이 정상적으로 완료된 상태일때를 가정으로 두겠습니다.)
        
        1. 먼저 진행중인 상태의 백업이 있는지 확인합니다.
        2. 최근 백업 시간을 조회합니다.
        3. 최근 백업 시간과 직원 수정 이력 테이블(ChangeLogs)에서 조회한 최근 수정 이력 시간을 비교하여
        백업 생성 여부를 확인합니다.
        4. 백업 생성 여부가 참으로, 백업을 수행하기 위해 직원(Employees) 테이블을 조회합니다.
        5. 조회된 데이터를 csv파일로 생성합니다.
        6. 백업 상태를 완료처리합니다.
        
        이때 ChangeLogs 테이블 조회와 Employees 테이블 조회 시점이 다르기 때문에
        
        Employees 테이블 조회 전 직원 수정(직원 추가, 삭제, 정보 수정) 이력이 발생하면 조회 시점별 데이터 불일치 문제가 발생합니다.
        
    - 해결방법
        
        `startBackup` 메서드에 `@Transactional` 애노테이션을 달아 트랜잭션 메서드로 만들어주고,
        
        격리 수준을 `REPEATABLE READ`로 주어 조회 시 항상 같은 결과가 나오도록(데이터 정합성 보장) 개선하였습니다.
        
        ```java
        @Transactional(isolation = Isolation.REPEATABLE_READ)
        ```
        

## 라이선스

본 프로젝트는 HR Bank 백엔드 시스템 구현을 위한 프로젝트입니다.
