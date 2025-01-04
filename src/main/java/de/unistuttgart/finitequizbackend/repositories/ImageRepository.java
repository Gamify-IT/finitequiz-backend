package de.unistuttgart.finitequizbackend.repositories;

import de.unistuttgart.finitequizbackend.data.Image;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {

    @Query("SELECT i FROM Image i WHERE i.imageUUID IN :imageUUIDs")
    List<Image> findByImageUUIDIn(@Param("imageUUIDs") List<String> imageUUIDs);
}