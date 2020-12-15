package study.datajpa.controller;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberRepository memberRepository;

    @GetMapping("/members/v1/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members/v2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 5, sort = "username", direction = Direction.DESC) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }

    @GetMapping("/members2")
    public Page<MemberDto> list2(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        //Page<MemberDto> pageDto = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        Page<MemberDto> pageDto = page.map(MemberDto::new);
        return pageDto;
    }

    @PostConstruct
    public void init() {
        for(int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }

}
