package com.example.moviereview.repository;

import com.example.moviereview.entity.Member;
import com.example.moviereview.entity.Movie;
import com.example.moviereview.entity.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Entity 를 기준으로 Query 를 작성한다.
    @Modifying          // inset, update, delete 쿼리에서 벌크 연산시 사용한다.
    @Query("DELETE FROM Review mr WHERE mr.member = :member")       // 비효율을 막기 위해 where 절 지정
    void deleteByMember(@Param("member") Member member);

    // @EntityGraph : Entity의 특정한 속성을 같이 로딩하도록 표시하는 어노테이션
    // -> 특정 기능을 수핼할 때만 EAGER 로딩을 하도록 지정할 수 있다.
    // Review 처리시 @EntityGraph 적용해 Member도 같이 로딩
    @EntityGraph(attributePaths = {"member"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Review> findByMovie(Movie movie);
}
