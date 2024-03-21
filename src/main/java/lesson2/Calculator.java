package lesson2;

import java.util.function.Consumer;

public class Calculator {

  private Consumer<Integer> sumAcceptor;

  public Calculator() {
    this(x -> {});
  }

  public Calculator(Consumer<Integer> sumAcceptor) {
    this.sumAcceptor = sumAcceptor;
  }

  public int sum(int a, int b) {
    return a + b;
  }

  public void voidSum(int a, int b) {
    sumAcceptor.accept(a + b);
  }

}
