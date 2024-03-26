package lesson3;

import java.sql.*;

public class JDBC {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test")) {
            acceptConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void acceptConnection(Connection connection) throws SQLException {
        createTable(connection);
        insertData(connection);
        deleteRandomRow(connection);
        updateRow(connection, "Igor", "Igor");

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select id, name, second_name from person");

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String secondName = resultSet.getString("second_name");

                System.out.println("id = " + id + ", name = " + name + ", second_name = " + secondName);
            }
        }
    }

    private static void insertData(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate("""
                    insert into person(id, name) values
                    (1, 'Igor'), 
                    (2, 'Person #2'), 
                    (3, 'John'), 
                    (4, 'Alex'), 
                    (5, 'Peter') 
                    """);

            System.out.println("INSERT: affected rows: " + affectedRows);
        }
    }

    private static void updateRow(Connection connection, String name, String secondName) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("update person set second_name = $1 where name = $2")) {
            stmt.setString(1, secondName);
            stmt.setString(2, name);

            stmt.executeUpdate();
        }

//    try (Statement statement = connection.createStatement()) {
//      statement.executeUpdate("update person set secondName = " + secondName + "where name = " + name);
//    }
    }

    private static void deleteRandomRow(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            System.out.println("DELETED: " + statement.executeUpdate("delete from person where id = 2"));
        }
    }


    private static void createTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                          create table person (
                            id bigint,
                            name varchar(256)
                    //        second_name varchar(256)
                          )
                          """);
        }
    }

}
