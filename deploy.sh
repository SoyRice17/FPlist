#!/bin/bash

echo "🚀 FPlist App Engine 배포 시작..."

# 1. 프로젝트 빌드
echo "📦 프로젝트 빌드 중..."
./gradlew clean build -x test

# 2. App Engine 배포
echo "☁️ App Engine에 배포 중..."
gcloud app deploy src/main/appengine/app.yaml --project=fplist --quiet

echo "✅ 배포 완료!"
echo "🌐 애플리케이션 확인: https://fplist.du.r.appspot.com"
echo "📊 로그 확인: gcloud app logs tail -s default" 