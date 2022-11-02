package com.example.moviereview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@Data
public class PageRequestDTO {

    // 화면에서 전돨되는 page, size 라는 파라미터를 수집한다.
    private int page;
    private int size;

    // 검색 처리를 위해 추가
    private String type;
    private String keyword;

    public PageRequestDTO() {
        // 기본값 지정
        this.page = 1;
        this.size = 10;
    }

    public Pageable getPageable(Sort sort) {
        // 페이지 번호가 0부터 시작한다는 점을 감안해 1페이지의 경우 0이 될 수 있도록 page-1로 작성해준다.
        // 정렬은 다양한 상황에서 쓰기 위해 별도의 파라미터로 받도록 설계
        return PageRequest.of(page - 1, size, sort);
    }
}
