package com.mts.lts.dao;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.Topic;
import com.mts.lts.domain.Topic;
import com.mts.lts.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByModuleId(Long moduleId);


    //сюда префикс!
    @Query("select new com.mts.lts.domain.Topic(topic.id, topic.title, topic.module)"
            + " from Topic topic join topic.module module"
            + " where module.id = :moduleId")
    List<Topic> findByModuleIdWithoutText(@Param("moduleId") Long moduleId);

    @Query("select count(*) from Topic t" +
            " join t.module m" +
            " join m.course c" +
            " where c = :course" +
            " and :user in (:usersWhoCompleted)")
    Integer countCompletedByUser(@Param("user") User user,
                                 @Param("usersWhoCompleted") Set<User> usersWhoCompleted,
                                 @Param("course") Course course);

    @Query("select count(*) from Topic t" +
            " join t.module m" +
            " join m.course c" +
            " where c = :course")
    Integer countTotalTopicsForCourse(@Param("course") Course course);
}
