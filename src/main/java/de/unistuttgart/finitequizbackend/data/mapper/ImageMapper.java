package de.unistuttgart.finitequizbackend.data.mapper;

import de.unistuttgart.finitequizbackend.data.Image;
import de.unistuttgart.finitequizbackend.data.ImageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageDTO imageToImageDTO(final Image image);

    Image imageDTOToImage(final ImageDTO imageDTO);
}