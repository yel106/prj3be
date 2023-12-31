package com.example.prj3be.dto;

import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor
public class SocialUser {
   public Object id; //네이버는 String, 카카오는 Long
   public String email;
   public String name;
}
