package co.paikama.stats;

import co.paikama.stats.repositories.DefaultRepository;

import java.lang.reflect.Field;

public class TestHelper {

    public static void resetSingleton() {
        final Field field;
        try {
            field = DefaultRepository.class.getDeclaredField("isTest");
            field.setAccessible(true);
            field.set(DefaultRepository.INSTANCE, true);
            DefaultRepository.INSTANCE.clearEntry();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
