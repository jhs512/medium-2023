package com.ll.medium.domain.member.member.controller;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.global.app.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
public class MemberControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("GET /member/login, 로그인 페이지를 보여준다.")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/member/login"))
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("domain/member/member/login"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showLogin"))
                .andExpect(content().string(containsString("""
                        로그인
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="text" name="username"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="password" name="password"
                        """.stripIndent().trim())));
    }

    @Test
    @DisplayName("POST /member/login, 비밀번호를 틀리면 인증이 안된다.")
    void t2() throws Exception {
        mvc.perform(
                        formLogin("/member/login")
                                .user("username", "user1")
                                .password("password", "12345")
                )
                .andDo(print())
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("POST /member/login, 올바른 계정정보를 입력하면 인증이 된다.")
    void t3() throws Exception {
        mvc.perform(
                        formLogin("/member/login")
                                .user("username", "user1")
                                .password("password", "1234")
                )
                .andDo(print())
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("GET /member/join, 회원가입 페이지를 보여준다.")
    void t4() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/member/join"))
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("domain/member/member/join"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showJoin"))
                .andExpect(content().string(containsString("""
                        회원가입
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="text" name="username"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="password" name="password"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="password" name="passwordConfirm"
                        """.stripIndent().trim())));
    }

    @Test
    @DisplayName("POST /member/join, 회원가입이 성공하면 회원이 생성된다.")
    void t5() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/member/join")
                                .with(csrf())
                                .param("username", "usernew")
                                .param("password", "1234")
                )
                .andDo(print());

        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(redirectedUrlPattern("/member/login?lastUsername=usernew&msg=**"));

        Member member = memberService.findLatest().get();

        assertThat(member.getUsername()).isEqualTo("usernew");
        assertThat(passwordEncoder.matches("1234", member.getPassword())).isTrue();
    }

    @Test
    @DisplayName("POST /member/join, 회원가입이 성공하면 회원이 생성된다.")
    void t6() throws Exception {
        String profileImgFilePath = AppConfig.getResourcesStaticDirPath() + "/android-chrome-512x512.png";

        ResultActions resultActions = mvc
                .perform(
                        multipart("/member/join")
                                .with(csrf())
                                .param("username", "usernew")
                                .param("password", "1234")
                                .param("email", "usernew@test.com")
                                .param("nickname", "홍길동")
                )
                .andDo(print());

        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(redirectedUrlPattern("/member/login?lastUsername=usernew&msg=**"));

        Member member = memberService.findLatest().get();

        assertThat(member.getUsername()).isEqualTo("usernew");
        assertThat(passwordEncoder.matches("1234", member.getPassword())).isTrue();
    }

    @Test
    @DisplayName("POST /member/join, 이미 사용중인 username 을 입력하면 회원가입에 실패한다.")
    void t7() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/member/join")
                                .with(csrf())
                                .param("username", "admin")
                                .param("password", "1234")
                )
                .andDo(print());

        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"));
    }
}
