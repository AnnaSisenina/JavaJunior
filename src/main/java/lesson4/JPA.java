package lesson4;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class JPA {

  // JPA Java (Jakarta) Persistence API
  // Hibernate - реализация спецификации JPA
  // EclipseLink

  public static void main(String[] args) throws SQLException {
    Configuration configuration = new Configuration().configure();
    try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
      insertPersons(sessionFactory);
//
//      try (Session session = sessionFactory.openSession()) {
//        // SQL Structure Query Language
//        // JQP Java Query Language
//
//        Query<Department> query = session.createQuery("select p from Department p", Department.class);
//        System.out.println(query.getSingleResult());
//      }

      try (Session session = sessionFactory.openSession()) {
        Transaction tx = session.beginTransaction();
        Person person = session.find(Person.class, 1L);
        System.out.println(person);

        person.setName("NEW NAME");
        session.merge(person);

        tx.commit();
      }

      List<Person> persons = new ArrayList<>();
      try (Session session = sessionFactory.openSession()) {
        // SQL Structure Query Language
        // JQP Java Query Language

        Query<Person> query = session.createQuery("select p from Person p where id > :id", Person.class);
        query.setParameter("id", 5);
        List<Person> resultList = query.getResultList();

        Transaction tx = session.beginTransaction();
        for (Person person : resultList) {
          person.setName("UPDATED");
//          session.merge(person);
        }
        tx.commit();

        persons.addAll(session.createQuery("from Person p", Person.class).getResultList());
        System.out.println(persons);
      }


      try (Session session = sessionFactory.openSession()) {
        Person person = session.find(Person.class, 1L);
        System.out.println(person);

        Transaction tx = session.beginTransaction();
        session.remove(person);
        tx.commit();
      }

      try (Session session = sessionFactory.openSession()) {
        Person person = session.find(Person.class, 1L);
        System.out.println(person);
      }
    }
  }

  private static void insertPersons(SessionFactory sessionFactory) {
    try (Session session = sessionFactory.openSession()) {
      Transaction tx = session.beginTransaction();

      Department department = new Department();
      department.setId(555L);
      department.setName("DEPARTMENT NAME");
      session.persist(department);

      for (long i = 1; i <= 10; i++) {
        Person person = new Person();
        person.setId(i);
        person.setName("Person #" + i);
        person.setDepartment(department);

        session.persist(person);
      }

      tx.commit();
    }
  }

}
