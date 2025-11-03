# Filter Chaining

## SecurityFilterChain 의 필터
> `SecurityFilterChain`에 속한 각각의 필터의 조상은 모두 동일함.
> 필터의 기반이 되는 필터 클래스를 만들어 두고 해당 클래스를 상속받아 각 특성에 맞게 구현되어 있음

## 필터 상속
필터는 중복되는 코드를 줄이고 각각의 구현부가 단일 책임을 갖고 작업한다. 따라서 상단의 필터 클래스는 구조적인 역할만, 상속받은 구현부는 구현부의 역할만 수행한다.

서블릿 기반의 Filter 인터페이스 바로 아래에 `GenericFilterBean`, `OncePerRequestFilter` 두 개의 추상 클래스가 존재하며 모든 필터는 두 추상 클래스를 기반으로 구현되어 있다.

### GenericFilterBean
```java
public abstract class GenericFilterBean implements Filter, BeanNameAware, EnvironmentAware,
        EnvironmentCapable, ServletContextAware, InitializingBean, DisposableBean {
    
}
```
`GenericFilterBean` 추상 클래스는 자바 서블릿 필터 기반으로 구현되어 있으며 자바 서블릿 영역에서 스프링 영역에 접근할 수 있도록 작성되어 있다.


### OncePerRequestFilter
```java
public abstract class OncePerRequestFilter extends GenericFilterBean {
    protected abstract void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException;
}
```

`OncePerRequestFilter`는 `GenericFilterBean`을 기반으로 작성된 추상 클래스로 클라이언트의 한 번 요청에 대해 내부적으로 동일한 서블릿 필터를 여러번 거칠 경우 한 번만 반응하도록 설계되어 있다.


## Filter 형식
```java
package jakarta.servlet;

import java.io.IOException;

public interface Filter {
    
    default void init(FilterConfig filterConfig) throws ServletException {
    }

    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException;

    void destroy() {
    }
}
```

* init()
  * 서블릿 컨테이너 실행 시 필터를 생성하고 초기화 할 때 사용하는 메서드
* doFilter()
  * 요청에 대한 작업 수행 및 다음 필터를 호출
* destroy()
  * 서블릿 컨테이너 종료 시 초기화 하는 메서드

### FilterChain 에서 다음 필터 호출
```java
public class LogoutFilter extends GenericFilterBean {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }
    
    private void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 처리할 로직
        //System.out.print("before");

        // 다음 필터 호출
        chain.doFilter(request, response);
        // 요청이 돌아서 다시 현재 필터 통과 시 처리할 로직
        //System.out.print("before");
    }
}
```