package org.example.mappers;

import org.example.dto.PostDTO;
import org.example.entity.Post;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.CDI)
public interface PostMapper {
    Post toEntity(PostDTO postDTO);

    PostDTO toDto(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(PostDTO postDTO, @MappingTarget Post post);
}