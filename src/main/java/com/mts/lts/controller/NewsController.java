package com.mts.lts.controller;

import com.mts.lts.dao.TagRepository;
import com.mts.lts.domain.News;
import com.mts.lts.dto.NewsDto;
import com.mts.lts.mapper.NewsMapper;
import com.mts.lts.service.NewsListerService;
import com.mts.lts.service.TagListerService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/news")
public class NewsController {
    private final NewsMapper newsMapper;
    private final NewsListerService newsListerService;
    private final TagListerService tagListerService;

    public NewsController(NewsMapper newsMapper, NewsListerService newsListerService, TagListerService tagListerService) {
        this.newsMapper = newsMapper;
        this.newsListerService = newsListerService;
        this.tagListerService = tagListerService;
    }

    @GetMapping
    @Transactional
    public String newsTable(
            Model model,
            @RequestParam(value = "tag", required = false) String tag
    ) {
        List<News> news_list = tag == null ? newsListerService.findAll() : List.copyOf(tagListerService.findByNameTag(tag).getNews());
        model.addAttribute(
                "news_list",
                newsMapper.domainToDto(news_list)
        );

        return "news";
    }

    @GetMapping("/{id}")
    public String newsForm(Model model, @PathVariable("id") Long id) {
        News news = newsListerService.findById(id);
        model.addAttribute("news", newsMapper.domainToDto(news));
        return "edit_news";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/new")
    public String newsForm(Model model) {
        model.addAttribute("news", new NewsDto());
        return "edit_news";
    }


    @Secured("ROLE_ADMIN")
    @Transactional
    @PostMapping
    public String submitNewsForm(@Valid NewsDto newsDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit_course";
        }
        newsListerService.save(newsMapper.dtoToDomain(newsDto));
        return "redirect:/news";
    }

}
