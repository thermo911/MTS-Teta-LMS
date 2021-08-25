package com.mts.lts.service;

import com.mts.lts.dao.NewsRepository;
import com.mts.lts.domain.News;
import com.mts.lts.service.exceptions.NewsNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsListerService {
    private final NewsRepository newsRepository;

    @Autowired
    public NewsListerService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
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

    public void save(News news) {
        newsRepository.save(news);
    }

}
