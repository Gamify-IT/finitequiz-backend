package de.unistuttgart.finitequizbackend.data.statistic;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeSpentDistribution {

    /**
     * The start of the percentage of game results that took between fromTime and toTime to finish.
     */
    long fromPercentage;
    /**
     * The end of the percentage of game results that took between fromTime and toTime to finish.
     */
    long toPercentage;

    /**
     * The lower bound of time game results that spent time on the quiz.
     */
    long fromTime;
    /**
     * The upper bound of time game results that spent time on the quiz.
     */
    long toTime;

    /**
     * The amount of game results that took between fromTime and toTime to finish.
     */
    int count;

    public void addCount() {
        count++;
    }

}
