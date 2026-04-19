package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.PostComment;
import com.api.bkhouse.entity.response.ICommentCompare;
import com.api.bkhouse.repository.PostCommentRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostCommentService {
    @Autowired
    private PostCommentRepository repository;

    @Transactional
    public PostComment save(PostComment postComment) {
        return repository.save(postComment);
    }

    public List<PostComment> findByPostId(UUID postId) {
        return repository.findByPostId(postId);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public boolean canDelete(UUID id, UUID userId) {
        if (userId.equals(UUID.fromString("admin"))) {
            return true;
        }
        Optional<ICommentCompare> commentCompareOptional = repository.compareOwner(id);
        if (commentCompareOptional.isEmpty()) {
            return false;
        }
        ICommentCompare commentCompare = commentCompareOptional.get();
        if (userId.equals(commentCompare.getCommentOwner()) || userId.equals(commentCompare.getPostOwner())) {
            return true;
        }
        return false;
    }
}
