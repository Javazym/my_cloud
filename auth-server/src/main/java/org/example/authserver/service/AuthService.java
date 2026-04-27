package org.example.authserver.service;

import org.example.authserver.dto.create.RegisterDto;
import org.example.authserver.dto.query.AuthCodeDto;
import org.example.authserver.dto.query.LoginDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String login(LoginDto dto);
    String register(RegisterDto dto);
    String authCode(AuthCodeDto dto);
}
