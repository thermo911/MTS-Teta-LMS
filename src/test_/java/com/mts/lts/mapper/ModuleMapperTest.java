package com.mts.lts.mapper;

import com.mts.lts.domain.Module;
import com.mts.lts.dto.ModuleDto;
import com.mts.lts.service.ModuleListerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import static com.mts.lts.TestUtils.assertEqualsCourse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Component
public class ModuleMapperTest {

    private ModuleListerService moduleListerServiceMock;
    private ModuleMapper courseMapper;
    private static Module module;
    private static ModuleDto moduleDto;

    private static final long courseId = 2;
    private static final String testAuthor = "author";
    private static final String testTitle = "title";

    @BeforeAll
    public static void setUp() {
        module = new Module();
        moduleDto = new ModuleDto(courseId, testAuthor, testTitle);
        module.setId(courseId);
        module.setAuthor(testAuthor);
        module.setTitle(testTitle);
    }

    @BeforeEach
    public void setUpForEach() {
        moduleListerServiceMock = Mockito.mock(ModuleListerService.class);
        Mockito.when(moduleListerServiceMock.getOne(courseId)).thenReturn(module);
        courseMapper = new ModuleMapper(moduleListerServiceMock);
    }

    @Test
    @Transactional
    public void dtoToDomainExistsTest() {
        Module convertedModule = courseMapper.dtoToDomain(moduleDto);
        assertEqualsCourse(convertedModule, module);
    }

    @Test
    public void dtoToDomainNewTest() {
        ModuleDto newModuleDto = new ModuleDto(null, testAuthor, testTitle);
        Module convertedModule = courseMapper.dtoToDomain(newModuleDto);
        assertEquals(convertedModule.getAuthor(), testAuthor);
        assertEquals(convertedModule.getTitle(), testTitle);
        assertNotEquals(convertedModule.getId(), module.getId());
    }

    @Test
    public void domainToDtoTest() {
        ModuleDto convertedModuleDto = courseMapper.domainToDto(module);
        assertEquals(convertedModuleDto, moduleDto);
    }
}
