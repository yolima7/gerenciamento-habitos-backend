package com.ylima.gerenciamentohabitos;

import com.ylima.gerenciamentohabitos.dto.*;
import com.ylima.gerenciamentohabitos.entity.Usuario;
import com.ylima.gerenciamentohabitos.exception.CredInvalidasException;
import com.ylima.gerenciamentohabitos.exception.EmailRepetidoException;
import com.ylima.gerenciamentohabitos.exception.RecursoNaoEncontradoException;
import com.ylima.gerenciamentohabitos.repository.UsuarioRepository;
import com.ylima.gerenciamentohabitos.security.JwtService;
import com.ylima.gerenciamentohabitos.service.UsuarioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
   private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
   private UsuarioService usuarioService;

    @Test
    void criarUsuario(){
        UsuarioRequestDTO usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setNome("Yuri");
        usuarioRequestDTO.setEmail("euylima@gmail.com");
        usuarioRequestDTO.setSenha("Ylima7272");

        when(usuarioRepository.findByEmail(usuarioRequestDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(usuarioRequestDTO.getSenha())).thenReturn("hash_simulado");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioResponseDTO resposta = usuarioService.criarUsuario(usuarioRequestDTO);
        assertEquals("Yuri", resposta.getNome());
        assertEquals("euylima@gmail.com", resposta.getEmail());

        verify(passwordEncoder).encode(usuarioRequestDTO.getSenha());

    }
    @Test
    void emailExistente(){
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail("euylima@gmail.com");

        UsuarioRequestDTO usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setNome("Yuri");
        usuarioRequestDTO.setEmail("euylima@gmail.com");
        usuarioRequestDTO.setSenha("Ylima7272");

        when(usuarioRepository.findByEmail(usuarioExistente.getEmail())).thenReturn(Optional.of(usuarioExistente));
        assertThrows(EmailRepetidoException.class,  () -> usuarioService.criarUsuario(usuarioRequestDTO));

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void loginUsuario(){
        LoginRequestDTO logUser = new LoginRequestDTO();
        logUser.setEmail("fof@gmail.com");
        logUser.setSenha("Umbrella2");

        Usuario user = new Usuario();
        user.setNome("Fofo");
        user.setEmail("fof@gmail.com");
        user.setSenha("hash_simulado");


        when(usuarioRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(logUser.getSenha(), user.getSenha())).thenReturn(true);
        when(jwtService.gerarToken(any())).thenReturn("seu_token");


        LoginResponseDTO respostaLog =  usuarioService.loginUsuario(logUser);
        assertEquals("seu_token", respostaLog.getToken());

    }

    @Test
    void loginEmailInexistente(){
        LoginRequestDTO logUser = new LoginRequestDTO();
        logUser.setEmail("macaco@gmail.com");
        logUser.setSenha("Macaco0205");

        when(usuarioRepository.findByEmail(logUser.getEmail())).thenReturn(Optional.empty());
        assertThrows(CredInvalidasException.class,  () -> usuarioService.loginUsuario(logUser));
    }

    @Test
    void logSenhaIncorreta(){
        LoginRequestDTO logUser = new LoginRequestDTO();
        logUser.setEmail("lili@gmail.com");
        logUser.setSenha("lililover2");

        Usuario user = new Usuario();
        user.setNome("Lili");
        user.setEmail("lili@gmail.com");
        user.setSenha("hash_simulado");

        when(usuarioRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(logUser.getSenha(), user.getSenha())).thenReturn(false);

        assertThrows(CredInvalidasException.class, () -> usuarioService.loginUsuario(logUser));
    }

    @Test
    void updateUser(){
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(1L);

        SecurityContextHolder.setContext(context);

        Usuario user = new Usuario();
        user.setId(1L);
        user.setNome("Yuri");
        user.setEmail("yuri@gmail.com");

        UsuarioPatchDTO userAtt = new  UsuarioPatchDTO();
        userAtt.setNome("Yuri Lima");
        userAtt.setEmail(null);

        when(usuarioRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UsuarioResponseDTO respostaAtt = usuarioService.atualizarAtributoUsuario(1L, userAtt);
        assertEquals("Yuri Lima", respostaAtt.getNome());
        assertEquals("yuri@gmail.com", respostaAtt.getEmail());
    }

    @AfterEach
    void clear(){
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateAnotherUsuer(){
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(1L);

        SecurityContextHolder.setContext(context);

        Usuario user = new Usuario();
        user.setId(2L);
        user.setNome("kekel");
        user.setEmail("kekel@gmail.com");

        UsuarioPatchDTO userAtt = new UsuarioPatchDTO();
        userAtt.setNome("kekel santos");
        userAtt.setEmail(null);

        when(usuarioRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(RecursoNaoEncontradoException.class, () -> usuarioService.atualizarAtributoUsuario(2L, userAtt));
    }

    @Test
    void updateSenha(){
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(1L);

        SecurityContextHolder.setContext(context);

        Usuario user = new Usuario();
        user.setId(1L);
        user.setNome("nana");
        user.setEmail("nana@gmail.com");
        user.setSenha("nanabanana");

        when(usuarioRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UsuarioSenhaDTO senhaAtt = new  UsuarioSenhaDTO();
        senhaAtt.setSenhaAtual("SenhaAntiga123");
        senhaAtt.setNovaSenha("SenhaNova123");

        when(passwordEncoder.matches("SenhaAntiga123", user.getSenha())).thenReturn(true);
        when(passwordEncoder.encode("SenhaNova123")).thenReturn("hash_simulado");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioResponseDTO respostaUser =  usuarioService.atualizarSenha(user.getId(), senhaAtt);
        assertEquals("nana", respostaUser.getNome());
        assertEquals("nana@gmail.com", respostaUser.getEmail());

        verify(passwordEncoder).encode("SenhaNova123");

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertEquals("hash_simulado", usuarioCaptor.getValue().getSenha());
    }

    @Test
    void updateSenhaAnotherUser(){
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(1L);

        SecurityContextHolder.setContext(context);

        Usuario user = new Usuario();
        user.setId(2L);
        user.setNome("nina");
        user.setEmail("nina@gmail.com");
        user.setSenha("ninaamiga");

        when(usuarioRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UsuarioSenhaDTO senhaAtt = new  UsuarioSenhaDTO();
        senhaAtt.setSenhaAtual("SenhaAntiga123");
        senhaAtt.setNovaSenha("SenhaNova123");

        assertThrows(RecursoNaoEncontradoException.class, () -> usuarioService.atualizarSenha(user.getId(), senhaAtt));

    }
    @Test
    void deleteUser(){
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(1L);

        SecurityContextHolder.setContext(context);

        Usuario user = new Usuario();
        user.setId(1L);
        user.setNome("alemao");
        user.setEmail("alemao@gmail.com");
        user.setSenha("alemaobonitao");

        when(usuarioRepository.findById(user.getId())).thenReturn(Optional.of(user));

        usuarioService.deletarUsuario(user.getId());
        verify(usuarioRepository).deleteById(user.getId());
    }
    @Test
    void deleteAnotherUser(){
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(1L);

        SecurityContextHolder.setContext(context);

        Usuario user = new Usuario();
        user.setId(2L);
        user.setNome("bob");
        user.setEmail("bob@gmail.com");
        user.setSenha("bobbobao");

        when(usuarioRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(RecursoNaoEncontradoException.class, () -> usuarioService.deletarUsuario(user.getId()));

    }

    @Test
    void deleteUserNotFound(){
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);

        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(1L);

        SecurityContextHolder.setContext(context);

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> usuarioService.deletarUsuario(99L));
    }
    @Test
    void updateSenhaIncorreta() {
        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(1L);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Yuri");
        usuario.setEmail("yuri@gmail.com");
        usuario.setSenha("hash_simulado");

        UsuarioSenhaDTO senhaDTO = new UsuarioSenhaDTO();
        senhaDTO.setSenhaAtual("SenhaErrada123");
        senhaDTO.setNovaSenha("SenhaNova123");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senhaDTO.getSenhaAtual(), usuario.getSenha())).thenReturn(false);

        assertThrows(CredInvalidasException.class, () -> usuarioService.atualizarSenha(1L, senhaDTO));
    }
    @Test
    void atualizarEmailExistente() {
        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(1L);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Yuri");
        usuario.setEmail("yuri@gmail.com");

        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);
        outroUsuario.setEmail("outro@gmail.com");

        UsuarioPatchDTO patchDTO = new UsuarioPatchDTO();
        patchDTO.setEmail("outro@gmail.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("outro@gmail.com")).thenReturn(Optional.of(outroUsuario));

        assertThrows(EmailRepetidoException.class, () -> usuarioService.atualizarAtributoUsuario(1L, patchDTO));
    }
}
