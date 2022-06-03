package com.example.demo.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(schema ="DM", name = "EMPLOYEE")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String gender;
    private String address;
}
