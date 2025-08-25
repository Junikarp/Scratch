package Modern_Java.Stream.Practice_Problem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// 문제7 - 전체 트랜잭션 중 최댓값은 얼마인가?
public class PP7 {
    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario","Milan");
        Trader alan = new Trader("Alan","Cambridge");
        Trader brian = new Trader("Brian","Cambridge");
        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );

        Optional<Integer> Max_value = transactions.stream()
                .map(Transaction::getValue)
        //      .reduce(0, (n,m) -> Math.max(n,m));
                .reduce(Integer::max);

        System.out.println(Max_value);
    }
}
