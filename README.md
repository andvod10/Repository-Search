Requirements:
Java 17
Kotlin 1.7+
Docker

## Build Project:
### Windows:
`gradlew clean build`
### Linux:
`./gradlew clean build`
### Docker:
```
docker build -t vcs-repository-search-image .
```
```
docker run -p 8080:8080 --name vcs-repository-search vcs-repository-search-image
```
### Docker-Compose:
Start Docker-Compose and rebuild image if exist
```
docker-compose up -d --build
```
Stop Docker-Compose with all inner services 
```
docker-compose down
```
Should be used any of [Docker](#docker) or [Docker-Compose](#docker-compose) approach

## Run project
1.When project is built and integration test are passed, check status of running instance.
- localhost:8080/actuator/health

2.Open specification
- http://localhost:8080/swagger-ui/index.html

3.Retrieve all repositories the latest commits for branches. Use API
- /api/v1/owner/{ownerName}/repositories

4.Retrieve repositories the latest commits for branches with pagination. Use API with query params
- /api/v1/owner/{ownerName}/repositories?page={page}&size={size}

5.Use one of hypermedia links from response or use another API for more precise results

Tip: Swagger forbid to provide to allowed "Accept" header. 
In this case can be used Postman or curl.
When provided forbidden "Accept" header, like "xsl/application", 
error response will be sent in json format.

## CI/CD
1. Install Jenkins locally, use docker commands:
### Linux
- ```
  docker network create jenkins
  ```
- ```
  docker run --name jenkins-docker --rm --detach \
  --privileged --network jenkins --network-alias docker \
  --env DOCKER_TLS_CERTDIR=/certs \
  --volume jenkins-docker-certs:/certs/client \
  --volume jenkins-data:/var/jenkins_home \
  --publish 2376:2376 \
  docker:dind --storage-driver overlay2
  ```
- ```
  docker build -t myjenkins-blueocean:latest-jdk17 -f Dockerfile.jenkins .
  ```
- ```
  docker run --name jenkins-blueocean --restart=on-failure --detach \
  --network jenkins --env DOCKER_HOST=tcp://docker:2376 \
  --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1 \
  --publish 8080:8080 --publish 50000:50000 \
  --volume jenkins-data:/var/jenkins_home \
  --volume jenkins-docker-certs:/certs/client:ro \
  myjenkins-blueocean:latest-jdk17
  ```
### Windows
- ```
  docker network create jenkins
  ```
- ```
  docker run --name jenkins-docker --rm --detach ^
  --privileged --network jenkins --network-alias docker ^
  --env DOCKER_TLS_CERTDIR=/certs ^
  --volume jenkins-docker-certs:/certs/client ^
  --volume jenkins-data:/var/jenkins_home ^
  --publish 2376:2376 ^
  docker:dind
  ```
- ```
  docker build -t myjenkins-blueocean:latest-jdk17 -f Dockerfile.jenkins .
  ```
- ```
  docker run --name jenkins-blueocean --restart=on-failure --detach ^
  --network jenkins --env DOCKER_HOST=tcp://docker:2376 ^
  --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1 ^
  --volume jenkins-data:/var/jenkins_home ^
  --volume jenkins-docker-certs:/certs/client:ro ^
  --publish 8080:8080 --publish 50000:50000 myjenkins-blueocean:latest-jdk17
  ```

Additional info about it can be found here https://www.jenkins.io/doc/book/installing/docker/

2. Finish installation jenkins. 
- Open jenkins dashboard. It's located on localhost:8080
- Install plugins, choose to install suggested plugins if have no preferred
- Continue installation. Fill personal data
- On the next page choose url for access to jenkins

3. Create Job
- Choose option "Create a job"
- Fill Job name and choose Pipeline option
- Add description, if needed.
  Choose option "GitHub project", and fill `https://github.com/andvod10/Repository-Search`
  Optionally, can be added SCM trigger, for asking github if changes made. 
  Example `*/5 * * * *`, every 5 minutes.
- Pipeline Definition.
  Pipeline script from SCM -> SCM -> fill Repository url `https://github.com/andvod10/Repository-Search`
                                  -> add branch `*/blue-ocean-config`
  Script path `Jenkinsfile`
  Save

4. Add docker hub configs
- Open https://hub.docker.com/
- Choose account settings 
- Choose security
- Generate new Access Token
- Return to Jenkins
- Dashboard -> Settings Jenkins -> Manage Credentials -> System -> Global credentials -> Add credentials
- Fill with data:
```
Kind - Username and password
Scope - global
username - <your docker hub username>
password - <your access token from docker hub>
ID - docker-hub
Description - docker-hub
```
  
5. Add AWS configs
- Create new or reuse existing AWS account
- Open IAM service on AWS console
- Choose user groups, create new user group
- Provide name of group, add at least next policies:
  ```
  AmazonECS_FullAccess
  AWSCloudFormationFullAccess
  ```
- Continue to creation group
- In IAM manager Add new user
- Provide name, choose option `Access key - Programmatic access`
- Add user to created user group
- Finish creation user, and copy `Access Key ID` and `Secret Access Key`
- Return to Jenkins
- Dashboard -> Settings Jenkins -> Manage Credentials -> System -> Global credentials -> Add credentials
- Fill with data:
```
Kind - AWS Credentials (If not exist, check if AWS plugin attached)
Scope - global
ID - aws-credentials
Description - aws-credentials
Access Key ID - <your AWS Access Key ID>
Secret Access Key - <your AWS Secret Access Key>
```

6. Run job
- Choose "Open Blue Ocean"
- Run
- Check your docker hub. Should be added new image
- Open CloudFormation service on AWS, check if `stack-repository-search` stack has status CREATE_COMPLETE
- Open API Gateway service on AWS, in `repository-search-mapping` open stage `$default`
- Add URI `/api/v1/owner/andvod10/repositories?page=0&size=20` to the stage host and run in browser. List of repos rendered
- Open CloudFormation service, choose `stack-repository-search` stack, and delete. Pay attention if stack successfully deleted.
Otherwise, it will collect payment, even not used.
