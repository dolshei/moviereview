package com.example.moviereview.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = "movie")
public class MovieImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inum;

    // java.util.UUID를 이용해 고유 번호 생성
    private String uuid;

    private String imgName;

    private String path;

    // movie 테이블이 PK를 가지고 movie_image 테이블이 FK를 가지므로 movie_image에 @ManyToOne 적용
    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;
}
