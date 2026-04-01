package com.froneus.dinosaur.mapper;

import com.froneus.dinosaur.dto.CreateDinosaurRequest;
import com.froneus.dinosaur.dto.DinosaurResponse;
import com.froneus.dinosaur.dto.UpdateDinosaurRequest;
import com.froneus.dinosaur.model.Dinosaur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DinosaurMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Dinosaur toEntity(CreateDinosaurRequest request);

    Dinosaur toEntity(String id, UpdateDinosaurRequest request);

    List<DinosaurResponse> toDinosaurResponseList(List<Dinosaur> dinosaurs);

    DinosaurResponse toDTO(Dinosaur dinosaur);
}
