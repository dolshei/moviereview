package com.example.moviereview.controller;


import com.example.moviereview.dto.ReviewDTO;
import com.example.moviereview.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    @Autowired
    private final ReviewService reviewService;

    // 결과 데이터 : ReviewDTO 리스트, 해당 영화의 모든 리뷰 반환
    @GetMapping("/{mno}/all")
    public ResponseEntity<List<ReviewDTO>> getList(@PathVariable("mno") Long mno) {
        List<ReviewDTO> reviewDTOList = reviewService.getListOfMovie(mno);

        return new ResponseEntity<>(reviewDTOList, HttpStatus.OK);
    }

    // 결과 데이터 : 생성된 리뷰 번호, 새로운 리뷰등록
    @PostMapping("/{mno}")
    public ResponseEntity<Long> addReview(@RequestBody ReviewDTO movieReviewDTO) {

        Long reviewnum = reviewService.register(movieReviewDTO);

        return new ResponseEntity<>(reviewnum, HttpStatus.OK);
    }

    // 결과 데이터 : 리뷰의 수정 성공 여부, 리뷰 수정
    @PutMapping("/{mno}/{reviewnum}")
    public ResponseEntity<Long> modifyReview(@PathVariable Long reviewnum, @RequestBody ReviewDTO movieReviewDTO) {
        reviewService.modify(movieReviewDTO);

        return new ResponseEntity<>(reviewnum, HttpStatus.OK);
    }

    // 리뷰 삭제
    @DeleteMapping("/{mno}/{reviewnum}")
    public ResponseEntity<Long> removieReview(@PathVariable Long reviewnum) {
        reviewService.remove(reviewnum);

        return new ResponseEntity<>(reviewnum, HttpStatus.OK);
    }
}
