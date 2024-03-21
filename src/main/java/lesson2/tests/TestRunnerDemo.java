package lesson2.tests;

public class TestRunnerDemo {

  // private никому не видно
  // default (package-private) внутри пакета
  // protected внутри пакета + наследники
  // public всем

  public static void main(String[] args) {
    TestRunner.run(TestRunnerDemo.class);
  }

  @Test
  private void test1() {
    System.out.println("test1");
  }

  @Test (order = 2)
  void test2() {
    System.out.println("test2");
  }

  @Test (order = 3)
  void test3() {
    System.out.println("test3");
  }

  @Test
  void test3_1() {
    System.out.println("test3_1");
  }



  @BeforeEach
  void test4() {
    System.out.println("BeforeEach");
  }

  @AfterEach
  void test5() {
    System.out.println("AfterEach");
  }

  @AfterAll
  void test6() {
    System.out.println("AfterAll");
  }

  @BeforeAll
  void test7() {
    System.out.println("BeforeAll");
  }

  @BeforeAll
  void test8() {
    System.out.println("BeforeAll_2");
  }



}
