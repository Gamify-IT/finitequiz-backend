package de.unistuttgart.finitequizbackend.data.statistic;

import de.unistuttgart.finitequizbackend.data.QuestionDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProblematicQuestion {

    int attempts;
    int correctAnswers;
    int wrongAnswers;

    QuestionDTO question;

    public void addCorrectAnswer() {
        correctAnswers++;
        attempts++;
    }

    public void addWrongAnswer() {
        wrongAnswers++;
        attempts++;
    }

}
