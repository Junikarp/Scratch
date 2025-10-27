# SecurityFilterChain 등록

## 커스텀 SecurityFilterChain 등록
> 스프링 시큐리티 의존성을 추가하면 기본적으로 `DefaultSecurityFilterChain` 하나가 등록됨
> 
> 내가 원하는 `SecurityFilterChain` 을 등록하려면 `SecurityFilterChain` 을 리턴하는 `@Bean` 메소드를 등록하면 됨

* 커스텀 SecurityFilterChain 한 개 등록
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(httpSecurity http) throws Exception {
        return http.build;
    }
}
```

* 커스텀 SecurityFilterChain 두 개 등록
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(httpSecurity http) throws Exception {
        return http.build;
    }

    @Bean
    public SecurityFilterChain securityFilterChain2(httpSecurity http) throws Exception {
        return http.build;
    }
}
```

### 멀티 SecurityFilterChain 설정 시 하나 선택
> `FilterChainProxy`는 N개의 SecurityFilterChain 중 하나를 선택해서 요청을 전달함

체인의 선택 기준은 아래와 같음
1. 등록된 인덱스 순서
2. 필터 체인에 대한 `RequestMatcher` 값이 일치하는지 확인

### 멀티 SecurityFilterChain 경로 설정 (필수)
* 문제 상황
    * 멀티 `SecurityFilterChain` 설정 시 N 개의 `SecurityFilterChain` 이 모두 `/**` 경로에서 매핑됨
    * 예를 들어 2개의 필터 체인이 존재할 때 두 체인이 모두 모든 경로에 대해 매핑되어 있다면 무조건 첫번째로 등록된 체인만 거치게 됨(등록 인덱스 순으로 전달되기 때문)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(httpSecurity http) throws Exception {
        http
                .authorizeHttpRequest((auth) -> auth
                        .requestMatchers("/user").parmitAll());
        return http.build;
    }

    @Bean
    public SecurityFilterChain securityFilterChain2(httpSecurity http) throws Exception {
        http
                .authorizeHttpRequest((auth) -> auth
                        .requestMatchers("/admin").parmitAll());
        return http.build;
    }
}
```

예를 들어 위와 같은 코드에서는 `securityFilterChain`이 `securityFilterChain2`보다 먼저 등록되어 있기 때문에 `/admin`경로로 요청을 보내더라도 잘못된 응답이 발생한다. 

이 상황에서는 경로 설정을 추가적으로 하지 않으면 `/**` 경로로 반응한다.

따라서 `securityFilterChain`으로 요청이 전달되고 `/admin` 에 대한 설정이 없기 때문에 거부가 발생한다.

이러한 문제를 해결하기 위해서는 아래와 같이 `securityMatchers`를 이용한 경로 매핑이 필요하다.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(httpSecurity http) throws Exception {
        http
                .securityMatchers((auth) -> auth.requestMatchers("/user"));
        http
                .authorizeHttpRequest((auth) -> auth
                        .requestMatchers("/user").parmitAll());
        return http.build;
    }

    @Bean
    public SecurityFilterChain securityFilterChain2(httpSecurity http) throws Exception {
        http
                .securityMatchers((auth) -> auth.requestMatchers("/admin"));
        http
                .authorizeHttpRequest((auth) -> auth
                        .requestMatchers("/admin").parmitAll());
        return http.build;
    }
}
```
위와 같이 별도의 경로에 대해서 매핑을 해주어야만 여러개의 체인에 대해서 올바른 응답을 제공받는 것이 가능하다.

### 멀티 SecurityFilterChain 에 대해 등록 순서 설정
> `@Order` 어노테이션을 선언하고 뒤에 괄호를 붙힌 후 순서를 정하면 된다. 예시로 알아보자

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(httpSecurity http) throws Exception {
        http
                .securityMatchers((auth) -> auth.requestMatchers("/user"));
        http
                .authorizeHttpRequest((auth) -> auth
                        .requestMatchers("/user").parmitAll());
        return http.build;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain2(httpSecurity http) throws Exception {
        http
                .securityMatchers((auth) -> auth.requestMatchers("/admin"));
        http
                .authorizeHttpRequest((auth) -> auth
                        .requestMatchers("/admin").parmitAll());
        return http.build;
    }
}
```
위와 같이 괄호 안의 숫자를 이용해 등록 순서를 지정하는 것도 가능하다.

### 특정한 요청은 필터를 거치지 않도록 하는 방법
> 정적 자원 등 필터를 통과하지 않게 하고 싶은 자원들을 등록할 수 있다.
> 설정 시 하나의 `SecurityFilterChain` 이 0번 인덱스로 설정되어 해당 필터 체인 내부에는 체인이 없는 상태로 생성된다.

```java
@Bean
public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().requestMatcher("/img/**");
}
```

위와 같이 설정 가능하다.
