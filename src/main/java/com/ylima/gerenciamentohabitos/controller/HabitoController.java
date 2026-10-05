package com.ylima.gerenciamentohabitos.controller;


import com.ylima.gerenciamentohabitos.dto.HabitoRequestDTO;
import com.ylima.gerenciamentohabitos.dto.HabitoResponseDTO;
import com.ylima.gerenciamentohabitos.dto.HabitoUpdateDTO;
import com.ylima.gerenciamentohabitos.entity.Periodo;
import com.ylima.gerenciamentohabitos.service.HabitoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/habitos")
public class HabitoController {
    private HabitoService habitoService;
    public HabitoController(HabitoService habitoService) {
        this.habitoService = habitoService;

    }
   @PostMapping
    public ResponseEntity<HabitoResponseDTO> save(@Valid @RequestBody HabitoRequestDTO habitoRequestDTO){

       HabitoResponseDTO habitoCriado = habitoService.criarHabito(habitoRequestDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(habitoCriado);
       }

       @GetMapping("/{id}")
        public HabitoResponseDTO buscar(@PathVariable Long id){
        return habitoService.buscarHabito(id);
       }

       @PutMapping("/{id}")
        public HabitoResponseDTO atualizarHabito(@PathVariable Long id, @Valid @RequestBody HabitoUpdateDTO habitoUpdateDTO){
            return habitoService.atualizarHabito(id, habitoUpdateDTO);
       }

       @DeleteMapping("/{id}")
        public ResponseEntity<Void> removerHabito(@PathVariable Long id){
        habitoService.apagarHabito(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
       }

       @GetMapping
    public Page<HabitoResponseDTO> listarPorUsuario(@RequestParam (required = false)Periodo periodo, Pageable pageable){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       Long usuarioId = (Long) auth.getPrincipal();
        if(periodo == null){
          return habitoService.listarHabitosPorUsuario(usuarioId,  pageable);
        }else {
           return habitoService.listarHabitosPorUsuarioEPeriodo(usuarioId,periodo,pageable);
        }

       }


   }

