package com.flash21.caddycom.repository.golfFieldDetail;

import com.flash21.caddycom.entity.golfFieldDetail.Comment;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select t from Comment t where t.hole.id = :holeId")
    Optional<List<Comment>> findAllByHoleId(Long holeId);

    @Modifying
    @Transactional
    @Query("delete from Comment c where c.hole.course.formation.id in :formationIds")
    void deleteAllByFormationIds(Iterable<Long> formationIds);

    @Modifying
    @Transactional
    @Query("delete from Comment c where c.hole.course.id in :courseIds")
    void deleteAllByCourseIds(Iterable<Long> courseIds);

    @Modifying
    @Transactional
    @Query("delete from Comment c where c.hole in :holes")
    void deleteAllByHoles(Iterable<Hole> holes);
}
