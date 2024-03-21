package lesson2;

import org.junit.jupiter.api.*;

import java.util.function.Consumer;

class CalculatorTest {

  // IDE -> junit = java-app (класс)

  @BeforeAll
  static void beforeAll1() {
    System.out.println("before all1");
  }

  @BeforeAll
  static void beforeAll2() {
    System.out.println("before all2");
  }

  @AfterEach
  void afterEach() {
    System.out.println("after");
  }

  @BeforeEach
  void beforeEach() {
    System.out.println("before each");
  }

  @Test
  void testSum1() {
    Calculator calculator = new Calculator();
    int actual = calculator.sum(2, 5);
    Assertions.assertEquals(7, actual);
    System.out.println("test1");
  }

  @Test
  void testSum2() {
    Calculator calculator = new Calculator();
    int actual = calculator.sum(0, -3);
    Assertions.assertEquals(-3, actual);
    System.out.println("test2");
  }

  @Test
  void testVoidSum() {
    int[] arg = new int[1];
    Consumer<Integer> integerConsumer = new Consumer<>() {
      @Override
      public void accept(Integer integer) {
        arg[0] = integer;
      }
    };

    new Calculator(integerConsumer).voidSum(3, 7);
    Assertions.assertEquals(10, arg[0]);
  }


}