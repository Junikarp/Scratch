# 도메인 엔티티 vs 영속성 객체 vs DB 엔티티
> 엔티티는 Jpa 랑 상관이 없다. 실제 도메인 모델의 엔티티와 DB 관계형 모델의 엔티티는 같은 것이 아니다.

### 도메인 엔티티
> * 소프트웨어에서 어떤 도메인이나 문제를 해결하기 위해 만들어진 모델.
> * 비즈니스 로직을 들고 있고, 식별 가능하며, 일반적으로 생명주기를 가짐

```Java
public class User {
    
    private String name;
    private int age;
    
}
```

### DB 엔티티
> * 데이터베이스에 표현하려고 하는 유형
> * 무형의 객체(object)로써 서로 구별되는 것

| name | user |
|------|------|
| Juni | 12   |
| Karp | 22   |


### 영속성 객체
> * 도메인 엔티티와 DB 엔티티를 연결해주기 위한 객체

```Java
// User 도메인 엔티티와 DB 엔티티를 JPA 로 매핑한 모습

@Entity
public class User {
    
    @Column
    private String name;
    @Column
    private int age;
    
}
```