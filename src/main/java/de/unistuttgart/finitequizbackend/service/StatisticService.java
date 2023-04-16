package de.unistuttgart.finitequizbackend.service;

import de.unistuttgart.finitequizbackend.data.Configuration;
import de.unistuttgart.finitequizbackend.data.GameResult;
import de.unistuttgart.finitequizbackend.data.Question;
import de.unistuttgart.finitequizbackend.data.mapper.QuestionMapper;
import de.unistuttgart.finitequizbackend.data.statistic.ProblematicQuestion;
import de.unistuttgart.finitequizbackend.data.statistic.TimeSpentDistribution;
import de.unistuttgart.finitequizbackend.repositories.GameResultRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class StatisticService {

    static final int MAX_PROBLEMATIC_QUESTIONS = 5;
    static final int[] TIME_SPENT_DISTRIBUTION_PERCENTAGES = { 0, 25, 50, 75, 100 };

    @Autowired
    private ConfigService configService;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private GameResultRepository gameResultRepository;

    /**
     * Returns a list of the most problematic questions of a minigame
     *
     * @param configurationId the configuration id of the minigame
     * @return a list of the most problematic questions of a minigame
     */
    public List<ProblematicQuestion> getProblematicQuestions(final UUID configurationId) {
        final Configuration configuration = configService.getConfiguration(configurationId);
        final List<GameResult> gameResults = gameResultRepository.findByConfigurationAsUUID(configurationId);
        final List<ProblematicQuestion> problematicQuestions = new ArrayList<>();
        for (Question question : configuration.getQuestions()) {
            problematicQuestions.add(new ProblematicQuestion(0, 0, 0, questionMapper.questionToQuestionDTO(question)));
        }

        for (final GameResult gameResult : gameResults) {
            // iterate over all wrong answered round results and add wrong answered counter for this problematic question
            countWrongAnsweredQuestions(problematicQuestions, gameResult);

            // iterate over all correct answered round results and add correct answered counter for this problematic question
            countRightAnsweredQuestions(problematicQuestions, gameResult);
        }
        sortProblematicQuestionsByPercentageWrongAnswers(problematicQuestions);
        return problematicQuestions.subList(0, Math.min(MAX_PROBLEMATIC_QUESTIONS, problematicQuestions.size()));
    }

    /**
     * Counts amount of right answered questions and adds them to the problematic question list
     *
     * @param problematicQuestions the list of problematic questions where the right answered counter should be increased
     * @param gameResult the game result which contains the answered questions
     */
    private void countRightAnsweredQuestions(List<ProblematicQuestion> problematicQuestions, GameResult gameResult) {
        gameResult
            .getCorrectAnsweredQuestions()
            .forEach(roundResult -> problematicQuestions
                .stream()
                .filter(problematicQuestion ->
                    problematicQuestion.getQuestion().getId().equals(roundResult.getQuestion().getId())
                )
                .findAny()
                .ifPresent(ProblematicQuestion::addCorrectAnswer));
    }

    /**
     * Counts amount of wrong answered questions and adds them to the problematic question list
     *
     * @param problematicQuestions the list of problematic questions where the wrong answered counter should be increased
     * @param gameResult the game result which contains the answered questions
     */
    private void countWrongAnsweredQuestions(List<ProblematicQuestion> problematicQuestions, GameResult gameResult) {
        gameResult
            .getWrongAnsweredQuestions()
            .forEach(roundResult -> problematicQuestions
                .stream()
                .filter(problematicQuestion ->
                    problematicQuestion.getQuestion().getId().equals(roundResult.getQuestion().getId())
                )
                .findAny()
                .ifPresent(ProblematicQuestion::addWrongAnswer));
    }

    /**
     * Sorts the list of problematic questions by the amount of wrong answers to attempts
     *
     * @param problematicQuestions the list of problematic questions to sort
     */
    private void sortProblematicQuestionsByPercentageWrongAnswers(List<ProblematicQuestion> problematicQuestions) {
        problematicQuestions.sort((o1, o2) -> {
            double percantageWrong1 = (double) o1.getWrongAnswers() / (double) o1.getAttempts();
            double percantageWrong2 = (double) o2.getWrongAnswers() / (double) o2.getAttempts();
            if (percantageWrong1 > percantageWrong2) {
                return -1;
            } else if (percantageWrong1 < percantageWrong2) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    /**
     * Returns a list of the time spent distribution of a minigame
     *
     * @param configurationId the configuration id of the minigame
     * @return a list of the time spent distribution of a minigame
     */
    public List<TimeSpentDistribution> getTimeSpentDistributions(final UUID configurationId) {
        if (TIME_SPENT_DISTRIBUTION_PERCENTAGES.length < 2) {
            throw new IllegalArgumentException("TIME_SPENT_DISTRIBUTION_PERCENTAGES must have at least 2 elements");
        }
        if (TIME_SPENT_DISTRIBUTION_PERCENTAGES[0] != 0) {
            throw new IllegalArgumentException("TIME_SPENT_DISTRIBUTION_PERCENTAGES must start with 0");
        }
        if (TIME_SPENT_DISTRIBUTION_PERCENTAGES[TIME_SPENT_DISTRIBUTION_PERCENTAGES.length - 1] != 100) {
            throw new IllegalArgumentException("TIME_SPENT_DISTRIBUTION_PERCENTAGES must end with 100");
        }
        final List<GameResult> gameResults = gameResultRepository.findByConfigurationAsUUID(configurationId);
        final List<TimeSpentDistribution> timeSpentDistributions = new ArrayList<>();
        for (int i = 0; i < TIME_SPENT_DISTRIBUTION_PERCENTAGES.length - 1; i++) {
            TimeSpentDistribution timeSpentDistribution = new TimeSpentDistribution();
            timeSpentDistribution.setFromPercentage(TIME_SPENT_DISTRIBUTION_PERCENTAGES[i]);
            timeSpentDistribution.setToPercentage(TIME_SPENT_DISTRIBUTION_PERCENTAGES[i + 1]);
            timeSpentDistributions.add(timeSpentDistribution);
        }
        // order game results by time spent
        sortGameResultsByPlayedTime(gameResults);

        // calculate time spent time borders to time spent distribution percentage
        int currentGameResultIndex = 0;
        for (TimeSpentDistribution timeSpentDistribution : timeSpentDistributions) {
            GameResult gameResult = null;
            while (currentGameResultIndex < (timeSpentDistribution.getToPercentage() / 100.0) * gameResults.size()) {
                gameResult = gameResults.get(currentGameResultIndex);
                if (timeSpentDistribution.getFromTime() == 0) {
                    timeSpentDistribution.setFromTime(gameResult.getTimeSpent());
                }
                timeSpentDistribution.addCount();
                currentGameResultIndex++;
            }
            if (gameResult != null) {
                timeSpentDistribution.setToTime(gameResult.getTimeSpent());
            }
        }
        return timeSpentDistributions;
    }

    /**
     * Sorts a list of game results by played time
     *
     * @param gameResults the list of game results to sort
     */
    private void sortGameResultsByPlayedTime(List<GameResult> gameResults) {
        gameResults.sort((o1, o2) -> {
            if (o1.getTimeSpent() > o2.getTimeSpent()) {
                return 1;
            } else if (o1.getTimeSpent() < o2.getTimeSpent()) {
                return -1;
            } else {
                return 0;
            }
        });
    }
}
