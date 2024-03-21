package lesson2;



import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflections {

  public static void main(String[] args) throws Exception {
    // .java -> .class (bytecode) -> JVM (bytecode ->

    Class<User> userClass = User.class;
    Constructor<User> constructor = userClass.getConstructor(String.class, String.class);
    User inchestnov = constructor.newInstance("inchestnov", "pa$$");

    Method methodGetLogin = userClass.getMethod("getLogin");
    String result = (String) methodGetLogin.invoke(inchestnov); // inchestnov.getLogin();
    System.out.println(result);

    Method setPassword = userClass.getMethod("setPassword", String.class); // inchestnov.setPassword("newPassword");
    setPassword.invoke(inchestnov, "newPassword");
    System.out.println(inchestnov.getPassword());

    System.out.println(userClass.getMethod("getCounter").invoke(null));

    Field password = userClass.getDeclaredField("password");
    System.out.println(password.get(inchestnov)); // inchestnov.login

    Field login = userClass.getDeclaredField("login");
    login.setAccessible(true);
    login.set(inchestnov, "newValue");

    System.out.println(inchestnov);

    // declared - все, что объявлено в конкретном классе (без учета наследования)
    // not declared - все доступные (с учетом наследования)

    MyAnnotation anno = SuperUser.class.getMethod("setPassword", String.class).getAnnotation(MyAnnotation.class);
    System.out.println(anno.myParameter());

  }

  static class User {

    private static long counter = 0L;

    private final String login;
    private String password;

    public User(String login) {
      this(login, "defaultpassword");
    }

    public User(String login, String password) {
      this.login = login;
      this.password = password;
      counter++;
    }

    public static long getCounter() {
      return counter;
    }

    public String getLogin() {
      return login;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    @Override
    public String toString() {
      return "User{" +
        "login='" + login + '\'' +
        ", password='" + password + '\'' +
        '}';
    }
  }

  static class SuperUser extends User {

    public SuperUser(String login) {
      super(login, "");
    }

    @Override
    @MyAnnotation(myParameter = "text")
    public void setPassword(String password) {
      throw new UnsupportedOperationException();
    }
  }



}
