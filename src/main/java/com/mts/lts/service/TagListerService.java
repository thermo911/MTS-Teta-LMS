package com.mts.lts.service;

import com.mts.lts.dao.TagRepository;
import com.mts.lts.domain.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagListerService {
    private final TagRepository tagRepository;

    public TagListerService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag findByNameTag(String tag){
        return tagRepository.findByName(tag);
    }
}
