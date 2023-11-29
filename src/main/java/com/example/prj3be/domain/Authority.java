package com.example.prj3be.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="authority")
@Data
public class Authority {
    @Id
    @Column(name="authority_name", length=50)
    private String authorityName;
}
