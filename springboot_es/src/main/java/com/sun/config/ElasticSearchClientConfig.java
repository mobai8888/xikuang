package com.sun.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: springboot_es
 * @description: 新建ElasticSearchClientConfig配置类
 * @author: Mr.lk
 * @create: 2020-05-21 15:15
 **/
@Configuration
public class ElasticSearchClientConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient rest=new RestHighLevelClient(RestClient.builder(
                new HttpHost("192.168.93.135",9200,"http")
        ));
        return rest;
    }

}
