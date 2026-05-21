package br.dev.javacom.service;

import br.dev.javacom.dto.request.LoginRequest;
import br.dev.javacom.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
