package lesson1;

import java.util.List;
import java.util.stream.Collectors;

public class Optionals {

  public static void main(String[] args) {
    String s = "asfFKDSHFDSfhfkasfjfjfhJKFsdjLFJSDkfl";

//    Stream<int> == IntStream

    String collect = s.chars() // IntStream
      .filter(it -> {
        char symbol = (char) it;

        String symbolStr = String.valueOf(symbol);
        String symbolStrUpperCase = symbolStr.toUpperCase();

        return symbolStr.equals(symbolStrUpperCase);
      })
//      .filter(Character::isUpperCase) // // IntStream
      .mapToObj(it -> String.valueOf((char) it)) // (int -> char) -boxing> Character -> String#valueOf
      .collect(Collectors.joining("-"));

    System.out.println(collect);


    List<String> langs = List.of("Java", "C++", "Python", "Kotlin"
      , "Go"
    );

    langs.stream()
      .filter(it -> it.length() < 3)
      .findFirst() // Optional<String> "Go"
      .map(String::toUpperCase) // Optional<String> "GO"
      .ifPresentOrElse(Optionals::saveToDB, () -> System.out.println("нет значения"));

//    String lang = first.orElse("Unknown");
//    System.out.println(lang);
//
//    first.ifPresent(System.out::println);
//    first.ifPresentOrElse(System.out::println, () -> System.out.println("No value present"));

//    String s = first.orElseThrow(() -> new IllegalStateException("нет значения, а оно должно быть"));


//    Optional<String> uppered = first.map(it -> it.toUpperCase());


//    String s = first.get();
//    System.out.println(s);

  }

  private static void saveToDB(String langName) {
    System.out.println(langName);
  }

}
