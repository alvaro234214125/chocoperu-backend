package com.ecommerce.chocoperu.dto;

import com.ecommerce.chocoperu.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
}
