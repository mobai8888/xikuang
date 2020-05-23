package com.sun.controller;

import com.sun.service.BulkAndSerachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @program: springboot_es
 * @description:
 * @author: Mr.lk
 * @create: 2020-05-22 23:31
 **/
@RestController
public class JdController {

    @Autowired
    BulkAndSerachService bulkAndSerachService;

    //转换数据插入es
    @GetMapping("/parse/{keyword}")
    public boolean BulkIntoEs(@PathVariable("keyword")String keyword) throws IOException {
        return bulkAndSerachService.BulkGoods(keyword);
    }
    //高亮搜索
    @GetMapping("/search/{keyword}/{pageNum}/{pageSize}")
    public List<Map<String, Object>> SearchGoods(
            @PathVariable("keyword")String keyword,
            @PathVariable("pageNum")Integer pageNum,
            @PathVariable("pageSize")Integer pageSize
    ) throws IOException {
        return bulkAndSerachService.SearchGoods(keyword,pageNum,pageSize);
    }
}
