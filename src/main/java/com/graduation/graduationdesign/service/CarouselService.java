package com.graduation.graduationdesign.service;

import com.graduation.graduationdesign.entity.Carousel;

import java.util.List;

/**
 * 轮播图服务接口
 */
public interface CarouselService {
    List<Carousel> getEnabledCarousels();
    List<Carousel> getAllCarousels();
    void addCarousel(Carousel carousel);
    void updateCarousel(Carousel carousel);
    void deleteCarousel(Long id);
}
