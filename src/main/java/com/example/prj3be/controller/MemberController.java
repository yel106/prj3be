package com.example.prj3be.controller;

import com.example.prj3be.constant.Role;
import com.example.prj3be.domain.Member;
import com.example.prj3be.dto.FindMemberDto;
import com.example.prj3be.dto.MemberEditFormDto;
import com.example.prj3be.dto.MemberFormDto;
import com.example.prj3be.jwt.TokenProvider;
//import com.example.prj3be.dto.SocialMemberDto;
import com.example.prj3be.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @PostMapping("add")
    public void method1(@Validated @RequestBody MemberFormDto dto) {
        Member member = new Member();
        member.setLogId(dto.getLogId());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        if (dto.getFirstDigit()== 1 || dto.getFirstDigit()== 3) {
            member.setGender("male");
        }else {
            member.setGender("female");
        }
        int age = getAge(dto);

        member.setAge(age);
        member.setAddress(dto.getAddress());
        Role role = Role.valueOf(String.valueOf(dto.getRole()));
        member.setRole(role);
        member.setActivated(true);
        memberService.signup(member);
    }


    // 회원 정보
    @GetMapping
    public ResponseEntity<FindMemberDto> method2() {
        // access token Jwt Filter에서 SecurityContextHolder에 넣어줌
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("MemberController.method2");
        System.out.println("authentication = " + authentication);
        if(!authentication.getName().equals("anonymousUser")) {
            System.out.println("authentication.getName() = " + authentication.getName());
            Member findMember = memberService.findMemberByLogId(authentication.getName());
            FindMemberDto dto = new FindMemberDto();
            dto.setLogId(findMember.getLogId());
            dto.setName(findMember.getName());
            dto.setAddress(findMember.getAddress());
            dto.setEmail(findMember.getEmail());
            dto.setGender(findMember.getGender());
            dto.setRole(findMember.getRole());
            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PreAuthorize("#dto.logId == authentication.name")
    @PutMapping("/edit/{id}")
    public void method3(@PathVariable Long id,@Validated @RequestBody MemberEditFormDto dto) {
            memberService.update(id,dto);
    }
    @GetMapping(value = "check",params = "email")
    public ResponseEntity method4(String email){
        if (memberService.getEmail(email)==null){
            return ResponseEntity.notFound().build();
        }else {
            return ResponseEntity.ok().build();
        }
    }
    @GetMapping(value = "check",params = "logId")
    public ResponseEntity method6(String logId) {
        if (memberService.getLogId(logId)==null){
            return ResponseEntity.notFound().build();
        }else {
            return ResponseEntity.ok().build();
        }
    }
    @GetMapping("list")
    public Page<FindMemberDto> method5(Pageable pageable,
                                       @RequestParam(value = "k",defaultValue = "")String keyword,
                                       @RequestParam(value = "c",defaultValue = "all")String category) {
        System.out.println(keyword);
        System.out.println(category);
        Page<Member> memberPage = memberService.findMemberList(pageable,keyword,category);
        return memberPage.map(FindMemberDto::new);
    }
    @GetMapping("/{logId}/orders")
    public List<String> method7(@PathVariable String logId){
        return memberService.findOrderListByLogId(logId);
    }


    private static int getAge(MemberFormDto dto) {
        // 현재 날짜를 얻는다
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        int currentDay = currentDate.getDayOfMonth();
        // 생년월일을 분석한다.
        Integer birthdateInt = dto.getBirthDate();
        String birthdate = String.format("%06d", birthdateInt);
        int birthYear = Integer.parseInt(birthdate.substring(0, 2));
        int birthMonth = Integer.parseInt(birthdate.substring(2, 4));
        int birthDay = Integer.parseInt(birthdate.substring(4, 6));
        // 2000년대생인지 1900년대생인지 판단한다
        if (birthYear >= 0 && birthYear <= 22) { // 현재 연도를 기준으로 조정할 수 있다
            birthYear += 2000;
        } else {
            birthYear += 1900;
        }
        // 나이를 계산한다
        int age = currentYear - birthYear;
        // 생일이 지나지 않았으면 나이에서 1을 뺀다
        if (birthMonth > currentMonth || (birthMonth == currentMonth && birthDay > currentDay)) {
            age--;
        }
        return age;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteAccount(@PathVariable Long id) {
        // TODO 계정 삭제 전 참조 무결성을 위해 점검해야할 것:
        // social 멤버인지, 맞다면 social Token 삭제됐는지
        // (레코드 전체, socialTokenRepository -> findAndDeleteTokenById 사용)
        // fresh_token 삭제 됐는지
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!logId.equals("anonymousUser")) {
            tokenProvider.deleteRefreshTokenBylogId(logId);
        }else{
            //토큰 만료 된 경우 => 일단 그냥 id를 통해서 삭제 하는 걸로
            tokenProvider.deleteRefreshTokenById(id);
        }
        // payment에서 해당 멤버 관련 레코드 삭제됐는지
        // 해당 멤버별  like 삭제됐는지

        //회원 삭제
        memberService.deleteMember(id);
        return ResponseEntity.ok().build();
    }

}
