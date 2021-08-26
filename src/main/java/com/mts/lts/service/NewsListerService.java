package com.mts.lts.service;

import com.mts.lts.dao.NewsRepository;
import com.mts.lts.dao.UserRepository;
import com.mts.lts.domain.News;
import com.mts.lts.service.exceptions.NewsNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class NewsListerService {
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Autowired
    public NewsListerService(NewsRepository newsRepository, UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
    }

    public List<News> findAll() {
        return newsRepository.findAll();
    }

    public News findById(long newsId) {
        return newsRepository.findById(newsId).orElseThrow(() -> new NewsNotFoundException(newsId));
    }

    public News getOne(Long id) {
        return newsRepository.getOne(id);
    }

    public void save(News news, Authentication authentication)
    {
        if(news.getId() == null){
            news.setCreatedBy(userRepository.findUserByEmail(authentication.getName()).orElseThrow(NoSuchElementException::new));
            news.setCreatedAt(LocalDateTime.now());
        }

        news.setUpdatedBy(userRepository.findUserByEmail(authentication.getName()).orElseThrow(NoSuchElementException::new));
        news.setUpdatedAt(LocalDateTime.now());
        newsRepository.save(news);
    }



}
