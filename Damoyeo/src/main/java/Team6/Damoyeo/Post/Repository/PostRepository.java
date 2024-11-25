package Team6.Damoyeo.Post.Repository;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.User.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    // 좋아요 기능
//    boolean exitByPost_Id_like(int postid, int userid);

    // 조회수 기능
    @Modifying
    @Query("update Post p set p.viewCount = p.viewCount + 1 where p.postId = :id")
    int updateViews(@Param("id") int id);

    // 주변 모임 기능
    @Query("SELECT p FROM Post p WHERE p.roadAddress LIKE %:keyword% AND p.postId <> :postId")
    List<Post> findByroadAddress(@Param("keyword") String keyword, @Param("postId") int postId);

    // 검색어 조회 기능
    Page<Post> findByTitleContaining(String title, Pageable pageable);

    // 태그 검색 기능
    Page<Post> findByTagContaining(String tag, Pageable pageable);

    // 유저가 적은글 찾기
    List<Post> findByUser(User user);
    
    // 검색어 조회 기능 및 상태로 찾는 기능
    Page<Post> findByTitleContainingAndStatus(String title,String status,Pageable pageable);
    // 태그 검색 및 상태로 찾는 기능
    Page<Post> findByTagContainingAndStatus(String tag, String status ,Pageable pageable);
    
    // 상태로 찾는
    Page<Post>findByStatus(Pageable pageable ,String status);

    //endDate 이전 찾는
    List<Post> findByEndDateBeforeAndStatus(LocalDateTime now, String status);


}
