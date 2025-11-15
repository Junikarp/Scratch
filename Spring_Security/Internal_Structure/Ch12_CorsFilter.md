# CorsFilter

## 사용 목적
> `DefaultSecurityFilterChain` 에 기본적으로 등록되는 필터로 다섯 번째에 위치
> 
> 필터가 등록되는 목적은 `CorsConfigurationSource`에 설정한 값에 따라 필터단에서 응답헤더를 설정하기 위함

## 로직

* doFilter
    * `doFilter()`메서드에서 `HttpServletRequest`로 캐스팅 진행 후 CORS 요청 타입을 확인
    * switch문에 의해 request 값에 따라 알맞은 cors 처리 진행
