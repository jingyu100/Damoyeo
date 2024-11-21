package Team6.Damoyeo.Like.Repository;

import Team6.Damoyeo.Like.Entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like,Integer> {

    // 좋아요 개수 확인
    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.postId = :postId AND l.user.userId = :userId")
    int countLikeByPostIdAndUserId(@Param("postId") int postId, @Param("userId") int userId);

    // 특정 게시글의 좋아요 삭제
    @Query("DELETE FROM Like l WHERE l.post.postId = :postId AND l.user.userId = :userId")
    void deleteByPostIdAndUserId(@Param("postId") int postId,@Param("userId") int userId);
}
