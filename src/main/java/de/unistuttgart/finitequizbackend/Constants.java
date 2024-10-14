package de.unistuttgart.finitequizbackend;

/**
 * This class contains constants for defensive field checks
 * e.g. the game time cannot be smaller than 0 seconds
 */
public final class Constants {

    //----Game result attributes----
    public static final int MIN_QUESTION_COUNT = 0;
    public static final int MAX_QUESTION_COUNT = 600;

    //----Overworld result attributes----

    //score saved in %
    public static final long MIN_SCORE = 0;
    public static final long MAX_SCORE = 100;
    public static final int MIN_REWARDS = 0 ;
    public static final long MAX_REWARDS = 10;

    private Constants() {}
}
