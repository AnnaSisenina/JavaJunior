package lesson4;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "department")
public class Department {

  @Id
  private Long id;

  @Column(name = "name")
  private String name;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Department{" +
      "id=" + id +
      ", name='" + name + '\'' +
      '}';
  }
}
