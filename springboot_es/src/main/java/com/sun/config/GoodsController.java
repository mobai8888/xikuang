package com.sun.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: springboot_es
 * @description:
 * @author: Mr.lk
 * @create: 2020-05-22 22:27
 **/
@Controller
public class GoodsController {

    @RequestMapping("/")
    public String dd(){
        return "index";
    }
}
