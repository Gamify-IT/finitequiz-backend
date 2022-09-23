package de.unistuttgart.finitequizbackend.service;

import de.unistuttgart.finitequizbackend.data.Configuration;
import de.unistuttgart.finitequizbackend.data.ConfigurationDTO;
import de.unistuttgart.finitequizbackend.data.Question;
import de.unistuttgart.finitequizbackend.data.QuestionDTO;
import de.unistuttgart.finitequizbackend.data.mapper.ConfigurationMapper;
import de.unistuttgart.finitequizbackend.data.mapper.QuestionMapper;
import de.unistuttgart.finitequizbackend.repositories.ConfigurationRepository;
import de.unistuttgart.finitequizbackend.repositories.QuestionRepository;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * This service handles the logic for the ConfigController.class
 */
@Service
@Slf4j
@Transactional
public class ConfigService {

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    ConfigurationMapper configurationMapper;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    QuestionRepository questionRepository;

    /**
     * Search a configuration by given id
     *
     * @param id the id of the configuration searching for
     * @return the found configuration
     * @throws ResponseStatusException  when configuration by configurationName could not be found
     * @throws IllegalArgumentException if at least one of the arguments is null
     */
    public Configuration getConfiguration(final UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        return configurationRepository
            .findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("There is no configuration with id %s.", id)
                )
            );
    }

    /**
     * Save a configuration
     *
     * @param configurationDTO configuration that should be saved
     * @return the saved configuration as DTO
     * @throws IllegalArgumentException if at least one of the arguments is null
     */
    public ConfigurationDTO saveConfiguration(final @Valid ConfigurationDTO configurationDTO) {
        if (configurationDTO == null) {
            throw new IllegalArgumentException("configurationDTO is null");
        }
        final Configuration savedConfiguration = configurationRepository.save(
            configurationMapper.configurationDTOToConfiguration(configurationDTO)
        );
        return configurationMapper.configurationToConfigurationDTO(savedConfiguration);
    }

    /**
     * Update a configuration
     *
     * @param id               the id of the configuration that should be updated
     * @param configurationDTO configuration that should be updated
     * @return the updated configuration as DTO
     * @throws ResponseStatusException  when configuration with the id does not exist
     * @throws IllegalArgumentException if at least one of the arguments is null
     */
    public ConfigurationDTO updateConfiguration(final UUID id, @Valid final ConfigurationDTO configurationDTO) {
        if (id == null || configurationDTO == null) {
            throw new IllegalArgumentException("id or configurationDTO is null");
        }
        final Configuration configuration = getConfiguration(id);
        configuration.setQuestions(questionMapper.questionDTOsToQuestions(configurationDTO.getQuestions()));
        final Configuration updatedConfiguration = configurationRepository.save(configuration);
        return configurationMapper.configurationToConfigurationDTO(updatedConfiguration);
    }

    /**
     * Delete a configuration
     *
     * @param id the id of the configuration that should be updated
     * @return the deleted configuration as DTO
     * @throws ResponseStatusException  when configuration with the id does not exist
     * @throws IllegalArgumentException if at least one of the arguments is null
     */
    public ConfigurationDTO deleteConfiguration(final UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        final Configuration configuration = getConfiguration(id);
        configurationRepository.delete(configuration);
        return configurationMapper.configurationToConfigurationDTO(configuration);
    }

    /**
     * Add a question to specific configuration
     *
     * @param id          the id of the configuration where a question should be added
     * @param questionDTO the question that should be added
     * @return the added question as DTO
     * @throws ResponseStatusException  when configuration with the id does not exist
     * @throws IllegalArgumentException if at least one of the arguments is null
     */
    public QuestionDTO addQuestionToConfiguration(final UUID id, final @Valid QuestionDTO questionDTO) {
        if (id == null || questionDTO == null) {
            throw new IllegalArgumentException("id or questionDTO is null");
        }
        final Configuration configuration = getConfiguration(id);
        final Question question = questionRepository.save(questionMapper.questionDTOToQuestion(questionDTO));
        configuration.addQuestion(question);
        configurationRepository.save(configuration);
        return questionMapper.questionToQuestionDTO(question);
    }

    /**
     * Delete a question from a specific configuration
     *
     * @param id         the id of the configuration where a question should be removed
     * @param questionId the id of the question that should be deleted
     * @return the deleted question as DTO
     * @throws ResponseStatusException  when configuration with the id or question with id does not exist
     * @throws IllegalArgumentException if at least one of the arguments is null
     */
    public QuestionDTO removeQuestionFromConfiguration(final UUID id, final UUID questionId) {
        if (id == null || questionId == null) {
            throw new IllegalArgumentException("id or questionId is null");
        }
        final Configuration configuration = getConfiguration(id);
        final Question question = getQuestionInConfiguration(questionId, configuration)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Question with ID %s does not exist in configuration %s.", questionId, configuration)
                )
            );
        configuration.removeQuestion(question);
        configurationRepository.save(configuration);
        questionRepository.delete(question);
        return questionMapper.questionToQuestionDTO(question);
    }

    /**
     * Update a question from a specific configuration
     *
     * @param id          the id of the configuration where a question should be updated
     * @param questionId  the id of the question that should be updated
     * @param questionDTO the content of the question that should be updated
     * @return the updated question as DTO
     * @throws ResponseStatusException  when configuration with the id or question with id does not exist
     * @throws IllegalArgumentException if at least one of the arguments is null
     */
    public QuestionDTO updateQuestionFromConfiguration(
        final UUID id,
        final UUID questionId,
        final @Valid QuestionDTO questionDTO
    ) {
        if (id == null || questionId == null || questionDTO == null) {
            throw new IllegalArgumentException("id or questionId or questionDTO is null");
        }
        final Configuration configuration = getConfiguration(id);
        if (getQuestionInConfiguration(questionId, configuration).isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                String.format("Question with ID %s does not exist in configuration %s.", questionId, configuration)
            );
        }
        final Question question = questionMapper.questionDTOToQuestion(questionDTO);
        question.setId(questionId);
        final Question savedQuestion = questionRepository.save(question);
        return questionMapper.questionToQuestionDTO(savedQuestion);
    }

    /**
     * @param questionId    id of searched question
     * @param configuration configuration in which the question is part of
     * @return an optional of the question
     * @throws ResponseStatusException  when question with the id in the given configuration does not exist
     * @throws IllegalArgumentException if at least one of the arguments is null
     */
    private Optional<Question> getQuestionInConfiguration(
        final UUID questionId,
        final @Valid Configuration configuration
    ) {
        if (questionId == null || configuration == null) {
            throw new IllegalArgumentException("questionId or configuration is null");
        }
        return configuration
            .getQuestions()
            .parallelStream()
            .filter(filteredQuestion -> filteredQuestion.getId().equals(questionId))
            .findAny();
    }
}
