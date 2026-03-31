package com.froneus.dinosaur.mapper;

import com.froneus.dinosaur.dto.CreateDinosaurRequest;
import com.froneus.dinosaur.dto.UpdateDinosaurRequest;
import com.froneus.dinosaur.model.Dinosaur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DinosaurMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Dinosaur toEntity(CreateDinosaurRequest request);

    @Mapping(target = "id", ignore = true)
    void toEntity(UpdateDinosaurRequest request, @MappingTarget Dinosaur dinosaur);
}
