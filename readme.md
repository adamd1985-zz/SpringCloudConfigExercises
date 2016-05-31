# Spring Cloud Configuration POC

## Abstract 
 
Set of tests and POCs that showoff the capability of spring to use versioned
and distributed configurations.
 
## POC Iterations
 
### 1: Pulling configurations from filesystem
 
#### Config Server

Wiring in spring cloud via dependencies:
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
            <artifactId>spring-cloud-config-starter</artifactId>
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

Enabling configuration servlet using __@EnableConfigServer__

Accessing configurations as follows:
* /{application}/{profile}[/{label}]
* /{application}-{profile}.yml
* /{label}/{application}-{profile}.yml
* /{application}-{profile}.properties
* /{label}/{application}-{profile}.properties

This access can be configured in the server's properties
```yaml
server:
  port: 8888

spring:
  config:
    name: configserver
  profiles:
    active: native

  cloud:
    config:
      server:
        native:
          search-locations:
            classpath:/test-config-repo/,
            classpath:/test-config-repo/{profile}/,
            classpath:/test-config-repo/{profile}/{label}/
 ```
 
 Example; if you access a specific profile of the default applications property
 on: __http://localhost:8888/SpringConfigClient/profile/__ where:
 * SpringConfigClient is the **application**
 * Profile is the **profile**
 * The label defaults to null (not used).
 ```json
 {
 	name: "SpringConfigClient",
 	profiles: [
 		"profile"
 	],
 	label: null,
 	version: null,
 	propertySources: [{
 		name: "classpath:/test-config-repo/SpringConfigClient-profile.yaml",
 		source: {
 			test.name: "client-profile-default"
 		}
 	}, {
 		name: "classpath:/test-config-repo/application-profile.yaml",
 		source: {
 			test.name: "test-profile-default"
 		}
 	}, {
 		name: "classpath:/test-config-repo/SpringConfigClient.yaml",
 		source: {
 			test.name: "client-default"
 		}
 	}, {
 		name: "classpath:/test-config-repo/application.yaml",
 		source: {
 			test.name: "test-default"
 		}
 	}]
 }
 ```
 Note the priorities of the configurations and their fallbacks:
  1. The application **SpringConfigClient** targeted profile is first.
  2. The default for all applications of that **profile**.
  3. the application's default.
  4. The default for **all applications**.
 
 If you want the default configurations of that profile only call: __http://localhost:8888/app/profile/label__ 
 (where  __app__ is an arbitrary application name):
 ```json
 {
 	name: "app",
 	profiles: [
 		"profile"
 	],
 	label: "label",
 	version: null,
 	propertySources: [{
 		name: "classpath:/test-config-repo/application-profile.yaml",
 		source: {
 			test.name: "test-profile-default"
 		}
 	}, {
 		name: "classpath:/test-config-repo/application.yaml",
 		source: {
 			test.name: "test-default"
 		}
 	}]
 }
 ```
 
 If you want the default for the application named: **SpringConfigClient**
 perform a get on:  __http://localhost:8888/SpringConfigClient/default__
 ```json
{
	name: "SpringConfigClient",
	profiles: [
		"default"
	],
	label: null,
	version: null,
	propertySources: [{
		name: "classpath:/test-config-repo/SpringConfigClient.yaml",
		source: {
			test.name: "client-default"
		}
	}, {
		name: "classpath:/test-config-repo/application.yaml",
		source: {
			test.name: "test-default"
		}
	}]
}
```
 
 Calling __http://localhost:8888/env__ will display the whole environment for the server including our configurations done above:
 ```json
 {
    applicationConfig: [classpath:/application.yaml]: {
        server.port: 8888,
        spring.config.name: "configserver",
        spring.profiles.active: "native",
        spring.cloud.config.server.native.search-locations: "classpath:/test-config-repo/, classpath:/test-config-repo/{profile}/, classpath:/test-config-repo/{profile}/{label}/"
    }
 }
 ```
 
#### Config Client

Wiring in spring cloud via dependencies:
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
```

Note that the __spring-boot-starter-actuator__ and its actuators are required if you wish to check *env*.

With __spring-cloud-starter-config__ it will by default attempt to connect to a config 
server using: __http://localhost:8888/__

To configure the client:
```yaml
server:
  port: 8080

spring:
    application:
      name: SpringConfigClient
    cloud:
      config:
        uri: http://localhost:8888
        name: SpringConfigClient
        profile: profiles
        label: label
    profiles:
      active: profiles
```

The client can configure how to access the config server:
* __spring.application.name__ is the same as __spring.cloud.config.name__. By default it will always take application name.
* __spring.application.profiles.active__ is the same as __spring.cloud.config.profile__. By default it will take any active profile.

### 2: Pulling configurations from GIT

Alter Server configuration to include this:
```yaml
# ${user.dir} points to the base directory you are running from.
  cloud:
    config:
      server:
        git:
          uri: file:///${user.dir}/SpringConfigServer/src/main/resources/test-git-repo
```

Note that we still follow the "native" approach, though through the use of a local GIT remote.

## References

* [Spring official configuration documentation](http://cloud.spring.io/spring-cloud-static/spring-cloud.html) 
* [Cloud workshop](https://github.com/spencergibb/cloud-native-workshop)