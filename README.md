# 🎮 FPlist - 지오메트리 대쉬 프레임 퍼펙트 정보 사이트

## 📖 프로젝트 소개

**FPlist**는 지오메트리 대쉬(Geometry Dash) 게임의 프레임 퍼펙트(Frame Perfect) 레벨들의 상세 정보를 제공하는 웹 애플리케이션입니다.

### 🎯 주요 기능

- **📊 FPS별 프퍼펙 구간 분석**: 60fps, 120fps, 240fps에서의 1프레임 점프 요구 횟수
- **🔢 난이도 시스템**: 프퍼펙 구간 개수 기반 자동 Rating 계산
- **🔍 고급 필터링**: 상태별(R/V/C/U) 및 레벨명 검색
- **📱 반응형 디자인**: Bootstrap 기반 모던 UI
- **⚡ 실시간 정렬**: 난이도순 자동 정렬

### 💡 프퍼펙이란?

프레임 퍼펙트(Frame Perfect)는 특정 FPS에서 정확히 1프레임 타이밍에만 성공할 수 있는 점프 구간을 의미합니다.
- **60fps**: 거친 판정으로 많은 구간이 프퍼펙
- **240fps**: 정밀한 판정으로 대부분이 일반 점프로 처리

## 🛠️ 기술 스택

### Backend
- **Spring Boot 3.5.3** - 메인 프레임워크
- **Java 21** - 프로그래밍 언어
- **Thymeleaf** - 템플릿 엔진
- **Jackson** - JSON 처리

### Frontend
- **Bootstrap 5.1.3** - UI 프레임워크
- **Thymeleaf** - 서버사이드 렌더링
- **Responsive Design** - 모바일 지원

### Infrastructure
- **Google Cloud App Engine** - 배포 환경
- **Gradle** - 빌드 도구

## 🚀 빠른 시작

### 사전 요구사항
- Java 21+
- Gradle 7.0+

### 로컬 실행
```bash
# 프로젝트 클론
git clone https://github.com/[username]/FPlist.git
cd FPlist

# 애플리케이션 실행
./gradlew bootRun
```

웹 브라우저에서 `http://localhost:8080` 접속

### 빌드
```bash
# 프로덕션 빌드
./gradlew clean build

# 테스트 제외 빌드
./gradlew clean build -x test
```

## 📁 프로젝트 구조

```
FPlist/
├── src/main/
│   ├── java/inhatc/fplist/
│   │   ├── FPlistApplication.java      # 메인 애플리케이션
│   │   ├── controller/
│   │   │   ├── HomeController.java     # 홈페이지 컨트롤러
│   │   │   └── LevelController.java    # 레벨 목록 컨트롤러
│   │   ├── model/
│   │   │   └── Level.java              # 레벨 데이터 모델
│   │   └── service/
│   │       └── LevelService.java       # 비즈니스 로직
│   ├── resources/
│   │   ├── fplist_data.json           # 레벨 데이터
│   │   ├── application.properties      # 설정 파일
│   │   └── templates/
│   │       ├── index.html             # 홈페이지
│   │       ├── levels.html            # 레벨 목록
│   │       └── welcome.html           # 소개 페이지
│   └── appengine/
│       └── app.yaml                   # GCP 배포 설정
├── build.gradle                      # Gradle 빌드 설정
└── deploy.sh                         # 배포 스크립트
```

## 📊 데이터 구조

### Level JSON 구조
```json
{
  "level_name": "Aeternus",
  "level_link": "https://youtu.be/o3l6vCrmNoc",
  "fps_data": {
    "fps_60": 212,    // 60fps에서 프퍼펙 구간 수
    "fps_120": 78,    // 120fps에서 프퍼펙 구간 수
    "fps_240": 3      // 240fps에서 프퍼펙 구간 수
  },
  "rating": 380,      // 난이도 점수
  "status": {
    "code": "U",      // R: 공식인증, V: 비공식검증, C: 취소됨, U: 미검증
    "description": "unverified"
  },
  "level_id": "102646926",
  "comment": "특별한 주의사항"
}
```

### Rating 계산 공식
```
Rating = fps_60 + (fps_120 × 2) + (fps_240 × 4)
```

## 🌐 배포

### Google Cloud App Engine 배포
```bash
# GCP 프로젝트 설정
gcloud config set project YOUR_PROJECT_ID

# 배포 실행
./deploy.sh
```

자세한 배포 가이드는 [GCP_SETUP.md](GCP_SETUP.md) 참조

## 🔧 API 엔드포인트

| 엔드포인트 | 설명 | 파라미터 |
|-----------|------|----------|
| `GET /` | 홈페이지 | - |
| `GET /levels` | 레벨 목록 | `status`, `search` |
| `GET /welcome` | 소개 페이지 | - |

### 사용 예시
```bash
# 전체 레벨 목록
curl http://localhost:8080/levels

# 상태별 필터링
curl http://localhost:8080/levels?status=R

# 레벨명 검색
curl http://localhost:8080/levels?search=Aeternus
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 👨‍💻 개발자

- **개발자**: [Your Name]
- **이메일**: [your.email@example.com]
- **GitHub**: [@yourusername](https://github.com/yourusername)

## 🙏 감사의 말

- 지오메트리 대쉬 커뮤니티의 프퍼펙 데이터 수집에 기여한 모든 분들
- Spring Boot 및 Thymeleaf 개발팀
- Bootstrap 개발팀

---

⭐ 이 프로젝트가 도움이 되었다면 스타를 눌러주세요! 