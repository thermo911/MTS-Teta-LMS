package com.mts.lts.repository;

import com.mts.lts.domain.AvatarImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarImageRepository extends JpaRepository<AvatarImage, Long> {

    public Optional<AvatarImage> findByUserUsername(String username);

    public Optional<AvatarImage> findByCourseId(Long courseId);
}
