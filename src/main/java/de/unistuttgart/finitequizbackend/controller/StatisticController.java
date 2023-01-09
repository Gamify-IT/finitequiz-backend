package de.unistuttgart.finitequizbackend.controller;

import de.unistuttgart.finitequizbackend.data.statistic.ProblematicQuestion;
import de.unistuttgart.finitequizbackend.data.statistic.TimeSpentDistribution;
import de.unistuttgart.finitequizbackend.service.StatisticService;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * This controller handles all game-configuration-related REST-APIs
 */
@Tag(name = "Statistic", description = "Get statistics of minigame configurations")
@RestController
@RequestMapping("/statistics")
@Import({ JWTValidatorService.class })
@Slf4j
@Validated
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private JWTValidatorService jwtValidatorService;

    @Operation(summary = "Get problematic questions of a configuration")
    @GetMapping("/{id}/problematic-questions")
    public List<ProblematicQuestion> getProblematicQuestionsStatisticsOfMinigame(
        @CookieValue("access_token") final String accessToken,
        @PathVariable final UUID id
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get problematic questions statistic of configuration {}", id);
        return statisticService.getProblematicQuestions(id);
    }

    @Operation(summary = "Get the time spent distribution of a configuration")
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
