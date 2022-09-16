package com.example.moviereview.repository;

import com.example.moviereview.entity.MovieImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieImageRepository extends JpaRepository<MovieImage, Long> {

}
