package com.flash21.caddycom.repository.golfFieldDetail;

import com.flash21.caddycom.entity.golfFieldDetail.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select t from Comment t where t.hole.id = :holeId")
    Optional<List<Comment>> findAllByHoleId(Long holeId);
}
