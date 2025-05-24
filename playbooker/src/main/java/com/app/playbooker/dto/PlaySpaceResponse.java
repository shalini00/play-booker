package com.app.playbooker.dto;

import com.app.playbooker.entity.PlaySpace;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlaySpaceResponse {
    HttpStatus status;
    String msg;
    PlaySpace data;
}
