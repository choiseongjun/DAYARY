package us.flower.dayary.payload;

import javax.validation.constraints.NotBlank;

import lombok.Data;
/**
 *  로그인파라미터 설정 2019-09월중순
 *   by 최성준
 */
@Data
public class LoginRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    
}