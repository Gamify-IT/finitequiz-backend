package de.unistuttgart.finitequizbackend.controller;

import de.unistuttgart.finitequizbackend.data.GameResultDTO;
import de.unistuttgart.finitequizbackend.service.GameResultService;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * This controller handles the game-result-related REST-APIs
 */
@RestController
@RequestMapping("/results")
@Import({ JWTValidatorService.class })
@Slf4j
@Validated
public class GameResultController {

    @Autowired
    GameResultService gameResultService;

    @Autowired
    private JWTValidatorService jwtValidatorService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public GameResultDTO saveGameResult(
        @CookieValue("access_token") final String accessToken,
        @Valid @RequestBody final GameResultDTO gameResultDTO
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        final String userId = jwtValidatorService.extractUserId(accessToken);
        log.debug("save game result for userId {}: {}", userId, gameResultDTO);
        gameResultService.saveGameResult(gameResultDTO, userId);
        return gameResultDTO;
    }
}
