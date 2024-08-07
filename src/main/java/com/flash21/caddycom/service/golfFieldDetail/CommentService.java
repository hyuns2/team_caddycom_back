package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.comment.CommentRequest;
import com.flash21.caddycom.dto.golfFieldDetail.comment.CommentResponse;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.entity.golfFieldDetail.Comment;
import com.flash21.caddycom.global.common.FileUploader;
import com.flash21.caddycom.repository.golfFieldDetail.hole.HoleRepository;
import com.flash21.caddycom.repository.golfFieldDetail.CommentRepository;
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

    /**
     * 멘트 정보를 생성하거나 수정한다.
     *
     * @param holeId 멘트가 포함되는 홀의 id
     * @param commentInfos 설정한 멘트 정보 DTO
     * @throws NoSuchElementException
     *          멘트 정보를 설정할 홀이 존재하지 않는 경우
     */
    @Transactional
    public void createAndUpdateComments(Long holeId, List<CommentRequest.Create> commentInfos) {
        Hole hole = holeRepository.findById(holeId).orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));

        List<Comment> savedComments = hole.getComments();
        List<Comment> newComments = new ArrayList<>();
        for(CommentRequest.Create request : commentInfos) {
            if(request.getId() == 0) {
                String imageUrl = null;
                if(request.getImage() != null && !request.getImage().isEmpty())
                    imageUrl = uploadImage(request.getImage());
                newComments.add(new Comment(null, request.getTitle(), request.getContent(), imageUrl, hole));
                break;
            }

            for(Comment comment : savedComments) {
                if(request.getId().equals(comment.getId())) {
                    String imageUrl;
                    if(request.getImage() != null) {
                        fileUploader.delete(comment.getImageUrl());
                        if (request.getImage().isEmpty()) { // 이미지 삭제
                            imageUrl = null;
                        } else { // 새 이미지로 교체
                            imageUrl = uploadImage(request.getImage());
                        }
                    } else {
                        imageUrl = comment.getImageUrl();
                    }
                    comment.update(request.getTitle(), request.getContent(), imageUrl);
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
    public List<CommentResponse.Info> getAllComments(Long holeId) {
        List<Comment> comments = commentRepository.findAllByHoleId(holeId);

        List<CommentResponse.Info> Infos = new ArrayList<>();
        comments.forEach(comment ->
            Infos.add(new CommentResponse.Info(comment.getId(), comment.getTitle(), comment.getContent(), comment.getImageUrl()))
        );

        return Infos;
    }

    /**
     * 외부 저장소에 이미지를 저장하고 그 url을 반환한다.
     *
     * @param image 저장할 이미지
     * @return 저장된 파일 url
     */
    private String uploadImage(MultipartFile image) {
        return fileUploader.upload(image);
    }
}
