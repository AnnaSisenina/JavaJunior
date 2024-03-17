package lesson1;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Streams {

  public static void main(String[] args) {
//    Optional<Person> personOpt = Optional.empty();
//    personOpt.or




    List<Department> departments = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      departments.add(new Department("Department #" + i));
    }

    List<Person> persons = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      persons.add(new Person(
        "Person #" + i,
        ThreadLocalRandom.current().nextInt(20, 61),
        ThreadLocalRandom.current().nextInt(20_000, 100_000) * 1.0,
        departments.get(ThreadLocalRandom.current().nextInt(departments.size()))
      ));
    }

    // Найти сотрудника, который получает больше всех
    persons.stream()
      .max(Comparator.comparing(Person::getSalary))
      .ifPresent(System.out::println);

    Function<Person, Integer> personDepartmentNumberExtractor = person -> {
      String departmentName = person.getDepartment().getName();
      return Integer.parseInt(departmentName.split("#")[1]);
    };

    // Найти сотрудников, которые старше 40 лет и работают в департаменте с номером большe, и сохранить их в LinkedList
    LinkedList<Person> collect = persons.stream()
      .filter(it -> it.getAge() > 40)
      .filter(it -> personDepartmentNumberExtractor.apply(it) > 3)
      .collect(Collectors.toCollection(LinkedList::new));

    // Найти департаменты, в которых работают сотрудники, которые получают выше среднего
    double averageSalary = persons.stream()
      .mapToDouble(Person::getSalary)
      .average()
      .orElse(0.0);

    persons.stream()
      .filter(it -> it.getSalary() > averageSalary)
      .map(Person::getDepartment)
      .distinct()
      .forEach(System.out::println);


    // Собрать Map<String, List<Person>> - в которой ключ - имя отдела, значение - сотрудники, которые работают в этом отделе
    Map<String, List<Person>> personsGroupedByDepName = persons.stream()
      .collect(Collectors.groupingBy(it -> it.getDepartment().getName()));
    System.out.println(personsGroupedByDepName);


    // Собрать Map<String, Person> - в которой ключ - имя отдела, значение - сотрудник с самой высокой зарплатой в этом отделе
    Comparator<Person> salaryComparator = Comparator.comparing(Person::getSalary);
    Map<String, Person> maxSalary = persons.stream()
      .collect(Collectors.toMap(it -> it.getDepartment().getName(), Function.identity(), (first, second) -> {
        if (salaryComparator.compare(first, second) > 0) {
          return first;
        }

        return second;
      }));


  }

  public static class Person {
    private String name;
    private int age;
    private double salary;
    private Department department;

    public Person(String name, int age, double salary, Department department) {
      this.name = name;
      this.age = age;
      this.salary = salary;
      this.department = department;
    }

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }

    public double getSalary() {
      return salary;
    }

    public Department getDepartment() {
      return department;
    }

    @Override
    public String toString() {
      return "Person{" +
        "name='" + name + '\'' +
        ", age=" + age +
        ", salary=" + salary +
        ", department=" + department +
        '}';
    }
  }

  public static class Department {
    private String name;

    public Department(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Department that = (Department) o;
      return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name);
    }

    @Override
    public String toString() {
      return "Department{" +
        "name='" + name + '\'' +
        '}';
    }
  }

}
