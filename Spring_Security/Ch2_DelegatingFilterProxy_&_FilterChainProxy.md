# DelegatingFilterProxy & FilterChainProxy

## DelegatingFilterProxy
> 서블릿 컨테이너로 들어오는 HTTP 요청을 가로채 FilterChainProxy 에게 위임하는 역할을 하는 프록시

```java
@AutoConfiguration(after = SecurityAutoConfiguration.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnClass({ AbstractSecurityWebApplicationInitializer.class, SessionCreationPolicy.class })
public class SecurityFilterAutoConfiguration {

	private static final String DEFAULT_FILTER_NAME = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME;

	@Bean
	@ConditionalOnBean(name = DEFAULT_FILTER_NAME)
	public DelegatingFilterProxyRegistrationBean securityFilterChainRegistration(
			SecurityProperties securityProperties) {
		DelegatingFilterProxyRegistrationBean registration = new DelegatingFilterProxyRegistrationBean(
				DEFAULT_FILTER_NAME);
		registration.setOrder(securityProperties.getFilter().getOrder());
		registration.setDispatcherTypes(getDispatcherTypes(securityProperties));
		return registration;
	}

	private EnumSet<DispatcherType> getDispatcherTypes(SecurityProperties securityProperties) {
		if (securityProperties.getFilter().getDispatcherTypes() == null) {
			return null;
		}
		return securityProperties.getFilter()
			.getDispatcherTypes()
			.stream()
			.map((type) -> DispatcherType.valueOf(type.name()))
			.collect(Collectors.toCollection(() -> EnumSet.noneOf(DispatcherType.class)));
	}

}
```

위 코드를 간단히 살펴보면 `@ConditionalOnBean(name = DEFAULT_FILTER_NAME)` 어노테이션이 존재하고, `DEFAULT_FILTER_NAME`은 `AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME`이며, 그 값은 `springSecurityFilterChain`로 정의되어 있다.

따라서 해당 어노테이션은 `springSecurityFilterChain`이라는 이름을 가진 빈이 스프링 컨테이너에 존재할 때 실행되어 `DelegatingFilterProxyRegistrationBean`을 생성하는 역할을 담당한다.

## FilterChainProxy
> 위에서 본 `DelegatingFilterProxy` 가 가로챈 요청을 전달받는 주체이다.
> `springSecurityFilterChain` 이라는 이름으로 등록되어 있다.
> 내부적으로 등록되어 있는 `SecurityFilterChain` 중 알맞은 체인과 연결해준다.

```java
public class FilterChainProxy extends GenericFilterBean {

	private static final Log logger = LogFactory.getLog(FilterChainProxy.class);

	private static final String FILTER_APPLIED = FilterChainProxy.class.getName().concat(".APPLIED");

	private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
		.getContextHolderStrategy();

	private List<SecurityFilterChain> filterChains;

	private FilterChainValidator filterChainValidator = new NullFilterChainValidator();

	private HttpFirewall firewall = new StrictHttpFirewall();

	private RequestRejectedHandler requestRejectedHandler = new HttpStatusRequestRejectedHandler();

	private ThrowableAnalyzer throwableAnalyzer = new ThrowableAnalyzer();

	private FilterChainDecorator filterChainDecorator = new VirtualFilterChainDecorator();
}
```
위와 같이 일부 코드를 가져와 뜯어보면 내부적으로 `SecurityFilterChain` 을 리스트 형태로 담고 있는 것을 확인 가능하다.



### 정리
간단하게 정리하면 Http 요청이 들어왔을 때 `DelegatingFilterProxy` 가 `FilterChainProxy` 에 전달해주고 `FilterChainProxy` 는 알맞는 `SecurityFilterChain` 에서 로직을 처리할 수 있도록 동작한다는 것을 알 수 있다.