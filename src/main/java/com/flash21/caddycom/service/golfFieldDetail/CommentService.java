package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.comment.CommentDto;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.entity.golfFieldDetail.Comment;
import com.flash21.caddycom.repository.golfFieldDetail.hole.HoleRepository;
import com.flash21.caddycom.repository.golfFieldDetail.CommentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final HoleRepository holeRepository;

    @Transactional
    public void createAndUpdateComments(Long holeId, List<CommentDto.Info> tipInfos) {
        Hole hole = holeRepository.findById(holeId).orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));

        List<Comment> savedComments = hole.getComments();
        List<Comment> newComments = new ArrayList<>();
        for(CommentDto.Info info : tipInfos) {
            if(info.getId() == 0) {
                newComments.add(new Comment(null, info.getTitle(), info.getContent(), hole));
                break;
            }

            for(Comment comment : savedComments) {
                if(info.getId().equals(comment.getId())) {
                    comment.update(info.getTitle(), info.getContent());
                    break;
                }
            }
        }

        savedComments.addAll(newComments);
    }

    @Transactional
    public void deleteComments(List<Long> tipInfoIds) {
        commentRepository.deleteAllByIdInBatch(tipInfoIds);
    }

    @Transactional(readOnly = true)
    public List<CommentDto.Info> getAllComments(Long holeId) {
        List<Comment> comments = commentRepository.findAllByHoleId(holeId)
                .orElse(null);

        if(comments == null) {
            return null;
        }

        List<CommentDto.Info> Infos = new ArrayList<>();
        comments.forEach(comment ->
            Infos.add(new CommentDto.Info(comment.getId(), comment.getTitle(), comment.getContent()))
        );

        return Infos;
    }
}
