package com.mts.lts.mapper;

import com.mts.lts.domain.News;
import com.mts.lts.domain.Tag;
import com.mts.lts.dto.NewsDto;
import com.mts.lts.service.NewsListerService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class NewsMapper extends AbstractMapper<NewsDto, News, NewsListerService> {
    private static DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("dd MMMM yyyy год")
            .withLocale(new Locale("ru"));
    private static Pattern pattern = Pattern.compile("(^|\\B)#(?![0-9_]+\\b)([a-zA-Z0-9_]{1,30})(\\b|\\r)");

    public NewsMapper(NewsListerService entityService) {
        super(entityService);
    }

    private Set<Tag> getTags(String text){
        Matcher matcher = pattern.matcher(text);
        List<String> words = new ArrayList<String>();
        while(matcher.find()){
            words.add(text.substring(matcher.start(), matcher.end()));
        }
        return words.stream().map(x -> new Tag( x)).collect(Collectors.toSet());
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
        news.setTags(getTags(entityDto.getText()));

        return news;
    }

    @Override
    public NewsDto domainToDto(News entity) {
        return new NewsDto(
                entity.getId(),
                entity.getTitle(),
                entity.getText(),
                dateTimeFormatter.format(entity.getUpdatedAt()),
                entity.getUpdatedBy(),
                entity.getTags()
        );
    }
}
