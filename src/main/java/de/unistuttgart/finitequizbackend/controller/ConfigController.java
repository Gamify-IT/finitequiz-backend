package de.unistuttgart.finitequizbackend.controller;

import de.unistuttgart.finitequizbackend.data.ConfigurationDTO;
import de.unistuttgart.finitequizbackend.data.ImageDTO;
import de.unistuttgart.finitequizbackend.data.QuestionDTO;
import de.unistuttgart.finitequizbackend.data.mapper.ConfigurationMapper;
import de.unistuttgart.finitequizbackend.data.mapper.QuestionMapper;
import de.unistuttgart.finitequizbackend.repositories.ConfigurationRepository;
import de.unistuttgart.finitequizbackend.service.ConfigService;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * This controller handles all game-configuration-related REST-APIs
 */
@RestController
@RequestMapping("/configurations")
@Import({ JWTValidatorService.class })
@Slf4j
@Validated
public class ConfigController {

    public static final List<String> LECTURER = List.of("lecturer");
    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    ConfigService configService;

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    ConfigurationMapper configurationMapper;

    @Autowired
    private JWTValidatorService jwtValidatorService;

    @GetMapping("")
    public List<ConfigurationDTO> getConfigurations(@CookieValue("access_token") final String accessToken) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get all configurations");
        return configurationMapper.configurationsToConfigurationDTOs(configurationRepository.findAll());
    }

    @GetMapping("/{id}")
    public ConfigurationDTO getConfiguration(
        @CookieValue("access_token") final String accessToken,
        @PathVariable final UUID id
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get configuration {}", id);
        return configurationMapper.configurationToConfigurationDTO(configService.getConfiguration(id));
    }

    @GetMapping("/{id}/volume")
    public ConfigurationDTO getAllConfiguration(
            @CookieValue("access_token") final String accessToken,
            @PathVariable final UUID id
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get configuration {}", id);
        return configurationMapper.configurationToConfigurationDTO(
                configService.getAllConfigurations(id, accessToken)
        );
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ConfigurationDTO createConfiguration(
        @CookieValue("access_token") final String accessToken,
        @Valid @RequestBody final ConfigurationDTO configurationDTO
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER);
        log.debug("create configuration {}", configurationDTO);
        return configService.saveConfiguration(configurationDTO);
    }

    @PutMapping("/{id}")
    public ConfigurationDTO updateConfiguration(
        @CookieValue("access_token") final String accessToken,
        @PathVariable final UUID id,
        @Valid @RequestBody final ConfigurationDTO configurationDTO
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER);
        log.debug("update configuration {} with {}", id, configurationDTO);
        return configService.updateConfiguration(id, configurationDTO);
    }

    @DeleteMapping("/{id}")
    public ConfigurationDTO deleteConfiguration(
        @CookieValue("access_token") final String accessToken,
        @PathVariable final UUID id
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER);
        log.debug("delete configuration {}", id);
        return configService.deleteConfiguration(id);
    }

    @PostMapping("/{id}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionDTO addQuestionToConfiguration(
        @CookieValue("access_token") final String accessToken,
        @PathVariable final UUID id,
        @Valid @RequestBody final QuestionDTO questionDTO
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER);
        log.debug("add question {} to configuration {}", questionDTO, id);
        return configService.addQuestionToConfiguration(id, questionDTO);
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    public QuestionDTO removeQuestionFromConfiguration(
        @CookieValue("access_token") final String accessToken,
        @PathVariable final UUID id,
        @PathVariable final UUID questionId
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER);
        log.debug("remove question {} from configuration {}", questionId, id);
        return configService.removeQuestionFromConfiguration(id, questionId);
    }

    @PutMapping("/{id}/questions/{questionId}")
    public QuestionDTO updateQuestionFromConfiguration(
        @CookieValue("access_token") final String accessToken,
        @PathVariable final UUID id,
        @PathVariable final UUID questionId,
        @Valid @RequestBody final QuestionDTO questionDTO
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER);
        log.debug("update question {} with {} for configuration {}", questionId, questionDTO, id);
        return configService.updateQuestionFromConfiguration(id, questionId, questionDTO);
    }

    @GetMapping("/{id}/questions")
    public Set<QuestionDTO> getQuestions(
        @CookieValue("access_token") final String accessToken,
        @PathVariable final UUID id
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get configuration {}", id);
        return configurationMapper.configurationToConfigurationDTO(configService.getConfiguration(id)).getQuestions();
    }

    @PostMapping("/{id}/clone")
    @ResponseStatus(HttpStatus.CREATED)
    public UUID cloneConfiguration(@CookieValue("access_token") final String accessToken, @PathVariable final UUID id) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        jwtValidatorService.hasRolesOrThrow(accessToken, LECTURER);
        return configService.cloneConfiguration(id);
    }

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.CREATED)
    public ImageDTO addImage(
            @CookieValue("access_token") final String accessToken,
            @RequestParam("uuid") UUID uuid,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "description", required = false) String description 
    ) throws IOException {

        jwtValidatorService.validateTokenOrThrow(accessToken);

        byte[] imageBytes = image.getBytes();
        if (imageBytes.length == 0) {
            throw new IllegalArgumentException("Die hochgeladene Datei ist leer.");
        }

        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setImageUUID(uuid);
        imageDTO.setImage(imageBytes);

        if (description != null) {
            imageDTO.setDescription(description);
        }

        log.debug("Image UUID: {}", imageDTO.getImageUUID());
        log.debug("Description: {}", imageDTO.getDescription());

        return configService.addImage(imageDTO);
    }


    @GetMapping("/{uuid}/images")
    public List<ImageDTO> getImagesByConfigId(@PathVariable("uuid") String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        return configService.getImagesByConfigUUID(uuid);
    }


}
