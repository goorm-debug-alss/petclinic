<div id="top"></div>

<div align='center'>
<h1><b>☁️ 9oormthon DEEP DIVE IN Goorm (버깅 / 디버깅 프로젝트) ☁️</b></h1>
<h3><b>(버깅 / 디버깅 프로젝트)</b></h3>

</div>

## 프로젝트 소개
이 프로젝트는 [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) 프로젝트를 리팩토링하고 개선하는 것을 목표로 합니다.

- **리팩토링**: 기존 프로젝트는 `Controller`를 사용하고 있었으나, 이를 `RestController`로 변경하여 RESTful 방식으로 API를 제공하도록 개선.
- **기능 확장**: 기본적인 기능 외에 새로운 기능을 추가하여 프로젝트의 확장성을 높임.

### 주요 문서
- [프로젝트 노션 링크](https://goormkdx.notion.site/4-159c0ff4ce3180249c51e3a87d19d9cf)
- [API 명세](https://www.notion.so/goormkdx/cc89244e7aa0442aa72238f6564b0863?v=06acc662c9d241d7a891252828740036)
- [단위 테스트 명세](https://goormkdx.notion.site/fe1795ab4ef14e7a9c541e1c4b715127?v=c9dfe9f1ad9c4173949554f2124c73a4)
- [PetClinic-프로젝트 소개.pdf](https://github.com/user-attachments/files/18386006/PetClinic-.pdf)

## 0. 목차

1. [팀원 소개](#1)
2. [사용 기술](#2)
3. [디렉토리 구조](#3)
4. [브랜치 전략](#4)
5. [개선 사항](#5)

<br>

## <span id="1">🏃 1. 팀원 소개</span>

<div align="center">

| 역할 | 이름  | GitHub |                      주요 담당                      |
|:--:|:---:|:------:|:-----------------------------------------------:|
| 팀장 | 이지수 | [GitHub](https://github.com/leedidu) |    프로젝트 관리 및 일정 조율, 예외처리 및 Vet API 개발 및 테스트     |
| 팀원 | 박정현 | [GitHub](https://github.com/Do-oya) | Owner API, Appointment API, Review API 개발 및 테스트 |
| 팀원 | 송준환 | [GitHub](https://github.com/junhwan98) |         Visit API, History API 개발 및 테스트         |
| 팀원 | 정태민 | [GitHub](https://github.com/Jung-Taemin) |                Pet API 개발 및 테스트                 |
| 팀원 | 박세희 | [GitHub](https://github.com/popcifox) |               Review API 개발 및 테스트               |

</div>

<br>

## <span id="2">📌 2. 사용 기술

### 💻 기술 스택
- **Java 17**: 최신 장기 지원(LTS) 버전으로, 향상된 성능과 새로운 기능 활용
- **Spring Boot 3.4.0**: 애플리케이션 개발을 간소화하기 위한 프레임워크로 데이터베이스와의 통합, RESTful API 설계에 활용
- **Spring Security**: 애플리케이션 보안을 위한 인증 및 권한 관리
- **JPA (Java Persistence API)**: 데이터베이스와의 상호작용을 간단하게 처리
- **MySQL**: 관계형 데이터베이스 관리 시스템(RDBMS)으로 데이터 저장 및 관리
- **JUnit 5**: 테스트 자동화를 위한 프레임워크
- **Mockito**: 단위 테스트에서 객체의 동작을 모킹(mocking)하기 위한 라이브러리
- **Swagger UI**: API 문서화 및 테스트를 위한 도구

### ⚙️ 개발 환경
- **Gradle**: 프로젝트 빌드 및 의존성 관리를 위한 도구
- **SpringBootTest**: Spring Boot 애플리케이션의 통합 테스트를 지원
- **Notion**: 프로젝트 관리와 협업 도구로 사용

<br>


## <span id="3">🗂️ 3. 디렉토리 구조</span>

```plaintext
project-root/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── org/springframework/samples/petclinic/
│   │   │   │   ├── common/
│   │   │   │   │   ├── error/
│   │   │   │   │   ├── exception/
│   │   │   │   │   └── result/
│   │   │   │   ├── config/
│   │   │   │   │   ├── security/
│   │   │   │   │   └── web/
│   │   │   │   ├── domain/
│   │   │   │   │   ├── appointment/
│   │   │   │   │   ├── history/
│   │   │   │   │   ├── owner/
│   │   │   │   │   ├── pet/
│   │   │   │   │   ├── review/
│   │   │   │   │   ├── token/
│   │   │   │   │   ├── vet/
│   │   │   │   │   └── visit/
│   │   │   │   ├── handler/
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   ├── interceptor/
│   │   │   │   │   └── AuthorizationInterceptor.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── BaseEntity.java
│   │   │   │   │   └── package-info.java
│   │   │   │   ├── system/
│   │   │   │   │   ├── CacheConfiguration.java
│   │   │   │   │   ├── CrashController.java
│   │   │   │   │   └── WelcomeController.java
│   │   │   │   ├── PetClinicApplication.java
│   │   │   │   └── PetClinicRuntimeHints.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── build.gradle
├── README.md
└── ...
```

<br>

## <span id="4">🌲 4. 브랜치 전략</span>

- **main**: 배포를 위한 최상위 브랜치
- **develop**: 다음 배포 버전을 개발하는 브랜치 (main 브랜치에서 생성)
- **feature/**: 기능 개발을 위한 브랜치 (develop 브랜치에서 생성)
- **hotfix/**: 배포 버전에서 발생한 버그를 수정하는 브랜치 (main 브랜치에서 생성)

### 병합 기준
- **hotfix -> main**: 모든 버그를 수정하여 배포에 문제가 되지 않을 경우
- **develop -> main**: 모든 기능 개발 및 버그 수정이 완료되었을 경우
- **feature -> develop**: 개발 기능이 완료되었을 경우

### PR 규칙
1. 기능 개발, 버그 수정 등 모든 개발 완료 시 PR 요청
2. PR 요청 시 작업한 내용 명시
3. 관련된 이슈 태그 추가
4. 리뷰어 설정 및 승인 후 병합
5. 병합 후 관련된 브랜치 삭제

<br>

## <span id="5">🚀 5. 개선 사항</span>
1. 데이터베이스 수정

2. RESTful API로 변경

3. 보안 강화

4. CI/CD 파이프라인 구축

<br>




