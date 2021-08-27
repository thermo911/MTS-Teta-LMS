package com.mts.lts.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractMapperTest {

    private static class Value {

        private final int value;

        public Value(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Value value1 = (Value) o;
            return value == value1.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    private static class TestEntity extends Value {

        public TestEntity(int value) {
            super(value);
        }
    }

    private static class TestEntityDto extends Value {

        public TestEntityDto(int value) {
            super(value);
        }
    }

    private static class TestMapper extends AbstractMapper<TestEntityDto, TestEntity, Object> {

        public TestMapper(Object entityService) {
            super(entityService);
        }

        @Override
        public TestEntity dtoToDomain(TestEntityDto entityDto) {
            return new TestEntity(entityDto.getValue() + 1);
        }

        @Override
        public TestEntityDto domainToDto(TestEntity entity) {
            return new TestEntityDto(entity.getValue() - 1);
        }
    }

    private TestMapper testMapper;
    private List<TestEntity> entities;
    private List<TestEntityDto> entityDtos;

    @BeforeEach
    public void setUp() {
        testMapper = new TestMapper(null);
        entities = new LinkedList<>();
        entities.add(new TestEntity(1));
        entities.add(new TestEntity(2));
        entities.add(new TestEntity(3));
        entityDtos = entities.stream().map(testMapper::domainToDto).collect(Collectors.toList());
    }

    @Test
    public void domainToDtoListTest() {
        assertEquals(testMapper.domainToDto(entities), entityDtos);
    }

    @Test
    public void dtoToDomainListTest() {
        assertEquals(testMapper.dtoToDomain(entityDtos), entities);
    }

    @Test
    public void domainToDtoSetTest() {
        assertEquals(testMapper.domainToDto(new HashSet<>(entities)), new HashSet<>(entityDtos));
    }

    @Test
    public void dtoToDomainSetTest() {
        assertEquals(testMapper.dtoToDomain(new HashSet<>(entityDtos)), new HashSet<>(entities));
    }
}
