package de.unistuttgart.finitequizbackend.service;

import de.unistuttgart.finitequizbackend.clients.ResultClient;
import de.unistuttgart.finitequizbackend.data.*;
import de.unistuttgart.finitequizbackend.repositories.GameResultRepository;
import de.unistuttgart.finitequizbackend.repositories.QuestionRepository;
import feign.FeignException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * This service handles the logic for the GameResultController.class
 */
@Service
@Slf4j
@Transactional
public class GameResultService {

    @Autowired
    ResultClient resultClient;

    @Autowired
    GameResultRepository gameResultRepository;

    @Autowired
    QuestionRepository questionRepository;

    private static int hundredScoreCount = 0;


    /**
     * Cast list of question texts to a List of Questions
     *
     * @param roundResultDTOs list of RoundResults
     * @return a list of questions
     */
    public List<RoundResult> castQuestionList(final List<RoundResultDTO> roundResultDTOs) {
        final List<RoundResult> questionList = new ArrayList<>();
        for (final RoundResultDTO roundResultDTO : roundResultDTOs) {
            final Optional<Question> questionToAdd = questionRepository.findById(roundResultDTO.getQuestionUUId());
            if (questionToAdd.isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("There is no question with uuid %s.", roundResultDTO.getQuestionUUId())
                );
            } else {
                final RoundResult roundResult = new RoundResult(questionToAdd.get(), roundResultDTO.getAnswer());
                questionList.add(roundResult);
            }
        }
        return questionList;
    }

    /**
     * Casts a GameResultDTO to GameResult and saves it in the Database
     *
     * @param gameResultDTO extern gameResultDTO
     * @param userId id of the user
     * @param accessToken accessToken of the user
     * @throws IllegalArgumentException if at least one of the arguments is null
     */
    public void saveGameResult(
        final @Valid GameResultDTO gameResultDTO,
        final String userId,
        final String accessToken
    ) {
        if (gameResultDTO == null || userId == null || accessToken == null) {
            throw new IllegalArgumentException("gameResultDTO or userId is null");
        }
        final int resultScore = calculateResultScore(
            gameResultDTO.getCorrectAnsweredQuestions().size(),
            gameResultDTO.getQuestionCount()
        );

        final int rewards = calculateRewards(resultScore);
        gameResultDTO.setRewards(rewards);

        final OverworldResultDTO resultDTO = new OverworldResultDTO(
            gameResultDTO.getConfigurationAsUUID(),
            resultScore,
            userId,
                rewards
        );
        try {
            resultClient.submit(accessToken, resultDTO);
            final List<RoundResult> correctQuestions =
                this.castQuestionList(gameResultDTO.getCorrectAnsweredQuestions());
            final List<RoundResult> wrongQuestions = this.castQuestionList(gameResultDTO.getWrongAnsweredQuestions());
            final GameResult result = new @Valid GameResult(
                gameResultDTO.getQuestionCount(),
                gameResultDTO.getScore(),
                gameResultDTO.getTimeSpent(),
                rewards,
                correctQuestions,
                wrongQuestions,
                gameResultDTO.getConfigurationAsUUID(),
                userId
            );
            gameResultRepository.save(result);
        } catch (final FeignException.BadGateway badGateway) {
            final String warning =
                "The Overworld backend is currently not available. The result was NOT saved. Please try again later";
            log.error(warning + badGateway);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, warning);
        } catch (final FeignException.NotFound notFound) {
            final String warning = "The result could not be saved. Unknown User";
            log.error(warning + notFound);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, warning);
        }
    }

    /**
     * calculates the score a player made
     *
     * @param correctAnswers    correct answer count
     * @param numberOfQuestions available question count
     * @return score as int in %
     * @throws IllegalArgumentException if correctAnswers < 0 || numberOfQuestions < correctAnswers
     */
    public int calculateResultScore(final int correctAnswers, final int numberOfQuestions) {
        if (correctAnswers < 0 || numberOfQuestions < correctAnswers) {
            throw new IllegalArgumentException(
                String.format(
                    "correctAnswers (%s) or numberOfQuestions (%s) is not possible",
                    correctAnswers,
                    numberOfQuestions
                )
            );
        }
        return (int) ((100.0 * correctAnswers) / numberOfQuestions);
    }

    private int calculateRewards(final int resultScore) {
        if (resultScore < 0) {
            throw new IllegalArgumentException("Result score cannot be less than zero");
        }
        if (resultScore == 100 && hundredScoreCount < 3) {
            hundredScoreCount++;
            return 10;
        } else if (resultScore == 100 && hundredScoreCount >= 3) {
            return 5;
        }
        return resultScore/10;
    }
}
