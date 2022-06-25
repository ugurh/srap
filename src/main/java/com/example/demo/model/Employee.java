package com.example.demo.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema ="DM", name = "EMPLOYEE")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Employee  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String gender;
    private String address;
}
