package site.metacoding.blogv3.web.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.metacoding.blogv3.domain.post.Post;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDetailRespDto {
    private Post post;
    private boolean isPageOwner; // Lombok일 때 getter가 변수명 그대로 나옴(getter : isPageOwner(){}, setter :
                                 // setPageOwner(){}
    private boolean isLove; // 좋아요를 했으면 true, 아니면 false
    private Integer loveId;
}
