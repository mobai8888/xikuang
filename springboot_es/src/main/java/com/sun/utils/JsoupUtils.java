package com.sun.utils;

import com.sun.pojo.goods;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: springboot_es
 * @description: 爬虫工具
 * @author: Mr.lk
 * @create: 2020-05-22 21:43
 **/
public class JsoupUtils {
   /* public static void main(String[] args) throws IOException {
        List<goods> list=getTargetGoods("java");
        list.stream().forEach((s)->{System.out.println(s);});
    }*/
    //获取爬取的数据
    public static List<goods> getTargetGoods(String keywords) throws IOException {
        String url="https://search.jd.com/Search?keyword="+keywords;
        //获取节点对象
        Document document = Jsoup.parse(new URL(url),3000);
        //获取网站地址数据所在的父节点
        Element list = document.getElementById("J_goodsList");
        /*System.out.println(list.html());*/
        //获取网站地址数据所在的节点
        Elements li = list.getElementsByTag("li");
        //创建集合存储数据
        List<goods> goods = new ArrayList<>();
        li.stream().forEach((element) -> {
            String image = element.getElementsByTag("img").eq(0).attr("src");
            String name = element.getElementsByClass("p-name").eq(0).text();
            String price = element.getElementsByClass("p-price").eq(0).text();
            goods g = new goods();
            g.setImg(image);
            g.setName(name);
            g.setPrice(price);
            goods.add(g);
        });
        return goods;
    }

}
