package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.comment.CommentRequest;
import com.flash21.caddycom.dto.golfFieldDetail.comment.CommentResponse;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.entity.golfFieldDetail.Comment;
import com.flash21.caddycom.global.common.fileUploader.FileUploader;
import com.flash21.caddycom.repository.golfFieldDetail.hole.HoleRepository;
import com.flash21.caddycom.repository.golfFieldDetail.comment.CommentRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileUploader fileUploader;
    private final CommentRepository commentRepository;
    private final HoleRepository holeRepository;
    private final ObjectProvider<CommentService> commentServiceProvider;

    public void processComments(Hole hole, List<CommentRequest.Create> request) {
        if(request == null || request.isEmpty()) return;

        final CommentService commentService = commentServiceProvider.getObject();
        for(CommentRequest.Create create : request) {
            String imageUrl = uploadImage(create.getImage());
            if(create.getId() == 0) {
                commentService.createComment(hole, create, imageUrl);
            } else {
                commentService.updateComment(create, imageUrl);
            }
        }
    }

    @Transactional
    public void createComment(Hole hole, CommentRequest.Create request, String imageUrl) {
        commentRepository.save(new Comment(null, request.getTitle(), request.getContent(), imageUrl, hole));
    }

    @Transactional
    public void updateComment(CommentRequest.Create request, String imageUrl) {
        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 멘트가 존재하지 않습니다."));
        if(request.getImage() == null) imageUrl = comment.getImageUrl();
        comment.update(request.getTitle(), request.getContent(), imageUrl);
    }
    /**
     * 멘트를 삭제한다.
     *
     * @param commentIds 삭제할 멘트의 id 리스트
     */
    @Transactional
    public void deleteComments(List<Long> commentIds) {
        if(commentIds == null || commentIds.isEmpty()) return;
        commentRepository.deleteAllByIdInBatch(commentIds);
    }

    /**
     * 홀의 모든 멘트 정보를 조회한다.
     *
     * @param holeId 멘트 정보를 조회할 홀의 id
     * @return 멘트 정보 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<CommentResponse.Info> getAllComments(Long holeId) {
        List<Comment> comments = commentRepository.findAllByHoleId(holeId);

        return comments.stream()
                .map(CommentResponse.Info::from)
                .toList();
    }

    /**
     * 외부 저장소에 이미지를 저장하고 그 url을 반환한다.
     *
     * @param image 저장할 이미지
     * @return 저장된 파일 url
     */
    private String uploadImage(MultipartFile image) {
        return fileUploader.upload(image, "hole-detail/");
    }
}
