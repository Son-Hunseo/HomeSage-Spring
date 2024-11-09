# 베이스 이미지로 OpenJDK 17을 사용
FROM maven:3.8.5-openjdk-17-slim AS build

# 필수 패키지 설치 및 작업 디렉토리 설정
RUN apt-get update

WORKDIR /app

# 필요한 파일들을 복사
COPY pom.xml .
COPY src src

# 애플리케이션 빌드
RUN mvn clean package -DskipTests

# 빌드 결과물을 실행할 새로운 스테이지
FROM openjdk:17-slim

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/target/homesage-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]