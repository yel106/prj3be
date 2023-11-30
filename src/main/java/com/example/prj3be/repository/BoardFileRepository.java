package com.example.prj3be.repository;

import com.example.prj3be.domain.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {

    @Query("SELECT bf FROM BoardFile bf WHERE bf.id = :boardFile_id")
    BoardFile selectById(Long boardFile_id);

}