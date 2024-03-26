package lesson3;


import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.io.IOException;
import java.util.*;


public class Homework {

    /**
     * 0. Разобрать код с семниара
     * 1. Повторить код с семниара без подглядываний на таблице Student с полями:
     * 1.1 id - int
     * 1.2 firstName - string
     * 1.3 secondName - string
     * 1.4 age - int
     * 2.* Попробовать подключиться к другой БД
     * 3.** Придумать, как подружить запросы и reflection:
     * 3.1 Создать аннотации Table, Id, Column
     * 3.2 Создать класс, у которого есть методы:
     * 3.2.1 save(Object obj) сохраняет объект в БД
     * 3.2.2 update(Object obj) обновляет объект в БД
     * 3.2.3 Попробовать объединить save и update (сначала select, потом update или insert)
     */
    public static void main(String[] args) {
        String dbURL = null;
        String dbUser = null;
        String dbPassword = null;

        Person firstPerson = new Person(1, "Samanta", "Wilson", 25);
        Person secondPerson = new Person(2, "George", "Harris", 24);
        Person thirdPerson = new Person(1, "Samanta", "Brown", 25);

        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            Properties properties = new Properties();
            properties.load(fis);
            dbURL = properties.getProperty("dbURL");
            dbUser = properties.getProperty("USER_NAME");
            dbPassword = properties.getProperty("PASSWORD");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(dbURL, dbUser, dbPassword)) {
            deleteTable(connection, "persons");
            createTablePersons(connection);
            addPerson(connection, firstPerson);
            updatePersonSurname(connection, 1, "Lewis");

            save(connection, secondPerson);
            update(connection, thirdPerson);

            readDataFromTable(connection, "persons");

        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Table(name = "persons")
    static class Person {
        @Id
        @Column(name = "id")
        private int id;
        @Column(name = "firstName")
        private String firstName;
        @Column(name = "secondName")
        private String surname;
        @Column(name = "age")
        private int age;

        public Person(int id, String firstName, String secondName, int age) {
            this.id = id;
            this.firstName = firstName;
            this.surname = secondName;
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

        public String getSecondNname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }


    /**
     * Создает таблицу Persons с полями id (int), firstName (varchar(20)), secondName (varchar(20)), age (int)
     *
     * @param connection
     * @throws SQLException
     */
    static void createTablePersons(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    create table persons (
                    id int not null,
                    firstName varchar(20) not null,
                    secondName varchar(20) not null,
                    age int,
                    primary key(id))"""
            );
            System.out.println("Table persons was created");
        }
    }


    /**
     * Выводит на экран таблицу по заданному названию, включая наименования столбцов и содержание полей
     *
     * @param connection
     * @param tableName
     * @throws SQLException
     */
    static void readDataFromTable(Connection connection, String tableName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from " + tableName);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columns = metaData.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();
            while (resultSet.next()) {
                for (int i = 1; i <= columns; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println();
            }
        }
    }

    /**
     * Удаляет таблицу по полученному названию
     *
     * @param connection
     * @param tableName
     * @throws SQLException
     */
    static void deleteTable(Connection connection, String tableName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("drop table if exists " + tableName);
            System.out.println("Table " + tableName + " was deleted");
        }
    }


    /**
     * Добавляет в таблицу Persons полученный объект Person
     *
     * @param connection
     * @param person
     * @throws SQLException
     */
    static void addPerson(Connection connection, Person person) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                insert into persons(id, firstName, secondName, age)
                values(?, ?, ?, ?)
                """)) {
            statement.setString(1, String.valueOf(person.getId()));
            statement.setString(2, person.getFirstName());
            statement.setString(3, person.getSecondNname());
            statement.setString(4, String.valueOf(person.getAge()));

            statement.execute();
        }
    }


    /**
     * Находит в таблице Persons объект по заданному Id и обновляет его значение в поле lastName
     *
     * @param connection
     * @param id
     * @param newSurname
     * @throws SQLException
     */
    static void updatePersonSurname(Connection connection, int id, String newSurname) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                update persons
                set secondName = ? 
                where id = ?
                """)) {
            statement.setString(1, newSurname);
            statement.setString(2, String.valueOf(id));

            statement.execute();
        }
    }

    /**
     * Добавляет полученный объект в базу SQL в таблицу, соответствующую классу этого объекта
     *
     * @param connection
     * @param obj
     * @throws IllegalAccessException
     */
    static void save(Connection connection, Object obj) throws IllegalAccessException {
        Class<?> objClass = obj.getClass();

        List<Field> objFields = Arrays.stream(objClass.getDeclaredFields())
                .filter(it -> it.isAnnotationPresent(Column.class))
                .toList();

        StringBuilder queryFirstPart = new StringBuilder("insert into " + objClass.getAnnotation(Table.class).name() + " (");
        StringBuilder querySecondPart = new StringBuilder("values (");

        Iterator<Field> iterator = objFields.iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            queryFirstPart.append(field.getAnnotation(Column.class).name());
            querySecondPart.append("\"").append(String.valueOf(field.get(obj))).append("\"");
            if (iterator.hasNext()) {
                queryFirstPart.append(", ");
                querySecondPart.append(", ");
            }
        }
        queryFirstPart.append(") ");
        querySecondPart.append(");");

        try (Statement statement = connection.createStatement()) {
            statement.execute(queryFirstPart.toString() + querySecondPart.toString());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Обновляет информацию о заданном объекте в базе MySQL в соответствующей таблице,
     * если в классе не задан PrimaryKey или строки с полученным значением PrimaryKey в таблице нет, добавляет данные новой строкой
     *
     * @param connection
     * @param obj
     * @throws IllegalAccessException
     */
    static void update(Connection connection, Object obj) throws IllegalAccessException {
        Class<?> objClass = obj.getClass();

        List<Field> objFields = getObjectFields(objClass);
        Optional<Field> primaryKey = getObjectPrimaryKey(objClass);

        if (primaryKey.isPresent() && checkRowInTable(obj, connection)) {
            StringBuilder query = new StringBuilder("update " + objClass.getAnnotation(Table.class).name() + " set ");

            Iterator<Field> iterator = objFields.iterator();
            while (iterator.hasNext()) {
                Field field = iterator.next();
                query.append(field.getAnnotation(Column.class).name())
                        .append(" = ")
                        .append("\"")
                        .append(String.valueOf(field.get(obj)))
                        .append("\"");
                if (iterator.hasNext()) {
                    query.append(", ");
                }
            }
            query.append(" where ").append(primaryKey.get().getName()).append(" = \"").append(primaryKey.get().get(obj)).append("\";");

            try (Statement statement = connection.createStatement()) {
                statement.execute(query.toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            save(connection, obj);
        }
    }

    /**
     *
     * Метод проверяет есть ли полученный объект в базе данных SQL
     *
     * @param obj
     * @param connection
     * @return
     */
    private static boolean checkRowInTable(Object obj, Connection connection) {
        Class<?> objClass = obj.getClass();
        List<Field> objFields = getObjectFields(objClass);
        Optional<Field> primaryKey = getObjectPrimaryKey(objClass);

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from " + objClass.getAnnotation(Table.class).name()
                    + " where " + primaryKey.get().getName() + " = \"" + primaryKey.get().get(obj) + "\";");
            return resultSet.next();
            }
        catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private static Optional<Field> getObjectPrimaryKey(Class<?> objClass) {
        return Arrays.stream(objClass.getDeclaredFields())
                .filter(it -> it.isAnnotationPresent(Id.class))
                .findFirst();
    }

    private static List<Field> getObjectFields(Class<?> objClass) {
        return Arrays.stream(objClass.getDeclaredFields())
                .filter(it -> it.isAnnotationPresent(Column.class))
                .toList();
    }
}