package com.example.fileserver.model.dto;


import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private Set<Role> roles;
}