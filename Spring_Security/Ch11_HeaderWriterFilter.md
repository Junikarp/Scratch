# HeaderWriterFilter

## 사용 목적
> `HeaderWriterFilter`는 `DefaultSecurityFilterChain`에 기본적으로 등록되는 필터로 네번째에 위치한다.
> 
> 해당 필터는 HTTP 응답헤더에 사용자 보호를 위한 시큐리티 관련 헤더를 추가하는 필터이다.

비활성화 하고 싶다면 아래처럼 커스텀 하면된다.

```java
http
        .headers((headers) -> headers.disable());
```

## HeaderWriterFilter 클래스
```java
public class HeaderWriterFilter extends OncePerRequestFilter {
    
}
```

응답 헤더에 시큐리티 관련 헤더를 추가하는 시점은 2종류로 아래와 같다.

1. 현재 필터를 통과하는 순간
2. 서블릿에서 응답을 보내며 다시 이 필터를 통과하는 순간

기본값은 2번으로 설정되어 있다.

## 헤더 목록
| Key                    | 설명                                                                                  |
|------------------------|-------------------------------------------------------------------------------------|
| X-Content-Type-Options | 컨텐츠 스니핑을 막기위해 `nosniff value` 를 할당해 서버에서 응답하는 `Content-Type` 과 다른 타입일 경우 읽지 못하도록 설정 |
| X-XSS-Protection       | XSS 공격 감지 시 로딩 금자(0은 비활성화)                                                          |
| Cache-Control          | 이전에 받았던 데이터와 현재 보낼 데이터가 같다면 로딩에 대한 결정 여부                                            |
| Pragma                 | HTTP/1.0 방식에서 사용하던 Cache-Control                                                    |
| Expires                | 서버에서 보낼 데이터를 브라우저에서 캐싱할 시간                                                          |
| X-Frame-Options        | 브라우저가 응답 데이터를 iframe, frame, embed, object 태그에서 로딩해도 되는지 여부                         |


이 외 헤더 설정 변경 등은 공식 문서를 보고 알맞게 커스텀 해서 사용하면 된다.
