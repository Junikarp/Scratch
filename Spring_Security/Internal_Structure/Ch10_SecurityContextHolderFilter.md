# SecurityContextHolderFilter

## 사용 목적
> `SecurityContextHolderFilter`는 `DefaultSecurityFilterChain`에 기본적으로 등록되는 필터로 세 번째에 위치한다.
> 
> 이전 요청을 통해 이미 인증한 사용자 정보를 현재 요청의 `SecurityContextHolder`의 `SecurityContext`에 할당하는 역할을 수행하고, 현재 요청이 끝나면 `SecurityContext`를 초기화한다.

## SecurityContextHolderFilter 클래스
```java
public class SecurityContextHolderFilter extends GenericFilterBean {
    
}
```

### 기본 로직
이전 요청에서 사용자가 로그인했고, STATELESS 한 상태가 아니라면 서버의 세션 혹은 레디스 등 저장매체에 유저의 정보가 있을 것이다.

이 때 저장 매체에서 유저 정보를 가져올 때 `SecurityContextRepository`라는 인터페이스의 `loadDefferedContext()`메서를 황용하여 정보를 가져온다.(존재 X -> 빈 객체 응답)

이 후 불러온 유저 정보를 `SecurityContextHolder`에 setDefferedContext() 메서드로 저장하고 다음 필터로 넘긴다.

응답이 끝나면 유저 정보를 제거한다.

### 위임
저장 매체로부터 유저 정보를 가져오는 부분 -> `SecurityContextRepository` 인터페이스 구현 클래스
불러온 유저 정보 -> `SecurityContextHolder`에 설정

### SecurityContextRepository 인터페이스와 구현체들
세션이나 레디스 등 저장 매체들로 부터 유저 정보를 불러오는 `SecurityContextRepository`가 인터페이스로 정의된 이유 -> 저장 매체별로 구현 방식이 다르기 때문

* HttpSessionSecurityContextRepository : 서버 세션 기반 구현체
* NullSecurityContextRepository : 아무 작업도 하지 않음 (JWT를 활용한 STATELESS 관리 시 사용)
* RequestAttributeSecurityContextRepository : HTTP request 저장 기반 구현체
* 기타 저장 매체 : 알맞게 자신이 커스텀 구현을 통해 사용하면 된다.

## SecurityContextPersistenceFilter vs SecurityContextHolderFilter
> 전자가 과거 사용된 필터이고 후자가 현재 후속으로 나온 클래스이다.
> 
> 변경된 부분은 기능면에선 거의 동일하지만 `doFilter`부분에서 변경점을 저장하느냐 안하느냐에 차이가 있다.

* SecurityContextPersistenceFilter
```java
finally {
SecurityContext contextAfterChainExecution = this.securityContextHolderStrategy.getContext();
// Crucial removal of SecurityContextHolder contents before anything else.
			this.securityContextHolderStrategy.clearContext();
			this.repo.saveContext(contextAfterChainExecution, holder.getRequest(), holder.getResponse());
        request.removeAttribute(FILTER_APPLIED);
			this.logger.debug("Cleared SecurityContextHolder to complete request");
		}
```

* SecurityContextHolderFilter
```java
finally {
			this.securityContextHolderStrategy.clearContext();
			request.removeAttribute(FILTER_APPLIED);
		}
```

`SecurityContextPersistenceFilter`는 변경된 부분을 저장하는 `saveContext` 메서드가 존재하지만 `SecurityContextHolderFilter`에는 존재하지 않는 것을 확인가능하다.

