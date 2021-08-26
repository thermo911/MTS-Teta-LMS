package com.mts.lts.service;

import com.mts.lts.domain.Module;
import com.mts.lts.dao.ModuleRepository;
import com.mts.lts.service.exceptions.CourseNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import static com.mts.lts.TestUtils.assertEqualsCourse;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestDatabase
public class ModuleListerServiceTest {

    @Autowired
    private ModuleListerService moduleListerService;
    @Autowired
    private ModuleRepository moduleRepository;

    private Module testModule1;
    private Module testModule2;
    private static final String testAuthor1 = "author1";
    private static final String testAuthor2 = "author2";
    private static final String testTitle1 = "title1";
    private static final String testTitle2 = "title2";

    @BeforeEach
    void setUpForEach() {
        testModule1 = new Module(testAuthor1, testTitle1, Collections.emptyList(),
                Collections.emptySet());
        testModule2 = new Module(testAuthor2, testTitle2, Collections.emptyList(),
                Collections.emptySet());
        moduleRepository.deleteAll();
    }

    @Test
    public void findAllTest() {
        moduleRepository.save(testModule1);
        moduleRepository.save(testModule2);
        List<Module> res = moduleListerService.findAll();
        assertEquals(res.size(), 2);
    }

    @Test
    @Transactional
    public void getOneSuccessTest() {
        Module savedModule = moduleRepository.save(testModule1);
        moduleRepository.save(testModule2);
        Module res = moduleListerService.getOne(savedModule.getId());
        assertEqualsCourse(res, testModule1);
    }

    @Test
    @Transactional
    public void findByIdSuccessTest() {
        Module savedModule = moduleRepository.save(testModule1);
        moduleRepository.save(testModule2);
        Module res = moduleListerService.findById(savedModule.getId());
        assertEqualsCourse(res, testModule1);
    }

    @Test
    public void findByIdFailTest() {
        assertThrows(
                CourseNotFoundException.class,
                () -> {
                    Module savedModule = moduleRepository.save(testModule2);
                    moduleListerService.findById(savedModule.getId() + 1);
                }
        );
    }

    @Test
    @Transactional
    public void saveNewTest() {
        assertTrue(moduleRepository.findAll().isEmpty());
        moduleListerService.save(testModule2);
        assertEquals(moduleRepository.findAll().size(), 1);
        assertEqualsCourse(moduleRepository.findAll().get(0), testModule2);
    }

    @Test
    @Transactional
    public void saveUpdateTest() {
        Module savedModule = moduleRepository.save(testModule2);
        assertTrue(moduleRepository.findById(savedModule.getId()).isPresent());
        testModule2.setTitle(testTitle1);
        moduleListerService.save(testModule2);
        assertTrue(moduleRepository.findById(savedModule.getId()).isPresent());
        assertEqualsCourse(moduleRepository.findById(savedModule.getId()).get(), testModule2);
    }


    @Test
    public void deleteByIdExistsTest() {
        Module savedModule = moduleRepository.save(testModule2);
        assertTrue(moduleRepository.findById(savedModule.getId()).isPresent());
        moduleListerService.deleteById(savedModule.getId());
        assertFalse(moduleRepository.findById(savedModule.getId()).isPresent());
    }

    @Test
    public void findByTitleWithPrefixTest() {
        moduleRepository.save(testModule1);
        moduleRepository.save(testModule2);
        List<Module> res1 = moduleListerService
                .findByTitleWithPrefix(testTitle1.substring(0, testTitle1.length() - 1));
        assertEquals(res1.size(), 2);
        List<Module> res2 = moduleListerService.findByTitleWithPrefix(testTitle1);
        assertEquals(res2.size(), 1);
    }
}