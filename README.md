# Fortune Teller MicroServices - Fortune API

## Overview
This repository is a microservice of the larger Fortune Teller Application guided as a workshop. This is a thin API layer using Spring MVC that consumes the [Fortune Service](https://github.com/bernardpark/fortune-teller-service) backend.

## Service Registry
By including spring-cloud-services-starter-service-registry, your application will now be registered in your Service Registry service instance in Pivotal Cloud Foundry.

Take a look at src/main/resources/bootstrap.yml as well. This properties file, to be read before your application.yml file, define s the name of this application when it is registered in your Service Registry. In the future, you may build out your microservices to reference each service by its registered name.

## Spring Cloud Config
Examine the `FortuneProperties.java` class. You should notice the `@ConfigurationProperties` annotation. This tells Spring that this class will act as a properties object consuming your remote configuration. The annotation includes `prefix = "service"` that identifies the configuration prefix. The `@RefreshScope` annotation, as included by the Spring Actuator dependency, will force recreation of this bean when the actuator/refresh endpoint is called. This will pull in the latest configuration from your config server when changes are made. 
Be sure to note the `serviceURL` variable. Notice that it refers to the spring application naming convention, not an http/https url, we specify in our bootstrap.yml files, which is used to register application names in Service Registry.
In this series of applications, the Config Server should consume a git repository backend with a simple `application.yml` file. Re
fer to the [Fortune Config](https://github.com/bernardpark/fortune-teller-config) repository to see an example. You can notice in `deploy.sh` that a Config Server service is initialized with this particular configuration repository.

## Spring Cloud Registry
Now take a look at `ApiService.java`. The `randomFortune()` method, which is invoked in the method mapped to `/random` endpoint in `ApiController.java`, makes a rest call to the `serviceURL` endpoint we saw earlier. By doing so, this application is referring to the binded Service Registry to call the service application.

## Spring Cloud Circuit Breaker
In the same `ApiService.java` class, notice that the `randomFortune()` method is annotated with `@HystrixCommand( fallbackMethod = "fallbackFortune" )`. When this method fails, it falls back to the `fallbackFortune()` method below, to return a default response instead of an error code. This practice not only produces a non-critical error handler, but also helps implement a fail-fast methodology.

## Deploying the Application
<a href="https://push-to.cfapps.io?repo=https%3A%2F%2Fgithub.com%2Fmsathe-tech%2Ffortune-teller.git">
        <img src="https://push-to.cfapps.io/ui/assets/images/Push-to-Pivotal-Light.svg" width="200" alt="Push">
</a>

### Or

Build and deploy application on current 'cf target'

```
./deploy.sh
```
When prompted for the App Suffix, give a unique identifier. This is to ensure that there is no overlap in cf application names whe
n pushing.

This deploy script does the following.
1. Build your applications with Maven
1. Create the necessary services on Pivotal Cloud Foundry
1. Push your applications

Examine the manifest.yml file to review the application deployment configurations and service bindings.

## Test the application

### Test the API endpoint
1. Make sure the [Fortune Service](https://github.com/bernardpark/fortune-teller-service) is deployed in the same environment.
1. Visit `https://$YOUR_API_ENDPOINT/random`
1. Notice the random fortune returned
1. Refresh the page
1. Notice another random fortune returned

### Test Circuit Breaker
1. Stop your [Fortune Service](https://github.com/bernardpark/fortune-teller-service) (ex. `cf stop $YOUR_APP_SUFFIX-fortune-service`)
1. Visit `https://$YOUR_API_ENDPOINT/random`
1. Notice the default fallback message

### Test Cloud Config
1. Make a change to you `application.yml` with a new fallback message
1. Refresh your application beans using the actuator endpoint (ex. `curl -k https://$YOUR_API_ENDPOINT/actuator/refresh -X POST`)
1. Visit `https://$YOUR_API_ENDPOINT/random`
1. Notice the changed default fallback message

## Clean up

You can choose to clean up your environment, or keep it for the next lab.

```
./scripts/undeploy.sh
```

## Return to Workshop Respository
[Fortune Teller Workshop](https://github.com/msathe-tech/fortune-teller)

## Authors
* **Bernard Park** - [Github](https://github.com/bernardpark)
* **Madhav Sathe** - [Github](https://github.com/msathe-tech)
