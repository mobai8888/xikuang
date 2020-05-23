package com.sun.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: springboot_es
 * @description:
 * @author: Mr.lk
 * @create: 2020-05-21 15:30
 **/
@Data
@AllArgsConstructor
@ToString
public class User {
    String name;
    int age;
}
