# SecurityContextHolder

## 필요성
> SecurityFilterChain 내부에 존재하는 각 필터는 시큐리티 관련 작업을 기능 단위로 분업하여 진행한다.
> 이 때 앞에서 한 작업을 뒤의 필터가 알기 위한 저장소 역할을 하는 개념이 필요하다. (ex-유저의 ROLE 정보 등)

### Authentication 객체
![img.png](image/ch5/ch5-1.png)

아이디, 로그인 여부, ROLE 등 데이터들이 `Authentication` 객체에 담긴다.

`Authentication` 객체는 `SecurityContext`에 포함되어 관리되며, 0~N개의 `SecurityContext`가 `SecurityContextHodler`에 의해 관리된다.

* Authentication 객체 구조
    * Principal : 유저에 대한 정보
    * Credentials : 증명(비밀번호, 토큰)
    * Authorities : 유저의 권한(Role) 목록 

* 접근 방법
```java
SecurityContextHolder.getContext().getAuthentication().getAuthorities();
```
`SecurityContextHolder`의 메서드는 static 으로 선언되므로 어디서든 접근 가능하다.

* 특이 사항
멀티 쓰레드 환경에서 `SecurityContextHolder`를 통해 `SecurityContext`를 부여한느 관리 전략은 위임하여 다른 클래스에 맡김.

사용자별로 다른 저장소를 제공해야 인증정보가 겹치는 일이 발생하지 않기 때문이다.

`SecurityContextHolder`는 `SecurityContext`들을 관리하는 메서드를 제공하지만 실제로 등록, 초기화, 읽기 등의 작업은 `SecurityContextHolderStrategy`인터페이스를 활용한다.

기본적으로 `threadlocal` 방식을 활용

#### 접근 스레드별 SecurityContext 배분
톰캣 WAS 는 멀티스레드 방식으로 동작. -> 유저가 접속하면 유저당 하나의 스레드를 할당 (각 유저가 동시에 시큐리티 로그인 로직을 사용 가능)

이 때 `SecurityContextHolder`의 필드에 선언된 `SecurityContext`를 호출하게 되면 스레드간 공유하는 메모리의 code 영역에 데이터가 있으므로, 정보가 덮어지는 현상이 발생한다고 생각할 수 있음

하지만 `threadLocal`로 관리되므로 격리된 공간을 할당 받아 스레드 별로 다른 구획을 사용하게 된다.

#### ThreadLocal 간단 정리
> ThreadLocal 은 멀티 스레드 환경에서 각 스레드에게 별도의 저장공간을 할당하여 별도의 상태를 가질 수 있게 만드는 Java 의 Thread Safe 한 기술

* ThreadLocal 필요성
Spring 은 bean 이라고 불리는 스프링이 생명주기를 관리하는 객체를 활용한다. 이 때 해당 객체는 1개만 만들어져 모든 Thread 가 공유하여 사용하게 되는데 여기서 Thread 동기화 문제가 발생한다.
(예를 들어 하나의 인스턴스 필드값을 같은 스레드의 store에서 관리하면 덮어씌워질 가능성이 존재한다.)

자세한 내용은 다음 링크를 읽어보면 좋을 듯 하다.
[ThreadLocal 알아보기](https://catsbi.oopy.io/3ddf4078-55f0-4fde-9d51-907613a44c0d)

#### SecurityContext 생명 주기
`Authentication` 객체를 관리하는 `SecurityContext` 는 사용자의 요청이 서버로 들어오면 생성되고 처리가 끝난 후 응답되는 순간에 초기화

#### 사용 예시
* 로그인 필터 : 인증 완료 후 유저 정보를 담은 `Authentication`객체를 넣음
* 로그아웃 필터 : 로그아웃 로직을 수행하면서 `SecurityContext`의 `Authentication`객체를 비움

----
### 요약
1. `SecurityFilterChain`의 각 필터에서 수행한 작업 내용이 전달되기 위해서 요청(유저)별로 `Authentication`객체를 할당하여 확인함.
2. `Authentication` 객체는 `SecurityContextHolder`의 `SecurityContext`가 관리함
3. 멀티 스레드 환경에서 `SecurityContext`를 만들고 필드의 static 영역에 선언된 `SecurityContext`를 다루는 전략은 기본적으로 `threadLocal`전략을 이용함