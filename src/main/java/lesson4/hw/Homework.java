package lesson4.hw;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.sql.*;
import java.util.List;

public class Homework {

    /**
     * 1. Создать сущность Student с полями:
     * 1.1 id - int
     * 1.2 firstName - string
     * 1.3 secondName - string
     * 1.4 age - int
     * 2. Подключить hibernate. Реализовать простые запросы: Find(by id), Persist, Merge, Remove
     * 3. Попробовать написать запрос поиска всех студентов старше 20 лет (session.createQuery)
     */

    public static void main(String[] args) throws SQLException {
        Student firstStudent = new Student(1, "Jessica", "Harris", 20);
        Student secondStudent = new Student(2, "Joseph", "Evans", 18);
        Student thirdStudent = new Student(3, "Amelia", "Brown", 21);

        Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {

            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                session.persist(firstStudent);
                session.persist(secondStudent);
                session.persist(thirdStudent);
                transaction.commit();
            }
                /*try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test", "user", "");
                     Statement statement = connection.createStatement()) {
                    ResultSet result = statement.executeQuery("select * from students;");
                    while (result.next()) {
                        System.out.print(result.getInt("id") + "\t");
                        System.out.print(result.getString("firstName") + "\t");
                        System.out.print(result.getString("secondName") + "\t");
                        System.out.print(result.getInt("age") + "\t");
                        System.out.println();
                    }
                }*/

            try (Session session = sessionFactory.openSession()) {
                Query<Student> query = session.createQuery("from Homework$Student", Student.class);
                List<Student> students = query.getResultList();
                System.out.println(students);
            }


            try (Session session = sessionFactory.openSession()) {
                Student student = session.find(Student.class, 1);
                System.out.println("Before change" + student.toString());
                firstStudent.setSecondName("Adams");
                Transaction transaction1 = session.beginTransaction();
                student = session.find(Student.class, 1);
                System.out.println("Before merge" + student.toString());
                session.merge(firstStudent);
                student = session.find(Student.class, 1);
                System.out.println("Before commit" + student.toString());
                transaction1.commit();
            }

            try (Session session = sessionFactory.openSession()) {
                Student student = session.find(Student.class, 1);
                System.out.println(student.toString());
            }

            try (Session session = sessionFactory.openSession()) {
                Query<Student> query1 = session.createQuery("select s from Homework$Student s where s.age > 20", Student.class);
                List<Student> students = query1.getResultList();
                System.out.println("Students elder than 20: " + students);
            }

            try (Session session = sessionFactory.openSession()) {
                Transaction transaction2 = session.beginTransaction();
                session.remove(secondStudent);
                transaction2.commit();
            }
            try (Session session = sessionFactory.openSession()) {
                Student student = session.find(Student.class, 2);
                System.out.println(student);
            }
        }
    }

    @Entity
    @Table(name = "students")
    public static class Student {
        @Id
        private int id;
        @Column(name = "firstName")
        private String firstName;
        @Column(name = "secondName")
        private String secondName;
        @Column(name = "age")
        private int age;

        public Student() {
        }

        public Student(int id, String firstName, String secondName, int age) {
            this.id = id;
            this.firstName = firstName;
            this.secondName = secondName;
            this.age = age;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "id=" + id +
                    ", firstName='" + firstName + '\'' +
                    ", secondName='" + secondName + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}

