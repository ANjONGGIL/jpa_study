package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional // DB에 직접 insert 되지 않음. 반복적인 작업을 하기 때문에
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
//    @Rollback(value = false) 직접 DB에 넣기
    public void 회원가입()throws Exception{
        Member member = new Member();
        member.setName("kim");

        Long memberId = memberService.join(member);

        assertEquals(member, memberRepository.findOne(memberId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외()throws Exception{
        Member member1 = new Member();
        member1.setName("Ahn");

        Member member2 = new Member();
        member2.setName("Ahn");

        memberService.join(member1);
        memberService.join(member2);
        try{

        }catch (IllegalStateException e){
            return;
        }

        Assert.fail("예외가 발생한다");
    }
}