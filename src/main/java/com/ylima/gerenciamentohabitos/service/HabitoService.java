package com.ylima.gerenciamentohabitos.service;
import com.ylima.gerenciamentohabitos.dto.HabitoRequestDTO;
import com.ylima.gerenciamentohabitos.dto.HabitoResponseDTO;
import com.ylima.gerenciamentohabitos.dto.HabitoUpdateDTO;
import com.ylima.gerenciamentohabitos.entity.Habito;
import com.ylima.gerenciamentohabitos.entity.Periodo;
import com.ylima.gerenciamentohabitos.entity.Usuario;
import com.ylima.gerenciamentohabitos.exception.RecursoNaoEncontradoException;
import com.ylima.gerenciamentohabitos.repository.HabitoRepository;
import com.ylima.gerenciamentohabitos.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class HabitoService {
    private HabitoRepository habitoRepository;
    private UsuarioRepository usuarioRepository;

    public HabitoService(HabitoRepository habitoRepository, UsuarioRepository usuarioRepository) {
        this.habitoRepository = habitoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public HabitoResponseDTO criarHabito(HabitoRequestDTO habitoRequestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioId = (Long) auth.getPrincipal();
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);

        if (usuario.isEmpty()) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado");
        }
        Usuario usuarioEncontrado = usuario.get();
        Habito novoHabito = new Habito();
        novoHabito.setUsuario(usuarioEncontrado);
        novoHabito.setTitulo(habitoRequestDTO.getTitulo());
        novoHabito.setDescricao(habitoRequestDTO.getDescricao());
        novoHabito.setUnidade(habitoRequestDTO.getUnidade());
        novoHabito.setQuantidade(habitoRequestDTO.getQuantidade());
        novoHabito.setPeriodo(habitoRequestDTO.getPeriodo());

        return converterParaResponseDTO(habitoRepository.save(novoHabito));
    }

    public HabitoResponseDTO buscarHabito(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioId = (Long) auth.getPrincipal();

        Optional<Habito> habito = habitoRepository.findById(id);

        if (!habito.isPresent()) {
            throw new RecursoNaoEncontradoException("Hábito não encontrado!");
        }

        Habito habitoEncontrado = habito.get();

        if (!usuarioId.equals(habitoEncontrado.getUsuario().getId())) {
            throw new RecursoNaoEncontradoException("Esse hábito não foi encontrado!");
        }
        return converterParaResponseDTO(habitoEncontrado);
    }

        public HabitoResponseDTO atualizarHabito (Long id, HabitoUpdateDTO habitoUpdateDTO){
            Optional<Habito> habito = habitoRepository.findById(id);
            if (!habito.isPresent()) {
                throw new RecursoNaoEncontradoException("Esse hábito não foi encontrado!");
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long usuarioId = (Long) auth.getPrincipal();

            Habito habitoEncontrado = habito.get();
            if(!usuarioId.equals(habitoEncontrado.getUsuario().getId())) {
                throw new RecursoNaoEncontradoException("Esse hábito não foi encontrado!");
            }

            Habito habitoAtualizado = habito.get();
            habitoAtualizado.setTitulo(habitoUpdateDTO.getTitulo());
            habitoAtualizado.setDescricao(habitoUpdateDTO.getDescricao());
            habitoAtualizado.setUnidade(habitoUpdateDTO.getUnidade());
            habitoAtualizado.setQuantidade(habitoUpdateDTO.getQuantidade());
            habitoAtualizado.setPeriodo(habitoUpdateDTO.getPeriodo());

            habitoRepository.save(habitoAtualizado);
            return converterParaResponseDTO(habitoAtualizado);

        }

        public void apagarHabito (Long id){
            Optional<Habito> habito = habitoRepository.findById(id);
            if (!habito.isPresent()) {
                throw new RecursoNaoEncontradoException("Esse hábito não foi encontrado!");
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long usuarioId = (Long) auth.getPrincipal();

            Habito habitoEncontrado = habito.get();

            if(!usuarioId.equals(habitoEncontrado.getUsuario().getId())) {
                throw new RecursoNaoEncontradoException("Esse hábito não foi encontrado!");
            }

            habitoRepository.deleteById(id);

        }

        public Page<HabitoResponseDTO> listarHabitosPorUsuario (Pageable pageable){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long usuarioId = (Long) auth.getPrincipal();

            Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);

            if (!usuario.isPresent()) {
                throw new RecursoNaoEncontradoException("Esse usuário não existe ou não foi encontrado!");
            }

            Page<Habito> habitos = habitoRepository.findByUsuario_Id(usuarioId, pageable);
            return habitos.map(habito -> converterParaResponseDTO(habito)

            );
        }

        public Page<HabitoResponseDTO> listarHabitosPorUsuarioEPeriodo (Periodo periodo, Pageable pageable){

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long usuarioId = (Long) auth.getPrincipal();

            Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
            if (!usuario.isPresent()) {
                throw new RecursoNaoEncontradoException("Esse usuário não existe ou não foi encontrado!");
            }
            Page<Habito> habitos = habitoRepository.findByUsuario_IdAndPeriodo(usuarioId, periodo, pageable);
            return habitos.map(habito -> converterParaResponseDTO(habito)

            );
        }

        private HabitoResponseDTO converterParaResponseDTO (Habito habito){
            HabitoResponseDTO responseDTO = new HabitoResponseDTO();
            responseDTO.setId(habito.getId());
            responseDTO.setTitulo(habito.getTitulo());
            responseDTO.setDescricao(habito.getDescricao());
            responseDTO.setUnidade(habito.getUnidade());
            responseDTO.setQuantidade(habito.getQuantidade());
            responseDTO.setPeriodo(habito.getPeriodo());
            responseDTO.setUsuarioId(habito.getUsuario().getId());

            return responseDTO;
        }

    }

