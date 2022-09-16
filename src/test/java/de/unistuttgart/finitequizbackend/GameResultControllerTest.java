package de.unistuttgart.finitequizbackend;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import de.unistuttgart.finitequizbackend.data.*;
import de.unistuttgart.finitequizbackend.data.mapper.ConfigurationMapper;
import de.unistuttgart.finitequizbackend.repositories.ConfigurationRepository;
import de.unistuttgart.finitequizbackend.repositories.GameResultRepository;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { WireMockConfig.class })
class GameResultControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ConfigurationMapper configurationMapper;

  @Autowired
  private ConfigurationRepository configurationRepository;

  @Autowired
  private GameResultRepository gameResultRepository;

  @Autowired
  private WireMockServer mockResultsService;

  private ObjectMapper objectMapper;
  private Configuration initialConfig;
  private ConfigurationDTO initialConfigDTO;

  private Question initialQuestion1;
  private Question initialQuestion2;

  private final String API_URL = "/results";

  @MockBean
  JWTValidatorService jwtValidatorService;
  Cookie cookie = new Cookie("access_token", "testToken");

  @BeforeEach
  public void createBasicData() throws IOException {
    ResultMocks.setupMockBooksResponse(mockResultsService);
    configurationRepository.deleteAll();
    initialQuestion1 = new Question();
    initialQuestion1.setText("Are you cool?");
    initialQuestion1.setRightAnswer("Yes");
    initialQuestion1.setWrongAnswers(Set.of("No", "Maybe"));

    initialQuestion2 = new Question();
    initialQuestion2.setText("Is this game cool?");
    initialQuestion2.setRightAnswer("Yes");
    initialQuestion2.setWrongAnswers(Set.of("No", "Maybe"));

    final Configuration configuration = new Configuration();
    configuration.setQuestions(Set.of(initialQuestion1, initialQuestion2));

    initialConfig = configurationRepository.save(configuration);
    initialConfigDTO = configurationMapper.configurationToConfigurationDTO(initialConfig);
    initialConfig
      .getQuestions()
      .stream()
      .filter(question -> question.getText().equals(initialQuestion1.getText()))
      .forEach(question -> initialQuestion1 = question);
    initialConfig
      .getQuestions()
      .stream()
      .filter(question -> question.getText().equals(initialQuestion2.getText()))
      .forEach(question -> initialQuestion2 = question);

    objectMapper = new ObjectMapper();

    DecodedJWT jwtTest = new DecodedJWT() {
      @Override
      public String getToken() {
        return null;
      }

      @Override
      public String getHeader() {
        return null;
      }

      @Override
      public String getPayload() {
        return null;
      }

      @Override
      public String getSignature() {
        return "testUser";
      }

      @Override
      public String getAlgorithm() {
        return null;
      }

      @Override
      public String getType() {
        return null;
      }

      @Override
      public String getContentType() {
        return null;
      }

      @Override
      public String getKeyId() {
        return null;
      }

      @Override
      public Claim getHeaderClaim(String s) {
        return null;
      }

      @Override
      public String getIssuer() {
        return null;
      }

      @Override
      public String getSubject() {
        return null;
      }

      @Override
      public List<String> getAudience() {
        return null;
      }

      @Override
      public Date getExpiresAt() {
        return null;
      }

      @Override
      public Date getNotBefore() {
        return null;
      }

      @Override
      public Date getIssuedAt() {
        return null;
      }

      @Override
      public String getId() {
        return null;
      }

      @Override
      public Claim getClaim(String s) {
        return null;
      }

      @Override
      public Map<String, Claim> getClaims() {
        return null;
      }
    };
    when(jwtValidatorService.validate("testToken")).thenReturn(jwtTest);
  }

  @AfterEach
  void deleteBasicData() {
    gameResultRepository.deleteAll();
    configurationRepository.deleteAll();
  }

  @Test
  void saveGameResult() throws Exception {
    List<RoundResultDTO> correctList = new ArrayList<>();
    List<RoundResultDTO> wrongList = new ArrayList<>();
    correctList.add(new RoundResultDTO(initialQuestion1.getId(), initialQuestion1.getRightAnswer()));
    wrongList.add(
      new RoundResultDTO(initialQuestion2.getId(), initialQuestion2.getWrongAnswers().stream().findFirst().get())
    );
    GameResultDTO gameResultDTO = new GameResultDTO(2,1,correctList,
            wrongList,
            UUID.randomUUID());

    final String bodyValue = objectMapper.writeValueAsString(gameResultDTO);
    final MvcResult result = mvc
      .perform(post(API_URL).cookie(cookie).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andReturn();

    final GameResultDTO createdGameResultDTO = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      GameResultDTO.class
    );

    assertEquals(gameResultDTO, createdGameResultDTO);
  }
}
