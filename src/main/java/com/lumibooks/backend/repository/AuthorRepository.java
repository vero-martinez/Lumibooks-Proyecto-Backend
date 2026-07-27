package com.lumibooks.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {

    /**
     * Verifica si existe un autor por nombre y apellido ignorando mayúsculas y minúsculas. 
     * @param firstName nombre del autor a verificar
     * @param lastName apellido del autor a verificar
     * @return true si existe un autor con ese nombre y apellido, false si no existe
     */
    boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

    List<Author> findByIsActiveTrue();

}