package com.great.project.db.cloudfoundryapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserPwdDto {
    private String user;
    private String pwd;
}
