package com.chiSoftware.testTask.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data public class AuthRequest {
    private String login;
    private String password;
}
