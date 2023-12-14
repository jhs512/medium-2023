package com.ll.medium.domain.post.post.service;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.repository.PostRepository;
import com.ll.medium.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public void write(Member author, String title, String body, boolean isPublished) {
        Post post = Post.builder()
                .author(author)
                .title(title)
                .body(body)
                .isPublished(isPublished)
                .build();

        postRepository.save(post);
    }

    public Object findTop30ByIsPublishedOrderByIdDesc(boolean isPublished) {
        return postRepository.findTop30ByIsPublishedOrderByIdDesc(isPublished);
    }

    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }

    public Page<Post> search(String kw, Pageable pageable) {
        return postRepository.findByTitleContainingIgnoreCaseOrBodyContainingIgnoreCase(kw, kw, pageable);
    }

    @Transactional
    public Post makeTempPost(Member author) {
        Post post = Post.builder()
                .author(author)
                .isTemp(true)
                .build();

        return postRepository.save(post);
    }

    public Optional<Post> findTempPostByAuthor(Member author) {
        return postRepository.findByAuthorAndIsTemp(author, true);
    }

    @Transactional
    public RsData<Post> genTempPost(Member author) {
        Optional<Post> tempPostByAuthor = findTempPostByAuthor(author);

        if (tempPostByAuthor.isPresent()) {
            return RsData.of("200-1", "기존에 임시저장된 글을 불러옵니다.", tempPostByAuthor.get());
        }

        Post post = makeTempPost(author);

        return RsData.of("201-1", "", post);
    }

    public Optional<Post> findLatest() {
        return postRepository.findTopByOrderByIdDesc();
    }
}
