package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
public class PostControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private PostService postService;

    @Test
    @DisplayName("GET /post/write, 임시 글이 생성되고 그 글의 수정페이지로 이동한다.")
    @WithUserDetails("user1")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/post/write"))
                .andDo(print());

        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("showWrite"))
                .andExpect(redirectedUrlPattern("/post/*/modify?msg=*"));
    }

    @Test
    @DisplayName("GET /post/write, 해당 회원에 대해서 이미 임시글이 존재한다면 새 임시글을 만들지 않는다.")
    @WithUserDetails("user1")
    void t2() throws Exception {
        ResultActions resultActions1 = mvc
                .perform(get("/post/write"))
                .andDo(print());

        Optional<Post> latest1 = postService.findLatest();

        ResultActions resultActions2 = mvc
                .perform(get("/post/write"))
                .andDo(print());

        Optional<Post> latest2 = postService.findLatest();

        assertThat(latest1).isEqualTo(latest2);
    }
}
