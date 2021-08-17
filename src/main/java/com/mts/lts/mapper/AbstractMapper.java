package com.mts.lts.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractMapper<DTO, DOMAIN, SERVICE> {

    protected final SERVICE entityService;

    public AbstractMapper(SERVICE entityService) {
        this.entityService = entityService;
    }

    public abstract DOMAIN dtoToDomain(DTO entityDto);

    public abstract DTO domainToDto(DOMAIN entity);

    public List<DTO> domainToDto(List<DOMAIN> entities) {
        return entities.stream()
                .map(this::domainToDto)
                .collect(Collectors.toList());
    }

    public List<DOMAIN> dtoToDomain(List<DTO> dtos) {
        return dtos.stream()
                .map(this::dtoToDomain)
                .collect(Collectors.toList());
    }

    public Set<DTO> domainToDto(Set<DOMAIN> entities) {
        return entities.stream()
                .map(this::domainToDto)
                .collect(Collectors.toSet());
    }

    public Set<DOMAIN> dtoToDomain(Set<DTO> dtos) {
        return dtos.stream()
                .map(this::dtoToDomain)
                .collect(Collectors.toSet());
    }
}
