package lesson1.hw;

import lesson1.Optionals;
import lesson1.Streams;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Homework {

    /**
     * Вывести на консоль отсортированные (по алфавиту) имена персонов
     */
    public void printNamesOrdered(List<Streams.Person> persons) {

        persons.stream()
                .map(Streams.Person::getName)
                .sorted()
                .forEach(System.out::println);
    }

    /**
     * В каждом департаменте найти самого взрослого сотрудника.
     * Map<Department, Person>
     */
    public Map<Streams.Department, Streams.Person> findDepartmentOldestPerson(List<Streams.Person> persons) {
        Comparator<Streams.Person> ageComparator = Comparator.comparing(Streams.Person::getAge);
        return persons.stream()
                .collect(Collectors.toMap(Streams.Person::getDepartment, Function.identity(), (first, second) -> {
                    if (ageComparator.compare(first, second) > 0) {
                        return first;
                    }
                    return second;
                }));
    }

    /**
     * Найти 10 первых сотрудников, младше 30 лет, у которых зарплата выше 50_000
     */
    public List<Streams.Person> findFirstPersons(List<Streams.Person> persons) {
        return persons.stream()
                .filter(it -> it.getSalary() > 50_000)
                .filter(it -> it.getAge() < 30)
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Найти депаратмент, чья суммарная зарплата всех сотрудников максимальна
     */
    public Optional<Streams.Department> findTopDepartment(List<Streams.Person> persons) {
        return persons.stream()
                .collect(Collectors.toMap(Streams.Person::getDepartment, Streams.Person::getSalary, Double::sum))
                .entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

}
