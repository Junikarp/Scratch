# GenericFilterBean & OncePerRequestFilter

## GenericFilterBean VS OncePerRequestFilter
> 두 필터 방식의 차이점은 한 번의 요청에 대해서 어떻게 처리되느냐에 차이가 존재한다.

* `GenericFilterBean`은 내부적으로 동일한 필터를 여러 번 통과하더라도 통과한 수 만큼 내부 로직이 실행된다.
* `OncePerRequestFilter`는 이름 그대로 요청 당 한번만 처리하는 필터이다. 즉, 동일 필터를 여러 번 통과하더라도 한 번만 내부로직을 실행한다.

### OncePerRequestFilter 주의 사항
> `OncePerRequestFilter`는 Forward 방식에서는 한 번만 처리되는 것이 맞다. 하지만
> Redirect 방식에서는 두 번 처리되는 것이 맞다. 별개의 요청이 두 번 들어오는 방식이기 때문에 각각의 요청을 따로 처리하는 것