package com.gfg.ewallet.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Table(name="user")
@Entity
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;

   private String userName;
   private String email;
   private String phoneNumber;
   private String passwordHash;



}
