package de.unistuttgart.finitequizbackend.service;

import de.unistuttgart.finitequizbackend.data.Configuration;
import de.unistuttgart.finitequizbackend.data.GameResult;
import de.unistuttgart.finitequizbackend.data.Question;
import de.unistuttgart.finitequizbackend.data.mapper.QuestionMapper;
import de.unistuttgart.finitequizbackend.data.statistic.ProblematicQuestion;
import de.unistuttgart.finitequizbackend.data.statistic.TimeSpentDistribution;
import de.unistuttgart.finitequizbackend.repositories.GameResultRepository;
import de.unistuttgart.finitequizbackend.repositories.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class StatisticService {

    static final int MAX_PROBLEMATIC_QUESTIONS = 5;
    static final int[] TIME_SPENT_DISTRIBUTION_PERCENTAGES = { 0, 10, 50, 90, 100};


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
            gameResult.getWrongAnsweredQuestions().forEach(roundResult -> {
                problematicQuestions.stream().filter(problematicQuestion -> problematicQuestion.getQuestion().getId().equals(roundResult.getQuestion().getId()))
                        .findAny().ifPresent(problematicQuestion -> {
                            problematicQuestion.addWrongAnswer();
                        });
            });

            // iterate over all correct answered round results and add correct answered counter for this problematic question
            gameResult.getCorrectAnsweredQuestions().forEach(roundResult -> {
                problematicQuestions.stream().filter(problematicQuestion -> problematicQuestion.getQuestion().getId().equals(roundResult.getQuestion().getId()))
                        .findAny().ifPresent(problematicQuestion -> {
                            problematicQuestion.addCorrectAnswer();
                        });
            });
        }

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

        return problematicQuestions.subList(0, Math.min(MAX_PROBLEMATIC_QUESTIONS, problematicQuestions.size()));
    }

    /**
     * Returns a list of the time spent distribution of a minigame
     *
     * @param configurationId the configuration id of the minigame
     * @return a list of the time spent distribution of a minigame
     */
    public List<TimeSpentDistribution> getTimeSpentDistributions(final UUID configurationId) {
        final List<GameResult> gameResults = gameResultRepository.findByConfigurationAsUUID(configurationId);
        final List<TimeSpentDistribution> timeSpentDistributions = new ArrayList<>();
        for (int i = 0; i < TIME_SPENT_DISTRIBUTION_PERCENTAGES.length - 1; i++) {
            TimeSpentDistribution timeSpentDistribution = new TimeSpentDistribution();
            timeSpentDistribution.setFromPercentage(TIME_SPENT_DISTRIBUTION_PERCENTAGES[i]);
            timeSpentDistribution.setToPercentage(TIME_SPENT_DISTRIBUTION_PERCENTAGES[i + 1]);
            timeSpentDistributions.add(timeSpentDistribution);
        }
        // order game results by time spent
        gameResults.sort((o1, o2) -> {
            if (o1.getTimeSpent() > o2.getTimeSpent()) {
                return 1;
            } else if (o1.getTimeSpent() < o2.getTimeSpent()) {
                return -1;
            } else {
                return 0;
            }
        });

        // calculate time spent time borders to time spent distribution percentage
        int currentGameResultIndex = 0;
        // TODO: make this work that every timedistrubution has the correct amount of counts
        for (TimeSpentDistribution timeSpentDistribution : timeSpentDistributions) {
            GameResult gameResult = null;
            while (currentGameResultIndex <= (timeSpentDistribution.getToPercentage() / 100L) * gameResults.size() && currentGameResultIndex < gameResults.size()) {
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
}
