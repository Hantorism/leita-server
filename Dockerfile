# 1단계: 빌드를 위한 이미지
FROM openjdk:21-jdk-slim AS build

# Gradle 설치 (최신 버전 다운로드)
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-8.2-bin.zip && \
    unzip gradle-8.2-bin.zip && \
    mv gradle-8.2 /opt/gradle && \
    ln -s /opt/gradle/bin/gradle /usr/local/bin/gradle && \
    rm gradle-8.2-bin.zip

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 파일을 복사하고 의존성 캐시를 설정
COPY gradlew .
COPY gradle/wrapper gradle/wrapper
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

# 실행 권한 부여
RUN chmod +x gradlew

# Gradle 빌드 실행 (애플리케이션 빌드)
RUN gradle build --no-daemon -x test

# 2단계: 실행을 위한 이미지
FROM openjdk:21-jdk-slim

# JAR 파일을 복사
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]