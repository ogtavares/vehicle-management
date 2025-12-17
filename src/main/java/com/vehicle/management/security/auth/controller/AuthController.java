package com.vehicle.management.security.auth.controller;

import com.vehicle.management.security.auth.dto.request.AuthRequestDTO;
import com.vehicle.management.security.auth.dto.response.AuthResponseDTO;
import com.vehicle.management.security.jwt.service.JwtService;
import com.vehicle.management.security.user.dto.request.UserRequestDTO;
import com.vehicle.management.security.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(
        name = "Autenticação",
        description = "Endpoints responsáveis por autenticação e registro de usuários"
)
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Login do usuário",
            description = "Autentica o usuário a partir de usuario e senha e retorna um token JWT válido."
    )
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação nos dados de entrada")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @PostMapping("/logar")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody @Valid AuthRequestDTO request
    ) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails user = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @Operation(
            summary = "Registro de usuário",
            description = """
                    Registra um novo usuário no sistema.

                    O campo 'role' aceita apenas os valores:
                    - ADMIN
                    - USER

                    Caso o campo 'role' não seja informado, o usuário será registrado automaticamente como USER.
                    """
    )
    @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação nos dados de entrada")
    @ApiResponse(responseCode = "409", description = "Usuário já existe")
    @PostMapping("/registrar")
    public ResponseEntity<Void> register(
            @RequestBody @Valid UserRequestDTO dto
    ) {
        userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
