package com.example.moviereview.repository;

import com.example.moviereview.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Entity 를 기준으로 Query 를 작성한다.
    @Query("SELECT m, mi, avg(coalesce(r.grade, 0)), COUNT(DISTINCT r) " +
            "FROM Movie m LEFT OUTER JOIN MovieImage mi " +
            "ON m = mi.movie " +
            "LEFT OUTER JOIN Review r " +
            "ON m = r.movie GROUP BY m ")
    Page<Object[]> getListPage(Pageable pageable);

    @Query("SELECT m, mi " +
            "FROM Movie m LEFT OUTER JOIN MovieImage mi " +
            "ON m = mi.movie " +
            "   LEFT OUTER JOIN Review r " +
            "   ON m = r.movie " +
            "WHERE m.mno = :mno GROUP BY mi ")
    List<Object[]> getMovieWithAll(@Param("mno") Long mno);     // 특정 영화 조회
}
