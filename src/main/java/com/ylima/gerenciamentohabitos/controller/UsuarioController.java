package com.ylima.gerenciamentohabitos.controller;
import com.ylima.gerenciamentohabitos.dto.*;
import com.ylima.gerenciamentohabitos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> save(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO usuarioCriado = usuarioService.criarUsuario(usuarioRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);

    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarUsuario(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.atualizarUsuario(id, usuarioUpdateDTO));

    }

    @PatchMapping("/{id}")
        public ResponseEntity<UsuarioResponseDTO> atualizarAtributos(@PathVariable Long id, @Valid @RequestBody UsuarioPatchDTO usuarioPatchDTO){
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.atualizarAtributoUsuario(id, usuarioPatchDTO));

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = usuarioService.loginUsuario(loginRequestDTO);
        return ResponseEntity.ok(loginResponseDTO);
    }

}