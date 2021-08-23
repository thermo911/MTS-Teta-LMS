package com.mts.lts.dao;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.Module;
import com.mts.lts.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // TODO: check
//    @Query("SELECT ROUND(AVG(r.value), 2)" +
//            "FROM Rating r" +
//            "WHERE r.course.id = :course.id")
//    Double getRatingForCourse(@Param("course") Course course);
}
