package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.entity.Carousel;
import com.graduation.graduationdesign.mapper.CarouselMapper;
import com.graduation.graduationdesign.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图服务实现类
 */
@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public List<Carousel> getEnabledCarousels() {
        return carouselMapper.findAllEnabled();
    }

    @Override
    public List<Carousel> getAllCarousels() {
        return carouselMapper.findAll();
    }

    @Override
    public void addCarousel(Carousel carousel) {
        carouselMapper.insert(carousel);
    }

    @Override
    public void updateCarousel(Carousel carousel) {
        carouselMapper.update(carousel);
    }

    @Override
    public void deleteCarousel(Long id) {
        carouselMapper.delete(id);
    }
}
