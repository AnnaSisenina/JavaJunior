package lesson2.tests;


import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TestRunner {

    public static void run(Class<?> testClass) {
        final Object testObj = initTestObj(testClass);

        List<Method> sortedTests = Arrays.stream(testClass.getDeclaredMethods())
                .filter(it -> it.getModifiers() != Modifier.PRIVATE)
                .filter(it -> it.isAnnotationPresent(Test.class))
                .sorted(Comparator.comparing(it -> it.getAnnotation(Test.class).order()))
                .toList();
        List<Method> beforeEach = Arrays.stream(testClass.getDeclaredMethods())
                .filter(it -> it.getModifiers() != Modifier.PRIVATE)
                .filter(it -> it.getAnnotation(BeforeEach.class) != null)
                .toList();
        List<Method> afterEach = Arrays.stream(testClass.getDeclaredMethods())
                .filter(it -> it.getModifiers() != Modifier.PRIVATE)
                .filter(it -> it.isAnnotationPresent(AfterEach.class))
                .toList();


        findAndExecuteAllMethods(testObj, testClass, BeforeAll.class);
        for (Method testMethod : sortedTests) {
                executeListOfMethods(testObj, testClass, beforeEach);
                try {
                    testMethod.invoke(testObj);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                executeListOfMethods(testObj, testClass, afterEach);
            }
        findAndExecuteAllMethods(testObj, testClass, AfterAll.class);

    }

    private static void executeListOfMethods(Object testObj, Class<?> testClass, List<Method> methods) {
        try {
            for (Method method : methods) {
                method.invoke(testObj);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void findAndExecuteAllMethods(Object testObj, Class<?> testClass, Class<? extends Annotation> annotationClass) {
        Arrays.stream(testClass.getDeclaredMethods())
                .filter(it -> it.getModifiers() != Modifier.PRIVATE)
                .filter(it -> it.isAnnotationPresent(annotationClass)).forEach(it -> {
                    try {
                        it.invoke(testObj);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static Object initTestObj(Class<?> testClass) {
        try {
            Constructor<?> noArgsConstructor = testClass.getConstructor();
            return noArgsConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Нет конструктора по умолчанию");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Не удалось создать объект тест класса");
        }
    }

}
