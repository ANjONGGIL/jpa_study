package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    // 위 방법으로 주입을 할 시 MemberService가 호출 될때 인젝션을 생성하고 테스트를 진행할 때 편리해진다.

    //회원 가입
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member); // 중복회원 검증

        memberRepository.save(member);
        return member.getId();
    }

    //전체회원 조회
    @Transactional(readOnly = true)
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //회원 한명 조회
    @Transactional(readOnly = true) // DB 성능을 최적화 할수 있다. 읽기에는 readOnly를 true로 만들자
    public Member findMember(Long memberId){
        return memberRepository.findOne(memberId);
    }

    //중복회원 검증 로직
    // 이런 방식의 검증의 경우 데이터가 동시에 들어왔을 때 처리가 불가능 그래서 마지막 수단으로 DB에 유니크 키를 걸어 방어를 하는 것이 좋음
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());

        if (findMembers.size() > 0){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
}
