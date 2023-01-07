package de.unistuttgart.finitequizbackend.controller;

import de.unistuttgart.finitequizbackend.data.ConfigurationDTO;
import de.unistuttgart.finitequizbackend.data.QuestionDTO;
import de.unistuttgart.finitequizbackend.data.mapper.ConfigurationMapper;
import de.unistuttgart.finitequizbackend.data.mapper.QuestionMapper;
import de.unistuttgart.finitequizbackend.data.statistic.ProblematicQuestion;
import de.unistuttgart.finitequizbackend.data.statistic.TimeSpentDistribution;
import de.unistuttgart.finitequizbackend.repositories.ConfigurationRepository;
import de.unistuttgart.finitequizbackend.service.ConfigService;
import de.unistuttgart.finitequizbackend.service.StatisticService;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * This controller handles all game-configuration-related REST-APIs
 */
@RestController
@RequestMapping("/statistics")
@Import({ JWTValidatorService.class })
@Slf4j
@Validated
public class StatisticController {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private JWTValidatorService jwtValidatorService;

    @GetMapping("/{id}/problematic-questions")
    public List<ProblematicQuestion> getProblematicQuestionsStatisticsOfMinigame(
        @CookieValue("access_token") final String accessToken,
        @PathVariable final UUID id
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get problematic questions statistic of configuration {}", id);
        return statisticService.getProblematicQuestions(id);
    }

    @GetMapping("/{id}/time-spent")
    public List<TimeSpentDistribution> getTimeSpentStatistcOfMinigame(
        @CookieValue("access_token") final String accessToken,
        @PathVariable final UUID id
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get time spent statistic of configuration {}", id);
        return statisticService.getTimeSpentDistributions(id);
    }
}
