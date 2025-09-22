# 기타 사항

### 1. private 메서드는 테스트하지 않아도 된다.

만약 테스트 하고 싶다면 <br>다른 클래스로 분리/책임을 위임해 public 으로 만들라는 신호
<br>또는 public 으로 만들어야 한다는 신호 &rarr; 설계 개선이 필요

### 2. final 메서드를 stub 하는 상황을 피하라
final 메서드를 stub 해야하는 상황은 잘못된 설계로 인한 것 <br>
final 은 overwrite 하지 않겠다는 표시인데 대체해야 하는 상황 자체가 잘못된 것

#### 해결책
* 의존성 역정으로 완충할 것
* 테스트 시에는 Mock 을 사용

### 3. DRY < DAMP
DRY (건조한) - Don't Repeat Yourself(반복하지 않기)
<br> DAMP (습한) - Descriptive And Meaningful Phrase(서술적이고 의미 있는 문구)

### 4. 논리 로직을 피할 것
테스트에는 +, for, if 등의 논리를 넣지 말자 (간단한 논리로직으로 인해 예측 불가한 버그 발생 가능)