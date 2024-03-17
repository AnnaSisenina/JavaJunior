package lesson1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.stream.Stream;

public class Lambdas {

  // Integer apply(String t);
  static int length(String arg) {
    return arg.length();
  }

  public static void main(String[] args) {
//    lambdasIntro();
//    comparatorDemo();
//    methodReferences();

    List<Integer> integers = new ArrayList<>();
    for (int i = 0; i < 1_000_00; i++) {
      integers.add(ThreadLocalRandom.current().nextInt(100));
    }

    // Все числа, меньше 50, нужно умножить на 2 и распечатать на консоль
    integers.stream()
//      .parallel()
      .filter(x -> x < 50)
      .map(x -> x * 2)
//      .sequential()
      .forEach(System.out::println);


    // Создать стрим с рандомными числами, взять первые 5 элементов и сохранить их в List
    List<Integer> list = Stream.generate(() -> ThreadLocalRandom.current().nextInt(100))
      .limit(5)
      .toList();

    Stream.iterate(0, x -> x + 1).limit(10).forEach(System.out::println);


//      .map(x -> x * 2)
//      .forEach(System.out::println);


//    for (Integer integer : integers) {
//      if (integer < 50) {
//        System.out.println(integer * 2);
//      }
//    }


  }

  private static void methodReferences() {
    Function<String, Integer> stringLengthExtractor = String::length;
    System.out.println(stringLengthExtractor.apply("hello"));
    System.out.println(stringLengthExtractor.apply("world"));
    System.out.println(stringLengthExtractor.apply("c++"));
    System.out.println(stringLengthExtractor.apply("java"));

    // предикат, который сравниваем все входящие строки со строкой "java"
    Predicate<String> javaTester = "java"::equals; // str -> "java".equals(str);
    System.out.println(javaTester.test("java"));
    System.out.println(javaTester.test("c++"));
    System.out.println(javaTester.test("python"));

    Supplier<ArrayList<Integer>> arrayListSupplier = ArrayList::new; // new ArrayList<>()
    IntFunction<ArrayList<Integer>> arrayListSupplierWithCapacity = ArrayList::new; // new ArrayList<>(int capacity)

    ArrayList<Integer> arrayList = arrayListSupplier.get(); // new ArrayList<>()
    ArrayList<Integer> arrayListWithCapacity = arrayListSupplierWithCapacity.apply(100); // new ArrayList<>(100)
  }

  private static void comparatorDemo() {
    List<Integer> integers = new ArrayList<>();
    Random random = new Random();
    integers.add(5);
    integers.add(2);
    integers.add(10);
    integers.add(15);
    integers.add(23);
    integers.add(63);
    System.out.println(integers);

    // 2 10 5 15 23 63

//    Collections.sort(integers);
//    Collections.reverse(integers);
//    System.out.println(integers);

    // (T, T) -> int

    // method reference (ссылка на метод)
    Collections.sort(integers, Lambdas::compareIntegers);
    System.out.println(integers);
  }

  private static int compareIntegers(int x, int y) {
    if (x % 2 == 0 && y % 2 != 0) {
      return -1;
    } else if (x % 2 != 0 && y % 2 == 0) {
      return 1;
    }
    return Integer.compare(x, y);
  }

  private static void lambdasIntro() {
    Foo foo = () -> System.out.println("Hello, world!");
    foo.foo();

    // Лямбда, которая принимает целое число и возвращает его квадрат
    UnaryOperator<Integer> square = x -> x * x;

    // Лямбда, которая принимает строку, возвращает ее длину
    Function<String, Integer> lengthExtractor = str -> str.length();
    System.out.println(lengthExtractor.apply("Hello"));

    // Лямбда, которая принимает строку и печатает ее в консоль
    Consumer<String> printer = str -> System.out.println(str);
    printer.accept("Hello, world!");

    // Лямбда, которая генерирует рандомные целые числа в диапазоне от 0 до 100
    Supplier<Integer> randomer = () -> new Random().nextInt(101);
    System.out.println(randomer.get());

    // Лямбда, которая выводит на консоль рандомное целое число в диапазоне от 0 до 100
    Runnable runnable = () -> printer.accept(String.valueOf(randomer.get()));
    runnable.run();

    // Лямбда, которая проверяет целое число на четность
    Predicate<Integer> evenTester = n -> n % 2 == 0;
    System.out.println(evenTester.test(5));
    System.out.println(evenTester.test(0));
    System.out.println(evenTester.test(10));
  }

  interface SquareInterface {
    int square(int x);
  }

  interface Foo {
    void foo();
  }

}
