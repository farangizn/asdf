package org.example.mappers;

import org.example.dto.PostInputPTO;
import org.example.entity.Post;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.CDI)
public interface PostInputMapper {
    Post toEntity(PostInputPTO postInputPTO);

    PostInputPTO toDto(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(PostInputPTO postInputPTO, @MappingTarget Post post);
}