package com.mts.lts.service;

import com.mts.lts.domain.AvatarImage;
import com.mts.lts.domain.Course;
import com.mts.lts.domain.User;
import com.mts.lts.dao.ImageRepository;
import com.mts.lts.dao.CourseRepository;
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
public class AvatarStorageService {

    private final ImageRepository avatarImageRepository;

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    @Value("${avatar.file.storage.path}")
    private String userAvatarPath;

    @Value("${cover.file.storage.path}")
    private String courseCoverPath;

    @Autowired
    public AvatarStorageService(
            ImageRepository avatarImageRepository,
            UserRepository userRepository,
            CourseRepository courseRepository
    ) {
        this.avatarImageRepository = avatarImageRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public String getContentTypeByUser(String username) {
        return getContentType(
                avatarImageRepository.findByUserUsername(username),
                DEFAULT_USER_AVATAR_CONTENT_TYPE
        );
    }

    public String getContentTypeByCourse(Long courseId) {
        return getContentType(
                avatarImageRepository.findByCourseId(courseId),
                DEFAULT_COURSE_COVER_CONTENT_TYPE
        );
    }

    private String getContentType(
            Optional<AvatarImage> avatarImage,
            String defaultContentType
    ) {
        return avatarImage.map(AvatarImage::getContentType)
                .orElse(defaultContentType);
    }

    public byte[] getAvatarImageByUser(String username) {
        return getAvatarImage(
                avatarImageRepository.findByUserUsername(username),
                DEFAULT_USER_AVATAR_FILENAME,
                userAvatarPath
        );
    }

    public byte[] getAvatarImageByCourse(Long courseId) {
        return getAvatarImage(
                avatarImageRepository.findByCourseId(courseId),
                DEFAULT_COURSE_COVER_FILENAME,
                courseCoverPath
        );
    }

    private byte[] getAvatarImage(
            Optional<AvatarImage> avatarImage,
            String defaultFilename,
            String path
    ) {
        String filename = avatarImage.map(AvatarImage::getFilename)
                .orElse(defaultFilename);

        try {
            return Files.readAllBytes(Path.of(path, filename));
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Transactional
    public void save(String username, String contentType, InputStream input) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        AvatarImage avatarImage = user.getAvatarImage();
        if (avatarImage == null) {
            avatarImage = new AvatarImage();
            avatarImage.setUser(user);
        }
        save(avatarImage, contentType, input, userAvatarPath);
    }

    @Transactional
    public void save(Long courseId, String contentType, InputStream input) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        AvatarImage avatarImage = course.getCoverImage();
        if (avatarImage == null) {
            avatarImage = new AvatarImage();
            avatarImage.setCourse(course);
        }
        save(avatarImage, contentType, input, courseCoverPath);
    }

    private void save(
            AvatarImage avatarImage,
            String contentType,
            InputStream input,
            String path
    ) {
        String filename;
        if (avatarImage.getFilename() != null) {
            filename = avatarImage.getFilename();
        } else {
            filename = UUID.randomUUID().toString();
            avatarImage.setFilename(filename);
        }
        avatarImage.setContentType(contentType);
        avatarImageRepository.save(avatarImage);

        try (
                OutputStream output = Files.newOutputStream(
                        Path.of(path, filename),
                        CREATE,
                        WRITE,
                        TRUNCATE_EXISTING
                )
        ) {
            input.transferTo(output);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void deleteAvatarImageByUser(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        AvatarImage avatarImage = user.getAvatarImage();
        if (avatarImage == null) {
            return;
        }
        avatarImageRepository.delete(avatarImage);
    }

    public void deleteAvatarImageByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        AvatarImage coverImage = course.getCoverImage();
        if (coverImage == null) {
            return;
        }
        avatarImageRepository.delete(coverImage);
    }

    private static final String DEFAULT_USER_AVATAR_FILENAME = "default_avatar.png";
    private static final String DEFAULT_COURSE_COVER_FILENAME = "default_cover.jpeg";
    private static final String DEFAULT_COURSE_COVER_CONTENT_TYPE = "image/jpeg";
    private static final String DEFAULT_USER_AVATAR_CONTENT_TYPE = "image/png";
}
