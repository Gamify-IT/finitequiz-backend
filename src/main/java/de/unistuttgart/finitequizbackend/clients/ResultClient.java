package de.unistuttgart.finitequizbackend.clients;

import de.unistuttgart.finitequizbackend.data.OverworldResultDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * This client submits OverworldResultDTOs to the overworld backend
 */
@FeignClient(value = "resultClient", url = "${overworld.url}/internal")
public interface ResultClient {
    /**
     * Submits the resultDTO to the Overworld-Backend
     *
     * @param resultDTO the player submitted result, trimmed down to the data needed for the overworld
     */
    @PostMapping("/submit-game-pass")
    @Headers("Content-Type: application/json")
    void submit(OverworldResultDTO resultDTO);
}
