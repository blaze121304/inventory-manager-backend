# 멀티 스테이지 빌드를 위한 베이스 이미지
FROM openjdk:21-jdk-slim as builder

# 작업 디렉토리 설정
WORKDIR /app

# Maven Wrapper와 pom.xml 복사
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# 의존성 다운로드 (캐시 최적화)
RUN ./mvnw dependency:go-offline -B

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드
RUN ./mvnw clean package -DskipTests

# 런타임 이미지
FROM openjdk:21-jdk-slim

# 운영 환경 설정
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/target/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 로그 디렉토리 생성
RUN mkdir -p /app/logs

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# 헬스 체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
