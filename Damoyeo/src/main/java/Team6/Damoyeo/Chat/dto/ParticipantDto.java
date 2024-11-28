package Team6.Damoyeo.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantDto {

    private Integer userId;
    private String nickname;
    private String photoUrl;

}
