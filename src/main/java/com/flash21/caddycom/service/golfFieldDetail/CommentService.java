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

/**
 * 멘트 정보와 관련된 CRUD
 *
 * @author Koo-EunSung
 */
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final HoleRepository holeRepository;

    /**
     * 멘트 정보를 생성하거나 수정한다.
     *
     * @param holeId 멘트가 포함되는 홀의 id
     * @param commentInfos 설정한 멘트 정보 DTO
     * @throws NoSuchElementException
     *          멘트 정보를 설정할 홀이 존재하지 않는 경우
     */
    @Transactional
    public void createAndUpdateComments(Long holeId, List<CommentDto.Info> commentInfos) {
        Hole hole = holeRepository.findById(holeId).orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));

        List<Comment> savedComments = hole.getComments();
        List<Comment> newComments = new ArrayList<>();
        for(CommentDto.Info info : commentInfos) {
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

    /**
     * 멘트를 삭제한다.
     *
     * @param commentIds 삭제할 멘트의 id 리스트
     */
    @Transactional
    public void deleteComments(List<Long> commentIds) {
        commentRepository.deleteAllByIdInBatch(commentIds);
    }

    /**
     * 홀의 모든 멘트 정보를 조회한다.
     *
     * @param holeId 멘트 정보를 조회할 홀의 id
     * @return 멘트 정보 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<CommentDto.Info> getAllComments(Long holeId) {
        List<Comment> comments = commentRepository.findAllByHoleId(holeId);

        List<CommentDto.Info> Infos = new ArrayList<>();
        comments.forEach(comment ->
            Infos.add(new CommentDto.Info(comment.getId(), comment.getTitle(), comment.getContent()))
        );

        return Infos;
    }
}
