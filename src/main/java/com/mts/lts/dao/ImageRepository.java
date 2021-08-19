package com.mts.lts.dao;

import com.mts.lts.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    @Override
    Optional<Image> findById(Long imageId);
}
