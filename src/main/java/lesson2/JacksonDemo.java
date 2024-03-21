package lesson2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;

public class JacksonDemo {

  public static void main(String[] args) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter writer = objectMapper.writer().withDefaultPrettyPrinter();

    Student student = new Student();
    student.setFirstName("Igor");
    student.setFirstName("Chestnov");
    System.out.println(writer.writeValueAsString(student));

    Student readStudent = objectMapper.reader().readValue("""
      {
      "first_name": "1"
      }
      """, Student.class);
    System.out.println(readStudent);

    // camelCase
    // snake_case
  }

  private static class Student {

    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty(value = "blblalbab", required = true)
    private String secondName;

    public Student() {
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

    @Override
    public String toString() {
      return "Student{" +
        "firstName='" + firstName + '\'' +
        ", secondName='" + secondName + '\'' +
        '}';
    }
  }


}
