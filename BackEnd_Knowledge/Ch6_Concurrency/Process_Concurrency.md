# 프로세스 동시성 제어
> 프로세스 수준에서의 동시 접근 제어를 알아봅시다.

## Lock
> 프로세스 수준에서 동시성 제어를 위해 사용하는 가장 기본적인 방식이다. (공유 자원 잠그기)

잠금 방식은 임계 영역에 접근하는 다른 스레드의 접근을 막는 방식으로 아래의 단계로 이루어진다.
1. 잠금 획득
2. 공유 자원(임계 영역)에 접근
3. 잠금 해제

잠금은 한 번에 한 스레드만 획득 가능하며 잠금이 해제되면 다른 스레드가 접근 가능하다.

예시와 함께 보자

```java
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

class LockTest {
    private Lock lock = new ReentrantLock();
    private Map<String, Session> sessions = new HashMap<>();
    
    public void addSession(Session session) {
        lock.lock(); // 잠금
        try{
            sessions.put(session.getId(), session); // 공유 자원 접근
        } finally {
            lock.unlock(); // 잠금 해제
        }
    }
}
```

위의 코드에서 lock 과 unlock 을 통해 임계영역을 잠근 모습을 볼 수 있다. 이 때 lock 과 unlock 사이에 있는 코드 블럭인 sessions 가 공유자원으로서 보호된다.

# 세마포어
> * 뮤텍스의 경우 스레드의 접근을 한 개 까지로 제한한다. 하지만 세마포어는 동시에 실행 가능한 스레드 수를 여러개로 제한 가능하다.
> * 세마포어는 허용 가능한 숫자를 퍼밋(permit)이라고 표현한다.
> * 이진 세마포어는 1개의 스레드, 계수 세마포어는 지정한 수만큼 동시 접근을 허용한다.

예시 코드를 한번 보자

```java
import java.util.concurrent.Semaphore;

public class SemaphoreTest {
    private Semaphore semaphore = new Semaphore(5); // 접근 허용 스레드 수를 지정한다.
    
    public String getData() {
        try {
            semaphore.acquire(); // 퍼밋 획득 시도
        } catch(InterruptedException e) {
            throw new RuntimeException(e); // 획득 못하면 예외 처리
        } 
        try {
            String Data = "data";
            return data;
        }
        finally {
            semaphore.release(); // 퍼밋을 반환
        }
    }
}
```
위와 같이 허용 스레드 수가 5 이므로 최대 5개의 스레드만 실행 가능하다.

# 읽기 쓰기 잠금
> 읽기 작업은 데이터의 변경이 없는 한 여러 스레드에서 동시 실행해도 문제가 없는 경우가 있다. 따라서 읽기쓰기 잠금을 활용하면 읽기 성능의 저하를 예방가능하다.
> 특히 쓰기 대비 읽기 빈도가 높은 경우 효과적이다.

읽기 쓰기 잠금은 아래의 특징을 갖는다.

* 쓰기 잠금은 한 번에 한 스레드만 구할 수 있다.
* 읽기 잠금은 한 번에 여러 스레드가 구할 수 있다.
* 한 스레드가 쓰기 잠금을 획득했다면 쓰기 잠금 해제까지 읽기 잠금을 구할 수 없다.
* 읽기 잠금을 획득한 모든 스레드가 읽기 잠금을 해제할 때 까지 쓰기 잠금을 구할 수 없다.

아래의 코드를 살펴보자.

```java
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ReadWriteLockTest {
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();
    private Lock readLock = lock.readLock();
    private Map<String, Session> sessions = new HashMap<>();

    public void addSession(Session session) {
        writeLock.lock(); // 쓰기 잠금
        try {
            sessions.put(session.getId(), session); // 공유 자원 접근
        } finally {
            writeLock.unlock(); // 잠금 해제
        }
    }
    
    public Session getSession(String sessionId) {
        readLock.lock(); // 읽기 잠금
        try {
            return sessions.get(sessionId);
        } finally {
            readLock.unlock(); // 잠금 해제
        }
    }
}
```

