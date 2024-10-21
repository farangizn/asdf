package org.example.mappers;

import org.example.dto.UserInputDTO;
import org.example.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.CDI)
public interface UserInputMapper {
    User toEntity(UserInputDTO userInputDTO);

    UserInputDTO toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserInputDTO userInputDTO, @MappingTarget User user);
}