package com.example.moviereview.repository;

import com.example.moviereview.entity.Member;
import com.example.moviereview.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Entity 를 기준으로 Query 를 작성한다.
    @Modifying          // inset, update, delete 쿼리에서 벌크 연산시 사용한다.
    @Query("DELETE FROM Review mr WHERE mr.member = :member")       // 비효율을 막기 위해 where 절 지정
    void deleteByMember(@Param("member") Member member);
}
