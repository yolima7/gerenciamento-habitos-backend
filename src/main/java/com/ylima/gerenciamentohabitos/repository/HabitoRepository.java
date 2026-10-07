package com.ylima.gerenciamentohabitos.repository;
import com.ylima.gerenciamentohabitos.entity.Habito;
import com.ylima.gerenciamentohabitos.entity.Periodo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HabitoRepository extends JpaRepository<Habito, Long> {

    Page<Habito> findByUsuario_Id(Long id, Pageable pageable);

    Page<Habito> findByUsuario_IdAndPeriodo(Long id, Periodo periodo, Pageable pageable);

    Page<Habito> findAll(Pageable pageable);


}

