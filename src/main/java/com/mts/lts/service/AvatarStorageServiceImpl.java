//package com.mts.lts.service;
//
//import com.mts.lts.domain.Image;
//import com.mts.lts.domain.Module;
//import com.mts.lts.domain.User;
//import com.mts.lts.dao.ImageRepository;
//import com.mts.lts.dao.ModuleRepository;
//import com.mts.lts.dao.UserRepository;
//import com.mts.lts.service.exceptions.CourseNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.Optional;
//import java.util.UUID;
//
//import static java.nio.file.StandardOpenOption.*;
//
//@Service
//public class AvatarStorageServiceImpl implements AvatarStorageService {
//
//    private final ImageRepository avatarImageRepository;
//
//    private final UserRepository userRepository;
//
//    private final ModuleRepository moduleRepository;
//
//    @Value("${avatar.file.storage.path}")
//    private String userAvatarPath;
//
//    @Value("${cover.file.storage.path}")
//    private String courseCoverPath;
//
//    @Autowired
//    public AvatarStorageServiceImpl(
//            ImageRepository avatarImageRepository,
//            UserRepository userRepository,
//            ModuleRepository moduleRepository
//    ) {
//        this.avatarImageRepository = avatarImageRepository;
//        this.userRepository = userRepository;
//        this.moduleRepository = moduleRepository;
//    }
//
//    @Override
//    public String getContentTypeByUser(String username) {
//        return getContentType(
//                avatarImageRepository.findByUserUsername(username),//ИСКАТЬ ПО ЮЗЕРУ
//                DEFAULT_USER_AVATAR_CONTENT_TYPE
//        );
//    }
//
//    @Override
//    public String getContentTypeByCourse(Long courseId) {
//        return getContentType(
//                avatarImageRepository.findByCourseId(courseId), // ИСКАТЬ ПО КУРСУ
//                DEFAULT_COURSE_COVER_CONTENT_TYPE
//        );
//    }
//
//    @Override
//    public String getContentType(
//            Optional<Image> avatarImage,
//            String defaultContentType
//    ) {
//        return avatarImage.map(Image::getContentType)
//                .orElse(defaultContentType);
//    }
//
//    @Override
//    public byte[] getAvatarImageByUser(String username) {
//        return getAvatarImage(
//                avatarImageRepository.findByUserUsername(username),
//                DEFAULT_USER_AVATAR_FILENAME,
//                userAvatarPath
//        );
//    }
//
//    @Override
//    public byte[] getAvatarImageByCourse(Long courseId) {
//        return getAvatarImage(
//                avatarImageRepository.findByCourseId(courseId),
//                DEFAULT_COURSE_COVER_FILENAME,
//                courseCoverPath
//        );
//    }
//
//    @Override
//    public byte[] getAvatarImage(
//            Optional<Image> avatarImage,
//            String defaultFilename,
//            String path
//    ) {
//        String filename = avatarImage.map(Image::getFilename)
//                .orElse(defaultFilename);
//
//        try {
//            return Files.readAllBytes(Path.of(path, filename));
//        } catch (IOException ex) {
//            throw new IllegalStateException(ex);
//        }
//    }
//
//    @Override
//    @Transactional
//    public void save(String username, String contentType, InputStream input) {
//        User user = userRepository.findUserByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException(username));
//        Image image = user.getImage();
//        if (image == null) {
//            image = new Image();
//            image.setUser(user);
//        }
//        save(image, contentType, input, userAvatarPath);
//    }
//
//    @Override
//    @Transactional
//    public void save(Long courseId, String contentType, InputStream input) {
//        Module module = moduleRepository.findById(courseId)
//                .orElseThrow(() -> new CourseNotFoundException(courseId));
//        Image image = module.getCoverImage();
//        if (image == null) {
//            image = new Image();
//            image.setCourse(module);
//        }
//        save(image, contentType, input, courseCoverPath);
//    }
//
//    @Override
//    public void save(
//            Image image,
//            String contentType,
//            InputStream input,
//            String path
//    ) {
//        String filename;
//        if (image.getFilename() != null) {
//            filename = image.getFilename();
//        } else {
//            filename = UUID.randomUUID().toString();
//            image.setFilename(filename);
//        }
//        image.setContentType(contentType);
//        avatarImageRepository.save(image);
//
//        try (
//                OutputStream output = Files.newOutputStream(
//                        Path.of(path, filename),
//                        CREATE,
//                        WRITE,
//                        TRUNCATE_EXISTING
//                )
//        ) {
//            input.transferTo(output);
//        } catch (Exception ex) {
//            throw new IllegalStateException(ex);
//        }
//    }
//
//    @Override
//    public void deleteAvatarImageByUser(String username) {
//        User user = userRepository.findUserByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException(username));
//        Image image = user.getImage();
//        if (image == null) {
//            return;
//        }
//        avatarImageRepository.delete(image);
//    }
//
//    @Override
//    public void deleteAvatarImageByCourse(Long courseId) {
//        Module module = moduleRepository.findById(courseId)
//                .orElseThrow(() -> new CourseNotFoundException(courseId));
//        Image coverImage = module.getCoverImage();
//        if (coverImage == null) {
//            return;
//        }
//        avatarImageRepository.delete(coverImage);
//    }
//
//    private static final String DEFAULT_USER_AVATAR_FILENAME = "default_avatar.png";
//    private static final String DEFAULT_COURSE_COVER_FILENAME = "default_cover.jpeg";
//    private static final String DEFAULT_COURSE_COVER_CONTENT_TYPE = "image/jpeg";
//    private static final String DEFAULT_USER_AVATAR_CONTENT_TYPE = "image/png";
//}
