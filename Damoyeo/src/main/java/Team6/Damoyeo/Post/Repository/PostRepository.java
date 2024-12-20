package Team6.Damoyeo.Post.Repository;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.User.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer>, JpaSpecificationExecutor<Post> {

    // 좋아요 기능
//    boolean exitByPost_Id_like(int postid, int userid);

    // 조회수 기능
    @Modifying
    @Query("update Post p set p.viewCount = p.viewCount + 1 where p.postId = :id")
    int updateViews(@Param("id") int id);

    // 주변 모임 기능
    @Query("SELECT p FROM Post p WHERE p.roadAddress LIKE %:keyword% AND p.postId <> :postId")
    List<Post> findByroadAddress(@Param("keyword") String keyword, @Param("postId") int postId);

    // 주변 모임 기능인데 상태 1인거 구하면서 좋아요 갯수 내림차순인데 탑4까지만
    List<Post> findTop4ByRoadAddressContainingAndPostIdNotAndStatusOrderByLikeCountDesc(String roadAddress, int postId, String status);
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

    //게시글 상태가 아닌거 찾는거
    List<Post> findByUserAndStatusNot(User user, String status);

    // 필터링을 위한 새로운 메서드들 추가
    //상태, 위치, 카테고리 모두를 조건으로 검색하고 Containing은 부분 일치 검색
    Page<Post> findByStatusAndRoadAddressContainingAndTag(String status, String location, String tag, Pageable pageable);
    //상태와 위치로 찾는거
    Page<Post> findByStatusAndRoadAddressContaining(String status, String location, Pageable pageable);
    //상태와 태그로 찾는거
    Page<Post> findByStatusAndTag(String status, String tag, Pageable pageable);
}
