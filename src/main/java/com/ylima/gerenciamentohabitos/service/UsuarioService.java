package com.ylima.gerenciamentohabitos.service;

import com.ylima.gerenciamentohabitos.dto.*;
import com.ylima.gerenciamentohabitos.entity.Usuario;
import com.ylima.gerenciamentohabitos.exception.CredInvalidasException;
import com.ylima.gerenciamentohabitos.exception.EmailRepetidoException;
import com.ylima.gerenciamentohabitos.exception.RecursoNaoEncontradoException;
import com.ylima.gerenciamentohabitos.repository.UsuarioRepository;
import com.ylima.gerenciamentohabitos.security.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO){
        if(usuarioRepository.findByEmail(usuarioRequestDTO.getEmail()).isPresent()){
            throw new EmailRepetidoException("Email ja existente, crie outro!");
        }else{
            Usuario usuario = new Usuario();
            usuario.setEmail(usuarioRequestDTO.getEmail());
            usuario.setNome(usuarioRequestDTO.getNome());
            String senhaHash = passwordEncoder.encode(usuarioRequestDTO.getSenha());
            usuario.setSenha(senhaHash);
            Usuario usuarioCriado = usuarioRepository.save(usuario);


            UsuarioResponseDTO responseDTO = converterPraResponseDTO(usuarioCriado);
            return responseDTO;

        }
    }
    public UsuarioResponseDTO buscarUsuario(Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioId = (Long) auth.getPrincipal();

        Optional<Usuario> usuario = usuarioRepository.findById(id);

        if(!usuario.isPresent()){
            throw new RecursoNaoEncontradoException("Esse usuário não existe ou não foi encontrado!");
        }

        Usuario usuarioEncontrado = usuario.get();
        if(!usuarioId.equals(usuarioEncontrado.getId())){
            throw new RecursoNaoEncontradoException("Esse usuário não existe ou não foi encontrado!");
        }

        return converterPraResponseDTO(usuarioEncontrado);
    }
    
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioUpdateDTO usuarioUpdateDTO){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioId = (Long) auth.getPrincipal();

        Optional<Usuario> usuarioEncontrado = usuarioRepository.findById(id);

        if(!usuarioEncontrado.isPresent()){
            throw new RecursoNaoEncontradoException("Esse usuário não existe ou não foi encontrado!");
        }

        if(!usuarioId.equals(usuarioEncontrado.get().getId())){
            throw new RecursoNaoEncontradoException("Esse usuário não existe ou não foi encontrado!");
        }

        Optional<Usuario> usuario = usuarioRepository.findByEmail(usuarioUpdateDTO.getEmail());

        if(!usuario.isPresent()){

       } else if (usuario.get().getId().equals(id)) {

       }else{
            throw new EmailRepetidoException("Esse email ja existe, escolha outro!");
       }
      usuarioEncontrado.get().setNome(usuarioUpdateDTO.getNome());
        usuarioEncontrado.get().setEmail(usuarioUpdateDTO.getEmail());

       Usuario usuarioAtualizado = usuarioRepository.save(usuarioEncontrado.get());
       return converterPraResponseDTO(usuarioAtualizado);

    }

    public void deletarUsuario(Long id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioId = (Long) auth.getPrincipal();

       Optional<Usuario> usuario = usuarioRepository.findById(id);

        if(!usuario.isPresent()){
            throw new RecursoNaoEncontradoException("Esse usuário não existe ou não foi encontrado!");
        }

        Usuario usuarioEncontrado = usuario.get();

        if(!usuarioId.equals(usuarioEncontrado.getId())){
            throw new RecursoNaoEncontradoException("Esse usuário não existe ou não foi encontrado!");
        }

        usuarioRepository.deleteById(id);
    }

    private UsuarioResponseDTO converterPraResponseDTO(Usuario usuario){
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(usuario.getId());
        responseDTO.setNome(usuario.getNome());
        responseDTO.setEmail(usuario.getEmail());

        return responseDTO;
    }

    public LoginResponseDTO loginUsuario(LoginRequestDTO loginRequestDTO){
        Optional<Usuario> usuario = usuarioRepository.findByEmail(loginRequestDTO.getEmail());
        if(usuario.isEmpty()){
            throw new CredInvalidasException("Email ou senha inválidos!");
        }
        String senhaDigitada =  loginRequestDTO.getSenha();
        if(!passwordEncoder.matches(senhaDigitada,usuario.get().getSenha())){
            throw new CredInvalidasException("Email ou senha inválidos!");
        }
        String token = jwtService.gerarToken(usuario.get());

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setToken(token);

        return responseDTO;
    }

}
