package com.example.dish.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Tai khoan dang nhap. role = "USER" hoac "ADMIN" (Spring Security se them tien to ROLE_). */
@Entity
@Table(name = "BIT240024_Users")
@Getter
@Setter
public class User_BIT240024 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BIT240024_Id")
    private Long id;

    @Column(name = "BIT240024_Username", nullable = false, unique = true, length = 50)
    private String username;

    /** Mat khau da bam BCrypt (khong bao gio luu plain text). */
    @Column(name = "BIT240024_Password", nullable = false, length = 100)
    private String password;

    @Column(name = "BIT240024_Role", nullable = false, length = 20)
    private String role;
}
