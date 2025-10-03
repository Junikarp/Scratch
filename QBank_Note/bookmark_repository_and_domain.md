# bookmark 도메인과 레포지토리
> 그냥 개발 중에 이것저것 생각해 본 내용을 정리하기 위해서 작성한 글입니다.

### Bookmark Domain 설계
초기에 도메인을 작성할 때 북마크는 문제 도메인과 유저 도메인 사이에 있는 도메인이었습니다.

해당 도메인의 역할은 유저가 북마크를 한 문제의 리스트를 보관하는 단순한 도메인이었기에 단순하게 유저 아이디와 문제 아이디만을 갖도록 설계했습니다.

```java
@Getter
@Builder
@AllArgsConstructor
public class Bookmark {
    private Long id;
    private Long userId;
    private Long questionId;

    public static Bookmark from(Long userId, Long questionId) {
        return Bookmark.builder()
                .userId(userId)
                .questionId(questionId)
                .build();
    }
}
```

### 영속성 객체 설계

DB에 직접적으로 매칭되는 영속성 객체는 다음과 같습니다. 참조하는 도메인의 영속성 객체를 필드값으로 갖고있는 모습입니다.

```java
@Getter
@Setter
@Entity
@Table(name = "bookmark", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "question_id"})
})
public class BookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity questionEntity;

    public static BookmarkEntity from(Bookmark bookmark) {

        BookmarkEntity bookmarkEntity = new BookmarkEntity();
        bookmarkEntity.id = bookmark.getId();

        return bookmarkEntity;
    }

    public Bookmark to() {
        return Bookmark.builder()
                .id(id)
                .userId(userEntity.getId())
                .questionId(questionEntity.getId())
                .build();
    }

    public BookmarkEntity create(UserEntity userEntity, QuestionEntity questionEntity) {
        BookmarkEntity bookmarkEntity = new BookmarkEntity();
        bookmarkEntity.setUserEntity(userEntity);
        bookmarkEntity.setQuestionEntity(questionEntity);
        return bookmarkEntity;
    }
}
```

### repository 설계 중 하게 된 생각
repository 설계 중 문제가 있다고 생각한 시점은 save 기능을 개발하면서부터 들었습니다.

```java
@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepository {

    private final QuestionJpaRepository questionJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final BookmarkJpaRepository bookmarkJpaRepository;

    public Bookmark save(Bookmark bookmark) {
        UserEntity userEntity = userJpaRepository.findById(bookmark.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        QuestionEntity questionEntity = questionJpaRepository.findById(bookmark.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return bookmarkJpaRepository.save(BookmarkEntity.from(bookmark, userEntity, questionEntity)).to();
    }
}
```

위의 코드를 보면 save 라는 기능 하나를 위해서 `UserJpaRepository`, `BookmarkJpaRepository`를 참조해야만 했습니다. 우리가 가지고 있는 `Bookmark` 객체에
아이디를 제외한 데이터가 없기 때문이었습니다.

`BookmarkRepository`라는 클래스는 `Bookmark` 객체의 crud 에만 역할을 집중하고 다른 도메인의 repository를 의존하고 싶지 않다는 생각이 들었습니다. 또한 이렇게 설계하는 방향이
테스트 코드를 작성함에 있어서도 의존성을 줄여 훨씬 편리할 것이라고 판단했습니다.

### 개선 방안
우선 도메인 엔티티의 필드값에 존재하던 `userId`와 `questionId`를 각각 `User` 와 `Question`으로 변경하였습니다. 또한 `from` 메서드에 유저와 문제 엔티티까지 한번에 조립하도록 통합했습니다.
```java
@Getter
@Setter
@Entity
@Table(name = "bookmark", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "question_id"})
})
public class BookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity questionEntity;

    // 해당 메서드 수정
    public static BookmarkEntity from(Bookmark bookmark, UserEntity userEntity, QuestionEntity questionEntity) {
        BookmarkEntity bookmarkEntity = new BookmarkEntity();
        bookmarkEntity.id = bookmark.getId();
        bookmarkEntity.setUserEntity(userEntity);
        bookmarkEntity.setQuestionEntity(questionEntity);
        return bookmarkEntity;
    }

    public Bookmark to() {
        return Bookmark.builder()
                .id(id)
                .user(userEntity.to())
                .question(questionEntity.to())
                .build();
    }
}
```

다음으로 기존 `BookmarkRepositoryImpl` 에서 불러오던 `UserEntity` 와 `QuestionEntity` 값을 가져오는 로직을
`Service` 계층에서 처리하도록 변경하였습니다. 이로써 `repository` 계층에서는 다른 도메인과의 관계 없이 데이터를 저장하는 역할만 담당하도록 구조를
변경하였습니다.

아래는 변경한 코드입니다.

### Entity
```java
@Getter
@Setter
@Entity
@Table(name = "bookmark", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "question_id"})
})
public class BookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity questionEntity;

    public static BookmarkEntity from(Bookmark bookmark) {
        BookmarkEntity bookmarkEntity = new BookmarkEntity();
        bookmarkEntity.id = bookmark.getId();
        bookmarkEntity.setUserEntity(UserEntity.from(bookmark.getUser()));
        bookmarkEntity.setQuestionEntity(QuestionEntity.from(bookmark.getQuestion()));
        return bookmarkEntity;
    }

    public Bookmark to() {
        return Bookmark.builder()
                .id(id)
                .user(userEntity.to())
                .question(questionEntity.to())
                .build();
    }
}
```
### RepositoryImpl
```java
@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepository {

    private final BookmarkJpaRepository bookmarkJpaRepository;

    public Bookmark save(Bookmark bookmark) {
        return bookmarkJpaRepository.save(BookmarkEntity.from(bookmark)).to();
    }
}
```

### Service
```java
@RequiredArgsConstructor
@Service
@Builder
public class BookmarkServiceImpl implements BookmarkService {

    //TODO 나중에 repo 인터페이스 구현하고 jpa 의존 repo 의존으로 변경할 것
    private final QuestionJpaRepository questionJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    public List<Bookmark> findListByUserId(Long userId) {
        return bookmarkRepository.findByUserId(userId);
    }

    @Override
    public Bookmark create(Long userId, Long questionId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
                .to();
        Question question = questionJpaRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"))
                .to();
        Bookmark bookmark = Bookmark.from(user, question);
        return bookmarkRepository.save(bookmark);
    }
}
```