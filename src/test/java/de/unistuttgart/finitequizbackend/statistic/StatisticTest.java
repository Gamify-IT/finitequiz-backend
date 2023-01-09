package de.unistuttgart.finitequizbackend.statistic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.finitequizbackend.data.*;
import de.unistuttgart.finitequizbackend.data.mapper.QuestionMapper;
import de.unistuttgart.finitequizbackend.data.statistic.ProblematicQuestion;
import de.unistuttgart.finitequizbackend.data.statistic.TimeSpentDistribution;
import de.unistuttgart.finitequizbackend.repositories.ConfigurationRepository;
import de.unistuttgart.finitequizbackend.repositories.GameResultRepository;
import de.unistuttgart.finitequizbackend.service.GameResultService;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import java.util.*;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StatisticTest {

    private final String API_URL = "/statistics";

    @MockBean
    JWTValidatorService jwtValidatorService;

    Cookie cookie = new Cookie("access_token", "testToken");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GameResultRepository gameResultRepository;

    @Autowired
    private GameResultService gameResultService;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ConfigurationRepository configurationRepository;

    private ObjectMapper objectMapper;

    private Configuration randomConfiguration;
    private Configuration staticConfiguration;
    int numberOfGameResultsOfStaticConfiguration;
    private QuestionDTO problematicQuestion;
    private QuestionDTO bestAnsweredQuestion;
    private List<GameResult> gameResults;

    @BeforeEach
    public void createBasicData() {
        gameResultRepository.deleteAll();

        Set<Question> questions = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            questions.add(new Question("question" + i, "answer" + i, Set.of("answer2", "answer3", "answer4")));
        }

        randomConfiguration = new Configuration();
        randomConfiguration.setQuestions(questions);
        randomConfiguration = configurationRepository.save(randomConfiguration);

        gameResults = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            GameResult gameResult = new GameResult();
            gameResult.setConfigurationAsUUID(randomConfiguration.getId());
            gameResult.setPlayerId(UUID.randomUUID().toString());
            gameResult.setPlayedTime(new Date());
            List<RoundResult> wrongAnswers = new ArrayList<>();
            List<RoundResult> correctAnswers = new ArrayList<>();
            for (Question question : randomConfiguration.getQuestions()) {
                if (new Random().nextInt(10) > 3) {
                    correctAnswers.add(new RoundResult(question, question.getRightAnswer()));
                } else {
                    wrongAnswers.add(new RoundResult(question, UUID.randomUUID().toString()));
                }
            }
            gameResult.setCorrectAnsweredQuestions(correctAnswers);
            gameResult.setWrongAnsweredQuestions(wrongAnswers);
            gameResult.setQuestionCount(questions.size());
            gameResult.setScore(gameResultService.calculateResultScore(correctAnswers.size(), questions.size()));
            gameResult = gameResultRepository.save(gameResult);
            gameResults.add(gameResult);
        }

        questions = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            questions.add(new Question("question" + i, "answer" + i, Set.of("answer2", "answer3", "answer4")));
        }

        staticConfiguration = new Configuration();
        staticConfiguration.setQuestions(questions);
        staticConfiguration = configurationRepository.save(staticConfiguration);

        List<Question> questionList = questions.stream().toList();

        GameResult gameResult1 = new GameResult();
        gameResult1.setConfigurationAsUUID(staticConfiguration.getId());
        gameResult1.setPlayerId(UUID.randomUUID().toString());
        gameResult1.setPlayedTime(new Date());
        List<RoundResult> wrongAnswers1 = new ArrayList<>();
        List<RoundResult> rightAnswers1 = new ArrayList<>();
        rightAnswers1.add(new RoundResult(questionList.get(0), questionList.get(0).getRightAnswer()));
        for (int i = 1; i < questionList.size(); i++) {
            wrongAnswers1.add(new RoundResult(questionList.get(i), UUID.randomUUID().toString()));
        }
        gameResult1.setCorrectAnsweredQuestions(rightAnswers1);
        gameResult1.setWrongAnsweredQuestions(wrongAnswers1);
        gameResult1.setTimeSpent(60);

        GameResult gameResult2 = new GameResult();
        gameResult2.setConfigurationAsUUID(staticConfiguration.getId());
        gameResult2.setPlayerId(UUID.randomUUID().toString());
        gameResult2.setPlayedTime(new Date());
        List<RoundResult> wrongAnswers2 = new ArrayList<>();
        List<RoundResult> rightAnswers2 = new ArrayList<>();
        rightAnswers2.add(new RoundResult(questionList.get(0), questionList.get(0).getRightAnswer()));
        for (int i = 1; i < questionList.size(); i++) {
            wrongAnswers2.add(new RoundResult(questionList.get(i), UUID.randomUUID().toString()));
        }
        gameResult2.setCorrectAnsweredQuestions(rightAnswers2);
        gameResult2.setWrongAnsweredQuestions(wrongAnswers2);
        gameResult2.setTimeSpent(100);

        GameResult gameResult3 = new GameResult();
        gameResult3.setConfigurationAsUUID(staticConfiguration.getId());
        gameResult3.setPlayerId(UUID.randomUUID().toString());
        gameResult3.setPlayedTime(new Date());
        List<RoundResult> wrongAnswers3 = new ArrayList<>();
        List<RoundResult> rightAnswers3 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            rightAnswers3.add(new RoundResult(questionList.get(i), questionList.get(i).getRightAnswer()));
        }
        for (int i = 2; i < questionList.size(); i++) {
            wrongAnswers3.add(new RoundResult(questionList.get(i), UUID.randomUUID().toString()));
        }
        gameResult3.setCorrectAnsweredQuestions(rightAnswers3);
        gameResult3.setWrongAnsweredQuestions(wrongAnswers3);
        gameResult3.setTimeSpent(200);

        GameResult gameResult4 = new GameResult();
        gameResult4.setConfigurationAsUUID(staticConfiguration.getId());
        gameResult4.setPlayerId(UUID.randomUUID().toString());
        gameResult4.setPlayedTime(new Date());
        List<RoundResult> wrongAnswers4 = new ArrayList<>();
        List<RoundResult> rightAnswers4 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rightAnswers4.add(new RoundResult(questionList.get(i), questionList.get(i).getRightAnswer()));
        }
        for (int i = 5; i < questionList.size(); i++) {
            wrongAnswers4.add(new RoundResult(questionList.get(i), UUID.randomUUID().toString()));
        }
        gameResult4.setCorrectAnsweredQuestions(rightAnswers4);
        gameResult4.setWrongAnsweredQuestions(wrongAnswers4);
        gameResult4.setTimeSpent(300);

        numberOfGameResultsOfStaticConfiguration = 4;

        problematicQuestion = questionMapper.questionToQuestionDTO(questionList.get(5));
        bestAnsweredQuestion = questionMapper.questionToQuestionDTO(questionList.get(0));

        gameResultRepository.saveAll(List.of(gameResult1, gameResult2, gameResult3, gameResult4));

        objectMapper = new ObjectMapper();
        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
    }

    @Test
    public void testGetProblematicQuestions() throws Exception {
        final MvcResult result = mvc
            .perform(
                get(API_URL + "/" + staticConfiguration.getId() + "/problematic-questions")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final List<ProblematicQuestion> problematicQuestions = Arrays.asList(
            objectMapper.readValue(result.getResponse().getContentAsString(), ProblematicQuestion[].class)
        );

        for (int i = 0; i < problematicQuestions.size() - 1; i++) {
            assertTrue(
                problematicQuestions.get(i).getWrongAnswers() >= problematicQuestions.get(i + 1).getWrongAnswers()
            );
        }

        assertFalse(
            problematicQuestions
                .stream()
                .map(ProblematicQuestion::getQuestion)
                .anyMatch(question -> question.equals(bestAnsweredQuestion))
        );
        assertEquals(problematicQuestion, problematicQuestions.get(0).getQuestion());
        assertSame(5, problematicQuestions.size());
    }

    @Test
    public void testGetTimeSpentDistribution() throws Exception {
        final MvcResult result = mvc
            .perform(
                get(API_URL + "/" + staticConfiguration.getId() + "/time-spent")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final List<TimeSpentDistribution> timeSpentDistributions = Arrays.asList(
            objectMapper.readValue(result.getResponse().getContentAsString(), TimeSpentDistribution[].class)
        );
        long amountOfGameResults = timeSpentDistributions.stream().map(TimeSpentDistribution::getCount).count();
        assertEquals(numberOfGameResultsOfStaticConfiguration, amountOfGameResults);
    }
}
