#builder

FROM maven:3.6.3-jdk-11-slim AS build  
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

#package

FROM gcr.io/distroless/java  
COPY --from=build /usr/src/app/target/studying_squirrels_api-1.0.0-SNAPSHOT.jar /usr/app/studying_squirrels_api-1.0.0-SNAPSHOT.jar  
EXPOSE 8080  
ENTRYPOINT ["java","-jar","/usr/app/studying_squirrels_api-1.0.0-SNAPSHOT.jar"]  