package com.example.prj3be.controller;

import com.example.prj3be.dto.CartInfoDto;
import com.example.prj3be.dto.CartItemDto;
//import com.example.prj3be.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
//    private final CartService cartService;
//
//    @PostMapping(value="/cart")
//    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
//                                              BindingResult bindingResult, Principal principal) {
//        if(bindingResult.hasErrors()) {
//            StringBuilder sb = new StringBuilder();
//            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//            for(FieldError fieldError : fieldErrors) {
//                sb.append(fieldError.getDefaultMessage());
//            }
//            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
//        }
//
//        String email = principal.getName();
//        Long cartItemId;
//
//        try {
//            cartItemId = cartService.addCart(cartItemDto, email);
//        } catch(Exception e) {
//            return new ResponseEntity<String> (e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
//    }
//
//    //TODO : 회원 닉네임이 email이란 가정 하에 작성한 코드, CartController, getCartList 수정할 것
//    @GetMapping(value = "/cart")
//    public ResponseEntity<List<CartInfoDto>> orderHistory(Principal principal) {
//        List<CartInfoDto> cartInfoDtoList = cartService.getCartList(principal.getName());
//        return ResponseEntity.ok(cartInfoDtoList);
//    }
}
