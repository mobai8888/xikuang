package com.sun.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: springboot_es
 * @description:
 * @author: Mr.lk
 * @create: 2020-05-22 23:42
 **/
@RequestMapping
public class systemController {

    @GetMapping({"/","/index"})
    public String index(){
        return "index";
    }
}
