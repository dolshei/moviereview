package com.example.moviereview.service;

import com.example.moviereview.dto.MovieDTO;
import com.example.moviereview.entity.Movie;
import com.example.moviereview.entity.MovieImage;
import com.example.moviereview.repository.MovieImageRepository;
import com.example.moviereview.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;
    private final MovieImageRepository imageRepository;

    @Transactional
    @Override
    public Long register(MovieDTO movieDTO) {

        Map<String, Object> entityMap = dtoToEntity(movieDTO);
        Movie movie = (Movie) entityMap.get("movie");
        List<MovieImage> movieImageList = (List<MovieImage>) entityMap.get("imgList");

        movieRepository.save(movie);

        movieImageList.forEach(movieImage -> {imageRepository.save(movieImage);});

        return movie.getMno();
    }
}
