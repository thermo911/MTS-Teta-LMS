package com.mts.lts.dao;

import com.mts.lts.domain.Topic;
import com.mts.lts.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByModuleId(Long moduleId);


    //сюда префикс!
    @Query("select new com.mts.lts.domain.Topic(topic.id, topic.title, topic.module)"
            + " from Topic topic join topic.module module"
            + " where module.id = :moduleId")
    List<Topic> findByModuleIdWithoutText(@Param("moduleId") Long moduleId);
}
