package nl.malmberg.interactive_lessons;

import java.util.Random;

public class GameUtils {
    private static final Random RANDOM = new Random();
    private static final int MIN = 1000000;
    private static final int MAX = 10000000;

    private GameUtils() {
        //Hidden
    }

    public static String generateJoinKey() {
        return Integer.toString(RANDOM.nextInt(MAX - MIN) + MIN);
    }
}
