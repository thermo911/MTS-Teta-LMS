package com.mts.lts.repository;

import com.mts.lts.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AvatarImageRepository extends JpaRepository<Image, Long> {
}
