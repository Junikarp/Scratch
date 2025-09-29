# 랜덤 메서드 테스트 해보기 (진행 중)

> 랜덤으로 값이 생성되는 부분에 대한 테스트 코드를 작성하는 과정을 기록한 문서입니다.

### 초기 코드 작성

> 프로젝트 진행 중 문제은행에서 문제를 랜덤으로 섞어서 사용자에게 제공하는 기능을 개발하는 데 `Collections.shuffle()` 메서드를 활용하였습니다.
```java
@Override
public List<Question> createRandomQuestionList(int Quantity) {

    List<Question> wholeQuestionList = questionRepository.findAll();

    Collections.shuffle(wholeQuestionList);

    return wholeQuestionList.stream()
            .limit(Quantity)
            .toList();

}
```
주어진 `Quantity` 만큼 랜덤으로 문제를 제공하기 위해서 위와 같이 코드를 작성하였습니다.
하지만 테스트를 진행함에 있어서 랜덤한 값을 테스트하는데 어떻게 코드를 구성할 지 고민해야했습니다.

```java
@Test
void 사용자는_지정된_수량만큼_랜덤한_문제_리스트를_조회_할_수_있다() {
    //given
    TestContainer testContainer = TestContainer.builder()
            .build();

    testContainer.questionRepository.save(initQuestionList.get(0));
    testContainer.questionRepository.save(initQuestionList.get(1));
    testContainer.questionRepository.save(initQuestionList.get(2));

    int randomCount = 0;

    //when
    ResponseEntity<RandomQuestionListResponse> result = testContainer.questionController.getRandomQuestionList(2);

    for (int i = 0; i < 10; i++) {
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getList().size()).isEqualTo(2);

        if (result.getBody().getList().get(0).getId() != 1) {
            randomCount++;
        }
    }

    //then
    assertThat(randomCount).isGreaterThan(1);
}
```
초기 코드입니다. 해당 코드에서는 총 10회의 테스트를 반복해서 실행하며 기존 문제 리스트와 랜덤 문제 리스트의 첫번째 문제를 비교합니다.
해당 코드에서는 첫번째 문제의 아이디를 비교하며 테스트 진행 중 아이디가 다를 경우 randomCount 를 1씩 증가시켜 최종적으로 10회 반복후 randomCount 의 숫자가 2 이상일 경우
테스트를 통과한 것으로 간주합니다.

### 불필요한 로직의 제거

```java
@RepeatedTest(value = 5)
void 사용자는_지정된_수량만큼_랜덤한_문제_리스트를_조회_할_수_있다() {
    //given
    TestContainer testContainer = TestContainer.builder()
            .build();

    testContainer.questionRepository.save(initQuestionList.get(0));
    testContainer.questionRepository.save(initQuestionList.get(1));
    testContainer.questionRepository.save(initQuestionList.get(2));

    //when
    ResponseEntity<RandomQuestionListResponse> result = testContainer.questionController.getRandomQuestionList(2);

    //then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getList().size()).isEqualTo(2);
    assertThat(result.getBody().getList().get(0).getId()).isNotEqualTo(1);
}
```
for 문과 같은 논리적인 로직이 테스트에 영향을 줄 수 있기 때문에 for 문을 삭제하였습니다. 대신 `@RepeatedTest`를 활용하여 테스트를 여러번 진행하도록 개선하였습니다.
하지만 위 코드에서는 `assertThat(result.getBody().getList().get(0).getId()).isNotEqualTo(1);` 해당 코드 부분이 실패할 가능성이 존재합니다. 때문에 다른 테스트 방식을
고민해야 했습니다.