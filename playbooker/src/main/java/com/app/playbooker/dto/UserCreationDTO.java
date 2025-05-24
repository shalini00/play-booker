package com.app.playbooker.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationDTO {
    private String name;
    private String username;
    private String email;
    private String password;
}
