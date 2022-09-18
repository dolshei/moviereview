package com.example.moviereview.repository;

import com.example.moviereview.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.stream.IntStream;

@SpringBootTest
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void insertMembers() {

        IntStream.rangeClosed(1, 100).forEach(i -> {
            Member member = Member.builder()
                    .email("r" + i + "@aries.com")
                    .pw("1111")
                    .nickname("reviewer" + i)
                    .build();

            memberRepository.save(member);
        });
    }

    @Commit         // 테스트 코드 실행 성공 후 DB에서도 업데이트 된 결과를 확인하기 위해 사용
    @Transactional
    @Test
    public void testDeleteMember() {

        Long mid = 1L;          // Memeber 의 mid
        Member member = Member.builder().mid(mid).build();

        // 회원 정보를 참조하고 있는 Entity 개체에서 데이터를 먼저 삭제한 후 회원 데이터 삭제 처리
        reviewRepository.deleteByMember(member);
        memberRepository.deleteById(mid);
    }
}
