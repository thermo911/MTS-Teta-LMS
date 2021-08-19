package com.mts.lts.dao;

import com.mts.lts.domain.Module;
import com.mts.lts.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByModule(Module module);
}
