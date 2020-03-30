# spring-boot-security-scp
Spring Boot sample application with security for SAP Cloud Platform - Cloud Foundry

## Goal of this sample project

This [Spring Boot 2.0](http://projects.spring.io/spring-boot/) demo application shows how to implement basic access control in Spring based SAP Cloud Platform applications. It leverages [Spring Security 5.x](https://github.com/spring-projects/spring-security) and integrates to SAP Cloud Platform XSUAA service (OAuth Resource Server) using the [SAP Container Security Library (Java)](https://github.com/SAP/cloud-security-xsuaa-integration), which is available on [maven central](https://search.maven.org/search?q=com.sap.cloud.security).

In order to limit access to certain instances, you can restrict the access to specific function by Roles (Scopes). Or, even more fine granular, you can restrict the access on data level so that different users can see and maintain different subsets of the data instances depending on certain user dependent attribute values.

The microservice is a Spring boot version of the code developed in the [openSAP course: Cloud-Native Development with SAP Cloud Platform](https://open.sap.com/courses/cp5) and runs in the Cloud Foundry environment within SAP Cloud Platform.

> Note: The new `SAP Java Client Security Library` validates the access token, which is in JSON Web Token format, locally (offline). For verifying the signature of the access token it periodically retrieves and caches the JSON Web Keys (JWK) from the Authorization Server.
As consequence, in order to test our Spring Boot application locally, or as part of our JUnit tests, we have to provide a Mock Web Server that mocks the `/token_keys` endpoint that returns JWKs. Thus, this sample starts and configures a Mock Web Server for the OAuth 2.0 Authorization Server as explained [here](https://github.com/spring-projects/spring-security/tree/master/samples/boot/oauth2resourceserver). The mock server is only started in case the `uaamock` Spring profile is active.

## Local Execution
source localEnvironmentSetup.sh
mvn spring-boot:run

## Build and deploy
https://blogs.sap.com/2019/12/02/cloudfoundryfun-10-partial-deployments-to-cloud-foundry/

mbt build -p=cf
cf deploy spring-boot-security-scp_1.0.10.mtar
cf deploy spring-boot-security-scp_1.0.10.mtar -m  spring-boot-security-scp-approuter

## Using App Health Checks
https://docs.cloudfoundry.org/devguide/deploy-apps/healthchecks.html#health_check_timeout

APP_GUID=$(cf app --guid spring-boot-security-scp-backend)
PROCESS=$(cf curl "/v3/apps/${APP_GUID}/processes/web")
HEALTH_OBJECT=$(echo "${PROCESS}" | jq '{"health_check":.health_check}')
echo "${PROCESS}"
NEW_HEALTH_OBJECT=$(echo "${HEALTH_OBJECT}" | jq '.health_check.data.timeout |= 100')
echo "${HEALTH_OBJECT}"
PROCESS_GUID=$(echo "${PROCESS}" | jq -r '.guid')
echo "${PROCESS_GUID}"
cf curl -X PATCH -H "Content-Type: application/json" -d "${NEW_HEALTH_OBJECT}" "/v3/processes/${PROCESS_GUID}"
