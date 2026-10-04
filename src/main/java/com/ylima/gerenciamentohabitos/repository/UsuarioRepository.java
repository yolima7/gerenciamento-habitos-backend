package com.ylima.gerenciamentohabitos.repository;

import com.ylima.gerenciamentohabitos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    public Optional<Usuario> findByEmail(String email);


}
