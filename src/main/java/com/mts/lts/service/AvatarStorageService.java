package com.mts.lts.service;

import com.mts.lts.domain.Image;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.Optional;

public interface AvatarStorageService {
    String getContentTypeByUser(String username);

    String getContentTypeByCourse(Long courseId);

    String getContentType(
            Optional<Image> avatarImage,
            String defaultContentType
    );

    byte[] getAvatarImageByUser(String username);

    byte[] getAvatarImageByCourse(Long courseId);

    byte[] getAvatarImage(
            Optional<Image> avatarImage,
            String defaultFilename,
            String path
    );

    @Transactional
    void save(String username, String contentType, InputStream input);

    @Transactional
    void save(Long courseId, String contentType, InputStream input);

    void save(
            Image image,
            String contentType,
            InputStream input,
            String path
    );

    void deleteAvatarImageByUser(String username);

    void deleteAvatarImageByCourse(Long courseId);
}
