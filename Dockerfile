# Multi-stage build
FROM gradle:8.5-jdk17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 파일들 복사 (캐시 최적화)
COPY build.gradle settings.gradle gradlew ./
COPY gradle/ gradle/

# 의존성 다운로드 (캐시 레이어)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src/ src/

# 애플리케이션 빌드
RUN ./gradlew build --no-daemon -x test

# Runtime 이미지
FROM openjdk:17-jre-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 환경변수 설정
ENV SPRING_PROFILES_ACTIVE=docker
ENV TZ=Asia/Seoul

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
