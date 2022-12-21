Requirements:
Java 17
Kotlin 1.7+
Docker

##Build Project:
###Windows:
gradlew clean build
###Linux:
./gradlew clean build
###Docker:
```docker build -t vcs-repository-search-image .```
```docker run -p 8080:8080 --name vcs-repository-search vcs-repository-search-image```

##Run project
1.When project is built and integration test are passed, check status of running instance.
- localhost:8080/actuator/health

2.Open specification
- localhost:8080/swagger-ui/

3.Retrieve all repositories the latest commits for branches. Use API
- /api/v1/repositories/owner/{ownerName}

4.Retrieve repositories the latest commits for branches with pagination. Use API with query params
- /api/v1/repositories/owner/{ownerName}?page={page}&size={size}

5.Use one of hypermedia links from response or use another API for more precise results

Tip: Swagger forbid to provide to allowed "Accept" header. 
In this case can be used Postman or curl.
When provided forbidden "Accept" header, like "xsl/application", 
error response will be sent in json format.

##CI/CD
1. Install Jenkins locally, use docker commands:
- ```docker network create jenkins```
- ```docker run --name jenkins-docker --rm --detach ^ 
  --privileged --network jenkins --network-alias docker ^
  --env DOCKER_TLS_CERTDIR=/certs ^
  --volume jenkins-docker-certs:/certs/client ^
  --volume jenkins-data:/var/jenkins_home ^
  --publish 2376:2376 ^
  docker:dind
  ```
- ```docker build -t myjenkins-blueocean:latest-jdk17 -f Dockerfile.jenkins .```
- ```docker run --name jenkins-blueocean-17 --restart=on-failure --detach ^
  --network jenkins --env DOCKER_HOST=tcp://docker:2376 ^
  --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1 ^
  --volume jenkins-data:/var/jenkins_home ^
  --volume jenkins-docker-certs:/certs/client:ro ^
  --publish 8080:8080 docker:dind --publish 50000:50000 myjenkins-blueocean:latest-jdk17
  ```
  
Additional info about it can be found here https://www.jenkins.io/doc/book/installing/docker/
