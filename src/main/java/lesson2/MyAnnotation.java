package lesson2;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAnnotation {

  // int bool ... - все примитивы
  // String
  // Class
  // любой enum
  // массив всего, что выше

  String[] myParameter() default "defaultValue";

}
