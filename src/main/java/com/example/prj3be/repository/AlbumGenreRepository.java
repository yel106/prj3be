package com.example.prj3be.repository;

import com.example.prj3be.domain.AlbumGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumGenreRepository extends JpaRepository<AlbumGenre, Long>, AlbumGenreRepositoryCustom {

}
