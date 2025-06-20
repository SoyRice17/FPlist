# 🚀 FPlist GCP App Engine 배포 가이드

## 🔧 사전 준비

### 1. Google Cloud SDK 설치
```bash
# macOS (Homebrew)
brew install --cask google-cloud-sdk

# 또는 직접 다운로드
# https://cloud.google.com/sdk/docs/install
```

### 2. GCP 프로젝트 생성 및 설정
```bash
# GCP 로그인
gcloud auth login

# 새 프로젝트 생성 (프로젝트 ID는 고유해야 함)
gcloud projects create fplist-project-$(date +%Y%m%d) --name="FPlist"

# 프로젝트 설정
gcloud config set project fplist-project-$(date +%Y%m%d)

# App Engine 앱 생성 (지역 선택: asia-northeast3는 서울)
gcloud app create --region=asia-northeast3

# 결제 계정 연결 (필수)
gcloud billing projects link fplist-project-$(date +%Y%m%d) --billing-account=YOUR_BILLING_ACCOUNT_ID
```

### 3. 프로젝트 설정 파일 수정
1. `build.gradle`에서 `your-gcp-project-id`를 실제 프로젝트 ID로 변경
2. `deploy.sh`에서 URL도 해당 프로젝트 ID로 변경

## 📋 배포 단계

### 1. 로컬 테스트
```bash
cd FPlist
./gradlew bootRun
```

### 2. App Engine 배포
```bash
# 배포 스크립트 실행
./deploy.sh

# 또는 수동 배포
./gradlew clean build -x test
gcloud app deploy src/main/appengine/app.yaml
```

### 3. 배포 확인
```bash
# 애플리케이션 열기
gcloud app browse

# 로그 확인
gcloud app logs tail -s default

# 서비스 상태 확인
gcloud app services list
```

## 💰 비용 관리

### 인스턴스 설정 (app.yaml)
- `F1`: 무료 티어 (월 28시간 제한)
- `F2`: 소규모 트래픽
- `F4`: 중간 트래픽

### 자동 스케일링 설정
```yaml
automatic_scaling:
  min_instances: 0    # 트래픽 없을 때 인스턴스 0개
  max_instances: 2    # 최대 2개 인스턴스
  target_cpu_utilization: 0.6
```

## 🔍 모니터링

### Cloud Console에서 확인
- 트래픽: https://console.cloud.google.com/appengine/services
- 로그: https://console.cloud.google.com/logs
- 오류: https://console.cloud.google.com/errors

### 주요 명령어
```bash
# 버전 관리
gcloud app versions list
gcloud app versions delete VERSION_ID

# 서비스 중지
gcloud app services set-traffic default --splits VERSION_ID=0

# 커스텀 도메인 연결
gcloud app domain-mappings create DOMAIN_NAME
```

## 🛠️ 트러블슈팅

### 일반적인 문제
1. **빌드 실패**: Java 21 지원 확인
2. **메모리 부족**: 인스턴스 클래스 업그레이드
3. **시작 시간 초과**: 애플리케이션 최적화 필요

### 유용한 명령어
```bash
# 애플리케이션 로그 실시간 모니터링
gcloud app logs tail -s default

# 특정 버전으로 트래픽 라우팅
gcloud app services set-traffic default --splits v1=100

# 인스턴스 목록 확인
gcloud app instances list
``` 