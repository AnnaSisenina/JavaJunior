package lesson2.myTests;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TryMyTest {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<Player> player = Player.class;
        Constructor<Player> playerConstructor = player.getConstructor(String.class, String.class);
        Player newPlayer = playerConstructor.newInstance("name", "21314");
        String str = "Hello";
        Method playerToString = player.getMethod("toString");
        System.out.println(playerToString.invoke(str));
    }
}
