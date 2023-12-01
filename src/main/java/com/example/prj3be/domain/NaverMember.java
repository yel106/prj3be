package com.example.prj3be.domain;

import lombok.Data;

@Data
public class NaverMember {
    private String resultcode;
    private String message;
    private response response;

    // 공식 API 에서 리턴되는 필드 설명 -> response/id, response/nickname, etc
    //depth 하나가 더 들어가 있으므로 inner class 생성 필요 (orElse error)
    @Data
    public class response {
        private String id;
        private String name;
        private String gender;
        private String birthday;
        private String birthyear;
    }
}
