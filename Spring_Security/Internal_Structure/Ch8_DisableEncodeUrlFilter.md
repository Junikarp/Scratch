# DisableEncodeUrlFilter

## 사용 목적
> `DisableEncodeUrlFilter`는 `DefaultSecurityFilterChain`에 기본적으로 등록되는 필터로 가장 첫 번째에 위치한다.
> 
> 사용하는 목적은 URL 파라미터에 세션ID가 인코딩되어 로그로 유출되는 것을 방지하는 것

커스텀 `SecurityFilterChain` 생성 시에도 등록되므로 아래와 같이 비활성 가능하다.

```java
http
        .sessionManagement((manage) -> manage.disable());
```

## 내부 구조 간단히 살펴보기

* DisableEncodeUrlFilter
```java
public class DisableEncodeUrlFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		filterChain.doFilter(request, new DisableEncodeUrlResponseWrapper(response));
	}
    
	private static final class DisableEncodeUrlResponseWrapper extends HttpServletResponseWrapper {
        
		private DisableEncodeUrlResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public String encodeRedirectURL(String url) {
			return url;
		}

		@Override
		public String encodeURL(String url) {
			return url;
		}
	}
}
```
간단하게 `DisableEncodeUrlResponse` 코드를 살펴보면 `encodeRedirectURL`과 `encodeURL`라는 메서드가 오버라이드 된 것이 눈에 띈다.

따라서 기존 코드를 한 번 따라가 보면 다음과 같다.

```java
    @Override
    public String encodeRedirectURL(String url) {
        if (isEncodeable(toAbsolute(url))) {
            return toEncoded(url, request.getSessionInternal().getIdInternal()); // 세션값을 꺼내어 인코딩하는 로직
        } else {
            return url;
        }
    }


    @Override
    public String encodeURL(String url) {

        String absolute;
        try {
            absolute = toAbsolute(url);
        } catch (IllegalArgumentException iae) {
            // Relative URL
            return url;
        }

        if (isEncodeable(absolute)) {
            // W3c spec clearly said
            if (url.equalsIgnoreCase("")) {
                url = absolute;
            } else if (url.equals(absolute) && !hasPath(url)) {
                url += '/';
            }
            return toEncoded(url, request.getSessionInternal().getIdInternal()); // 세션값을 꺼내어 인코딩하는 로직
        } else {
            return url;
        }

    }
```
`HttpServletResponse` 인터페이스를 구현한 `Response` 클래스를 살펴보면 `toEncoded(url, request.getSessionInternal().getIdInternal());`와 같은 부분을 찾을 수 있다.
이 부분이 session 값을 꺼내어 인코딩 하는 부분이며 `DisableEncodeUrlFilter`에서는 이 메서드를 오버라이드하여 session을 인코딩하지 않도록 수정하는 것이다.