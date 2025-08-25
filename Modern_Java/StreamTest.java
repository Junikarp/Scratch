package Modern_Java;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class StreamTest {
    public static void main(String[] args) {
        List<Dish> menu = Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH)
        );

        List<String> threeHighCaloricDishNames =
                menu.stream()
                        .filter(dish -> dish.getCalories() > 300)
                        .map(Dish::getName)
                        .limit(3)
                        .collect(toList());

        System.out.println(threeHighCaloricDishNames);
    }
}



/**
 스트림은 파이프라이닝과 내부반복이라는 두가지 특징을 가짐
 파이프 라이닝 -대부분의 스트림 연산이 스트림 연산끼리 연결해서 커다란 파이프라인을
 구성 가능하도록 자신을 반환함

 배부반복 - 반복자로 명시적으로 반복하는 컬렉션과 다르게 스트림은 내부 반복을 지원함
 **/