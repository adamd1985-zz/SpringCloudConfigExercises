# Spring Cloud Configuration POC

## Abstract 
 
Set of tests and POCs that showoff the capability of spring to use versioned
and distributed configurations.
 
## POC Iterations
 
### 1: Pulling configurations from filesystem
 
* Wiring in spring cloud via dependencies:
```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-parent</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
```
* Enabling configuration servlet using __@EnableConfigServer__
* Accessing configurations as follows:
    * /{application}/{profile}[/{label}]
    * /{application}-{profile}.yml
    * /{label}/{application}-{profile}.yml
    * /{application}-{profile}.properties
    * /{label}/{application}-{profile}.properties
* Note that the above can be configured in the application properties search paths
* Application properties show bellow:
```yaml
 spring:
   profiles:
     active: native
 
   cloud:
     config:
       server:
         native:
           search-locations:
             classpath:/test-config-repo/,
             classpath:/test-config-repo/{application},
             classpath:/test-config-repo/{application}/{profile}/{label},
             classpath:/test-config-repo/{profile}/{label}
 ```
 
 
## References

* [Spring official configuration documentation](http://cloud.spring.io/spring-cloud-static/spring-cloud.html) 
