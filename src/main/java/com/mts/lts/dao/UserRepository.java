package com.mts.lts.dao;

import com.mts.lts.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("from User user join user.courses c where c.id = :courseId")
    Set<User> findByCourseId(@Param("courseId") Long courseId);


    @Query("from User u " +
            "where u.id not in ( " +
            "select u.id " +
            "from User u " +
            "left join u.courses c " +
            "where c.id = :courseId)")
    List<User> findNotAssignedToCourse(@Param("courseId") Long courseId);

    Optional<User> findUserByUsername(String username);

    Boolean existsByUsername(String username);
}
