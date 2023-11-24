package com.example.prj3be.domain;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;

@Entity
@Inheritance()
@DiscriminatorColumn(name="DTYPE")
public class Item {

}
