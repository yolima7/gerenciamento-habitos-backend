package com.ylima.gerenciamentohabitos;
import com.ylima.gerenciamentohabitos.dto.HabitoPatchDTO;
import com.ylima.gerenciamentohabitos.dto.HabitoRequestDTO;
import com.ylima.gerenciamentohabitos.dto.HabitoResponseDTO;
import com.ylima.gerenciamentohabitos.entity.Habito;
import com.ylima.gerenciamentohabitos.entity.Periodo;
import com.ylima.gerenciamentohabitos.entity.Usuario;
import com.ylima.gerenciamentohabitos.exception.RecursoNaoEncontradoException;
import com.ylima.gerenciamentohabitos.repository.HabitoRepository;
import com.ylima.gerenciamentohabitos.repository.UsuarioRepository;
import com.ylima.gerenciamentohabitos.service.HabitoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class HabitoServiceTest{

@Mock
private HabitoRepository habitoRepository;

@Mock
private UsuarioRepository usuarioRepository;

@InjectMocks
private HabitoService habitoService;


   @BeforeEach
   void configurar(){
      SecurityContext context = mock(SecurityContext.class);
      Authentication auth = mock(Authentication.class);

      when(context.getAuthentication()).thenReturn(auth);
      when(auth.getPrincipal()).thenReturn(1L);

      SecurityContextHolder.setContext(context);
   }

   @AfterEach
   void limpar(){
      SecurityContextHolder.clearContext();
   }

   @Test
    void buscarHabito(){
       Usuario usuario = new Usuario();
       usuario.setId(1L);

       Habito habito = new Habito();
       habito.setId(1L);
       habito.setTitulo("Estudar java");
       habito.setUsuario(usuario);
       when(habitoRepository.findById(1L)).thenReturn(Optional.of(habito));

       HabitoResponseDTO resposta = habitoService.buscarHabito(1L);

       assertEquals("Estudar java", resposta.getTitulo());
   }

   @Test
   void LancarExcecao(){
      when(habitoRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(RecursoNaoEncontradoException.class, () -> habitoService.buscarHabito(1L));

   }

   @Test
   void HabitoOutroUsuario(){

      Usuario usuario2 = new Usuario();
      usuario2.setId(2L);

      Habito habito = new Habito();
      habito.setId(1L);
      habito.setTitulo("Andar de bike");
      habito.setUsuario(usuario2);

      when(habitoRepository.findById(1L)).thenReturn(Optional.of(habito));

      assertThrows(RecursoNaoEncontradoException.class, () -> habitoService.buscarHabito(1L));
   }

   @Test
   void criarHabito(){
      Usuario usuario = new Usuario();
      usuario.setId(1L);
      when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

      HabitoRequestDTO habito = new HabitoRequestDTO();
      habito.setTitulo("ir ao mercado");
      habito.setUnidade("vezes");
      habito.setQuantidade(2);
      habito.setPeriodo(Periodo.MENSAL);

      when(habitoRepository.save(any(Habito.class))).thenAnswer(save -> save.getArgument(0));

      HabitoResponseDTO resposta = habitoService.criarHabito(habito);
      assertEquals("ir ao mercado", resposta.getTitulo());
      assertEquals("vezes", resposta.getUnidade());
      assertEquals(2, resposta.getQuantidade());
      assertEquals(Periodo.MENSAL, resposta.getPeriodo());

     verify(habitoRepository).save(any(Habito.class));
    

   }

   @Test
   void criarHabitoUsuarioInexistente() {
      when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(RecursoNaoEncontradoException.class, () -> habitoService.criarHabito(new HabitoRequestDTO()));

   }
   @Test
   void atualizarAtributoHabito() {
      Usuario usuario = new Usuario();
      usuario.setId(1L);

      Habito habito = new Habito();
      habito.setId(1L);
      habito.setTitulo("Estudar java");
      habito.setUnidade("horas");
      habito.setQuantidade(3);
      habito.setPeriodo(Periodo.DIARIO);
      habito.setUsuario(usuario);

      when(habitoRepository.findById(1L)).thenReturn(Optional.of(habito));

      HabitoPatchDTO habitoResposta = new HabitoPatchDTO();
      habitoResposta.setTitulo("Estudar java e frameworks");

      HabitoResponseDTO resposta = habitoService.atualizarAtributoHabito(1L, habitoResposta);
      assertEquals("Estudar java e frameworks", resposta.getTitulo());

      assertEquals("horas", resposta.getUnidade());
      assertEquals(3, resposta.getQuantidade());
      assertEquals(Periodo.DIARIO, resposta.getPeriodo());
   }
   @Test
   void apagarHabito(){
      Usuario usuario = new Usuario();
      usuario.setId(1L);

      Habito habito = new Habito();
      habito.setId(1L);
      habito.setUsuario(usuario);

      when(habitoRepository.findById(1L)).thenReturn(Optional.of(habito));
      habitoService.apagarHabito(1L);

      verify(habitoRepository).deleteById(1L);
   }
}
