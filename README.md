![Image](https://github.com/user-attachments/assets/08441c05-696d-48bd-8f78-9cc2e2b9dfa8)

# DongneHankki-BE (동네한끼 백엔드)

우리 동네의 맛집을 공유하고 추천받는 소셜 다이닝 서비스 '동네한끼'의 백엔드 API 서버입니다. 사용자들은 위치 기반으로 주변 음식점에 대한 게시글을 작성하고, 다른 사용자들과 소통하며, AI 기반의 메뉴 추천을 받을 수 있습니다.

# 🤔 서비스 개요


### **목표 사용자**
- 자신의 위치 근처 맛집을 찾고자 하는 모든 사람
- SNS 마케팅에 어려움을 겪는 모든 소상공인, 무료로 AI 마케팅 리포트를 얻고자 하는 모든 소상공인


<br>


### **Needs**
- 사진 업로드 및 키워드를 통한 자동 SNS 홍보 글 생성
- 유저 행위 기반의 AI 마케팅 리포트 생성

<br>

### **Solution 및 기대효과**
**(1) 간편한 여행지 선택**
- 

**(2) 일정 생성 및 공유에 대한 시간 절약**
- 

**(3) 선택지를 통한 개인화된 추천**
- 

<br>

### **서비스 차별성**

**AI를 활용한 선택지 제공**

- 다양한 선택지를 생성해 사용자의 선택에 대한 다양한 여행지를 추천한다.


**선택 기반 맞춤형 추천**

- 간단한 선택 과정을 통해 사용자가 원하는 여행지를 추천받는 독특한 방식으로, 복잡한 입력 없이도 개인화된 결과를 제공한다.




## ✨ 주요 기능

- **사용자**: JWT를 이용한 안전한 사용자 인증 및 프로필 관리를 제공합니다.
- **게시글**: 음식점 방문 후기를 담은 게시글을 작성, 조회, 수정, 삭제할 수 있으며, 이미지 업로드를 지원합니다.
- **댓글 & 좋아요**: 게시글에 대한 댓글을 작성하고 다른 사용자들과 소통하며, 관심 있는 게시글에 '좋아요'를 누를 수 있습니다.
- **상점**: 음식점의 기본 정보, 메뉴, 운영 시간 등을 관리하고, 공공데이터 API와 연동하여 정보를 동기화합니다.
- **지도**: 현재 위치를 기반으로 주변의 음식점과 등록된 게시글을 지도 위에서 한눈에 확인할 수 있습니다.
- **팔로우**: 다른 사용자를 팔로우하여 그들의 활동 소식을 받아볼 수 있습니다.
- **알림**: 새로운 팔로워, 댓글, 좋아요 등 주요 활동에 대해 FCM을 통한 실시간 푸시 알림을 보냅니다.
- **추천**: Google Cloud Vertex AI를 활용하여 사용자의 취향과 주변 음식점 메뉴를 분석해 음식을 추천합니다.
- **분석**: 사용자 활동 로그를 기록하여 서비스 개선을 위한 데이터 기반을 마련합니다.

## 🛠️ 기술 스택

- **Backend**: Java, Spring Boot, Spring Security, JPA (Hibernate)
- **Database**: Redis
- **Cloud & DevOps**: AWS S3, Docker, GitHub Actions
- **External Services**: Firebase Cloud Messaging (FCM), Google Cloud Vertex AI
- **API & Documentation**: REST API, Swagger (OpenAPI)

## 🏗️ 프로젝트 구조

프로젝트는 역할 분리를 위해 계층형 아키텍처(Layered Architecture)를 따릅니다.

- `presentation`: API 엔드포인트를 정의하고 HTTP 요청/응답을 처리합니다.
- `application`: 핵심 비즈니스 로직을 서비스 계층에서 처리합니다.
- `domain`: 엔티티, VO 등 도메인 모델을 정의합니다.
- `infrastructure` / `repository`: 데이터베이스 연동, 외부 API 호출 등 인프라 관련 로gic을 처리합니다.

## 📖 API 문서

Swagger (OpenAPI)를 통해 API 명세를 실시간으로 확인할 수 있습니다. 서버 실행 후 `/swagger-ui/index.html` 경로로 접속하여 모든 API를 테스트할 수 있습니다.






<br>

# 📜 API 명세서


<br>

# 📁 ERD
### MySQL
![ERD](./Docs/img/DBERD.png)


<br>

# 🗺️ 시스템 아키텍처
![아키텍처](./Docs/img/system.png)
<br>


# 💻 백엔드

## 👍 공통 사항

- 단위 테스트 작성(service 메소드 별로) : Kotest 사용
- 다른 사람이 알아보기 쉽도록 주석처리해야 합니다. (controller, service 메서드마다)
    - javadoc 형식 https://jake-seo-dev.tistory.com/59
- issue 생성 및 PR을 통해 본인이 구현한 부분에 대한 기록을 남겨야 합니다.
- 테스트 및 원할한 서버 운영을 위한 로그를 작성해야 합니다.(에러나 운영에 필요한 로그. 검색시 검색어와 같은 로그)
- 예외처리는 항상 잘 만들어두기 (code, message, data)
- 개발 기간 : 2025-05 ~ 


<br>

## 🛠️ 기술 스택

- #### Language, Framework, Library
  ![Springboot](https://img.shields.io/badge/Springboot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
  ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=flat-square&logo=Gradle&logoColor=white)
  ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)
    - JPA를 통해 SQL을 직접 작성하지 않아도 되므로 데이터베이스 작업에 소요되는 시간을 줄이고, 비즈니스 로직 구현에 집중 가능

- #### Test
  ![JUnit](https://img.shields.io/badge/JUnit-25A162?style=flat-square&logo=JUnit5&logoColor=white)
    - JUnit은 간단한 어노테이션 기반 설정으로 테스트 작성과 실행을 직관적이고 효율적으로 만들어줌

- #### CICD
  ![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=flat-square&logo=Jenkins&logoColor=white)
  ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=flat-square&logo=docker&logoColor=white)
    - Jenkins를 사용한 CI/CD 파이프라인은 자동화된 테스트, 빌드, 배포를 통해 개발 프로세스를 효율적으로 관리
    - Docker는 애플리케이션을 컨테이너로 패키징하여 일관된 실행 환경을 제공하고, 배포를 빠르고 효율적으로 수행 가능

- #### Database
  ![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=flat-square&logo=mysql&logoColor=white)
  ![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=flat-square&logo=MongoDB&logoColor=white)
  ![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=flat-square&logo=redis&logoColor=white)
    - MySQL은 뛰어난 성능과 확장성을 제공하며, 광범위한 커뮤니티 지원과 다양한 플랫폼에서의 안정성을 보장
    - MongoDB는 유연한 스키마 설계를 통해 다양한 데이터를 효율적으로 저장하고, 빠른 쿼리 성능을 제공
    - Redis는 인메모리 데이터 저장소로 초고속 데이터 접근과 다양한 데이터 구조를 지원

- #### API 테스트, 명세서
  ![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=flat-square&logo=notion&logoColor=white)
  ![Spring REST Docs](https://img.shields.io/badge/Spring%20REST%20Docs-6DB33F?style=flat-square&logo=spring&logoColor=white)
  ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=white)
    - RestDocs를 통해 생성된 문서를 Swagger UI로 시각화하여, 개발자와 비개발자 모두가 실시간으로 API를 테스트 가능
    - 테스트 코드 작성과 함께 API 문서가 자동으로 생성되어, 실제 코드와 문서의 동기화 문제가 발생하지 않음
    - 테스트 시에 문서를 검증할 수 있어 신뢰성을 높임

- #### 🙏 협업 툴
  ![Notion](https://img.shields.io/badge/Notion-000000.svg?style=flat-square&logo=notion&logoColor=white)
  <br>


<br>

# 👧‍👦 팀 소개


| **분야** | **이름** | **포지션**     | **내용**       |
|:------:|:------:|-------------|--------------|
|   개발   |  정소현   | 💻 백엔드      | API 구현       |
|   개발   |  박준서   | 💻 백엔드      | API 구현,서버 배포 |
|   개발   |  황호연   | 📱  프론트엔드   |              |
|   개발   |  황혜성   | 📱  프론트엔드   |              |
|  디자인   |  권예경   | 📱  앱,웹 디자인 |              |

<br>
