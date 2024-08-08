package com.flash21.caddycom.service.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.hole.HoleRequest;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.global.common.fileUploader.FileUploader;
import com.flash21.caddycom.repository.golfFieldDetail.CommentRepository;
import com.flash21.caddycom.repository.golfFieldDetail.hole.HoleRepository;
import com.flash21.caddycom.repository.golfFieldDetail.tee.TeeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 홀 정보와 관련된 CRUD
 *
 * @author Koo-EunSung
 */
@Service
@RequiredArgsConstructor
public class HoleService {
    private final HoleRepository holeRepository;
    private final TeeService teeService;
    private final CommentService commentService;
    private final TeeRepository teeRepository;
    private final CommentRepository commentRepository;
    private final FileUploader fileUploader;

    /**
     * 홀의 핸디를 수정한다.
     *
     * @param request 홀의 핸디 수정 요청 DTO
     * @throws NoSuchElementException
     *          핸디를 수정할 홀이 존재하지 않는 경우
     */
    @Transactional
    public void updateHandicap(HoleRequest.UpdateHandicap request) {
        Hole hole = holeRepository.findById(request.getHoleId())
                .orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));

        hole.updateHandicap(request.getHandicap());
    }

    /**
     * 홀의 파(par)를 수정한다.
     *
     * @param request 홀의 파 수정 요청 DTO
     * @throws NoSuchElementException
     *          파를 수정할 홀이 존재하지 않는 경우
     */
    @Transactional
    public void updatePar(HoleRequest.UpdatePar request) {
        Hole hole = holeRepository.findById(request.getHoleId())
                .orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다"));

        hole.updatePar(request.getPar());
    }

    /**
     * 홀 정보를 생성한다.
     *
     * @param courses 코스 리스트
     * @return 생성된 홀의 id 리스트 <b>(mysql 사용 시 id가 아닌 null 반환됨)</b>
     */
    @Transactional
    public List<Long> createHoles(List<Course> courses) {
        List<Hole> holes = new ArrayList<>();
        for(Course course : courses) {
            for(int i = course.getHoles().size() + 1; i <= course.getTotalHoles(); i++) {
                Hole hole = new Hole(i, course);
                holes.add(hole);
            }
        }
        List<Long> holeIds = holeRepository.saveAllInBatch(holes);
        return holeIds;
    }

    /**
     * 홀의 상세 정보(파, 핸디, 티, 멘트)를 설정한다.<br>
     * request에 따라 홀 정보의 수정 및 티와 멘트의 생성, 수정, 삭제가 이루어질 수 있다.
     *
     * @param request 홀 상세 정보 설정 DTO
     */
    @Transactional
    public void createDetailInfo(HoleRequest.CreateDetailInfo request) {
        Hole savedHole = holeRepository.findById(request.getHoleId()).orElseThrow(() -> new NoSuchElementException("해당 홀은 존재하지 않습니다."));

        if(request.getPar() != savedHole.getPar())
            savedHole.updatePar(request.getPar());
        if(request.getHandicap() != savedHole.getHandicap())
            savedHole.updateHandicap(request.getHandicap());

        if(request.getImage() != null) { //이미지에 변경사항 존재
            String imageUrl;
            fileUploader.delete(savedHole.getImageUrl());
            if(request.getImage().isEmpty()) // 이미지 삭제
                imageUrl = null;
            else // 새 이미지로 교체
                imageUrl = uploadImage(request.getImage());

            savedHole.updateImage(imageUrl);
        }

        if(request.getTeeData() != null)
            teeService.createAndUpdateTees(request.getHoleId(), request.getTeeData());
        if(!request.getDeleteTeeIds().isEmpty())
            teeService.deleteTees(request.getDeleteTeeIds());

        if(request.getCommentData() != null)
            commentService.createAndUpdateComments(request.getHoleId(), request.getCommentData());
        if(!request.getDeleteCommentIds().isEmpty())
            commentService.deleteComments(request.getDeleteCommentIds());
    }

    /**
     * 특정 코스에 포함된 모든 홀을 티, 멘트와 함께 삭제한다.
     *
     * @param courses 코스 리스트
     */
    @Transactional
    public void deleteHoles(List<Course> courses) {
        List<Hole> deleteHoles = new ArrayList<>();
        for(Course course : courses) {
            for (Hole hole : course.getHoles()) {
                if (hole.getNum() > course.getTotalHoles())
                    deleteHoles.add(hole);
            }
        }
        commentRepository.deleteAllByHoles(deleteHoles);
        teeRepository.deleteAllByHoles(deleteHoles);
        holeRepository.deleteAllInBatch(deleteHoles);
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
