package com.example.prj3be.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SocialUser {
   public Object id; //네이버는 String, 카카오는 Long
   public String email;
   public boolean verifiedEmail;
   public String name;
   public String givenName;
   public String familyName;
   public String picture;
   public String locale;
}
