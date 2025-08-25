주요 Stream 연산 메서드는 아래와 같음.

| 연산 형식 | 반환 형식 | 사용된 함수형 인터페이스 형식 | 함수 디스크립터 |
| :--- | :--- | :--- | :--- |
| filter 중간 연산 | Stream<T> | Predicate<T> | T -> boolean |
| distinct 중간 연산 (상태 있는 언바운드) | Stream<T> | (없음) | (없음) |
| takeWhile 중간 연산 | Stream<T> | Predicate<T> | T -> boolean |
| dropWhile 중간 연산 | Stream<T> | Predicate<T> | T -> boolean |
| skip 중간 연산 (상태 있는 바운드) | Stream<T> | (없음) | long |
| limit 중간 연산 (상태 있는 바운드) | Stream<T> | (없음) | long |
| map 중간 연산 | Stream<R> | Function<T, R> | T -> R |
| flatMap 중간 연산 | Stream<R> | Function<T, Stream<R>> | T -> Stream<R> |
| sorted 중간 연산 (상태 있는 언바운드) | Stream<T> | Comparator<T> | (T, T) -> int |
| anyMatch 최종 연산 | boolean | Predicate<T> | T -> boolean |
| noneMatch 최종 연산 | boolean | Predicate<T> | T -> boolean |
| allMatch 최종 연산 | boolean | Predicate<T> | T -> boolean |
| findAny 최종 연산 | Optional<T> | (없음) | (없음) |
| findFirst 최종 연산 | Optional<T> | (없음) | (없음) |
| forEach 최종 연산 | void | Consumer<T> | T -> void |
| collect 최종 연산 | R | Collector<T, A, R> | (없음) |
| reduce 최종 연산 (상태 있는 바운드) | Optional<T> | BinaryOperator<T> | (T, T) -> T |
| count 최종 연산 | long | (없음) | (없음) |