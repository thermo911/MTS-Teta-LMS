package com.mts.lts.service;

import com.mts.lts.domain.Image;
import com.mts.lts.domain.Module;
import com.mts.lts.domain.User;
import com.mts.lts.dao.ImageRepository;
import com.mts.lts.dao.ModuleRepository;
import com.mts.lts.dao.UserRepository;
import com.mts.lts.service.exceptions.CourseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.*;

@Service
public class AvatarStorageServiceMock implements AvatarStorageService {

    private final ImageRepository avatarImageRepository;

    private final UserRepository userRepository;

    private final ModuleRepository moduleRepository;

    @Value("${avatar.file.storage.path}")
    private String userAvatarPath;

    @Value("${cover.file.storage.path}")
    private String courseCoverPath;

    @Autowired
    public AvatarStorageServiceMock(
            ImageRepository avatarImageRepository,
            UserRepository userRepository,
            ModuleRepository moduleRepository
    ) {
        this.avatarImageRepository = avatarImageRepository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
    }

    @Override
    public String getContentTypeByUser(String username) {
        return null;
    }

    @Override
    public String getContentTypeByCourse(Long courseId) {
        return null;
    }

    @Override
    public String getContentType(
            Optional<Image> avatarImage,
            String defaultContentType
    ) {
        return null;
    }

    @Override
    public byte[] getAvatarImageByUser(String username) {
        return null;
    }

    @Override
    public byte[] getAvatarImageByCourse(Long courseId) {
        return null;
    }

    @Override
    public byte[] getAvatarImage(
            Optional<Image> avatarImage,
            String defaultFilename,
            String path
    ) {
        return null;
    }

    @Override
    @Transactional
    public void save(String username, String contentType, InputStream input) {

    }

    @Override
    @Transactional
    public void save(Long courseId, String contentType, InputStream input) {
    }

    @Override
    public void save(
            Image image,
            String contentType,
            InputStream input,
            String path
    ) {
    }

    @Override
    public void deleteAvatarImageByUser(String username) {
    }

    @Override
    public void deleteAvatarImageByCourse(Long courseId) {
    }

    private static final String DEFAULT_USER_AVATAR_FILENAME = "default_avatar.png";
    private static final String DEFAULT_COURSE_COVER_FILENAME = "default_cover.jpeg";
    private static final String DEFAULT_COURSE_COVER_CONTENT_TYPE = "image/jpeg";
    private static final String DEFAULT_USER_AVATAR_CONTENT_TYPE = "image/png";
}
