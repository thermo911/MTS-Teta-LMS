package com.mts.lts.mapper;

import com.mts.lts.domain.News;
import com.mts.lts.dto.NewsDto;
import com.mts.lts.service.NewsListerService;
import org.springframework.stereotype.Component;

@Component
public class NewsMapper extends AbstractMapper<NewsDto, News, NewsListerService> {

    public NewsMapper(NewsListerService entityService) {
        super(entityService);
    }

    @Override
    public News dtoToDomain(NewsDto entityDto) {
        News news;
        Long newsDtoId = entityDto.getId();
        if (newsDtoId != null) {
            news = entityService.getOne(newsDtoId);
        } else {
            news = new News();
        }
        news.setTitle(entityDto.getTitle());
        news.setText(entityDto.getText());
        news.setUpdatedAt(null); //todo: текущее время
        news.setTags(entityDto.getTags());

        return news;
    }

    @Override
    public NewsDto domainToDto(News entity) {
        return new NewsDto(
                entity.getId(),
                entity.getTitle(),
                entity.getText(),
                entity.getUpdatedAt(),
                entity.getTags()
        );
    }
}
