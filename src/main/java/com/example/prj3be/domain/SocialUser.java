package com.example.prj3be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocialUser {
   public String id;
   public String email;
   public boolean vertifiedEmail;
   public String name;
   public String givenName;
   public String familyName;
   public String picture;
   public String locale;
}
