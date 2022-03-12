package com.zipzoong.querydsl.contraller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloContraller {
    @GetMapping("/hello")
    public String hello(){
        return "hello";

    }

    @GetMapping("hi")
    public String hi(){
        return "hi yo!!";
    }
}
