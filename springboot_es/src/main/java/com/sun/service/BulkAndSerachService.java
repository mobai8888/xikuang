package com.sun.service;

import com.alibaba.fastjson.JSON;
import com.sun.pojo.goods;
import com.sun.utils.JsoupUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: springboot_es
 * @description: 批量插入es数据
 * @author: Mr.lk
 * @create: 2020-05-22 23:06
 **/
@Service
public class BulkAndSerachService {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    //将批量数据存储到es中
    public Boolean BulkGoods(String keywords) throws IOException {
        List<goods> goodsList = JsoupUtils.getTargetGoods(keywords);
        BulkRequest request = new BulkRequest();
        request.timeout("10s");
        goodsList.stream().forEach(good->request.add(
            new IndexRequest("j_goods").source(JSON.toJSONString(good), XContentType.JSON)
                )
        );

        BulkResponse bulk = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }
    //查询并对查找字段高亮
    public List<Map<String, Object>> SearchGoods(String keyword, Integer pageNum, Integer pageSize) throws IOException {
        if(pageNum<1){
            pageNum=1;
        }
        SearchRequest searchRequest = new SearchRequest("j_goods");
        //设置搜索
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置高亮的条件
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        //搜索的条件-设置s高亮
        searchSourceBuilder.highlighter(highlightBuilder);
        searchSourceBuilder.from(pageNum);
        searchSourceBuilder.size(pageSize);
        //设置搜索的类型
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("name", keyword);
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //将构建的条件加入到请求中
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //设置数据存储集合
        List<Map<String, Object>> mapList=new ArrayList<>();
        Arrays.stream(searchResponse.getHits().getHits()).forEach(
                (s)->{
                    Map<String, HighlightField> highlightFields = s.getHighlightFields();
                    //得到高亮字段的对象
                    HighlightField name = highlightFields.get("name");
                    Map<String, Object> sourceAsMap = s.getSourceAsMap();
                    if(Objects.nonNull(name)){
                        Text[] fragments = name.fragments();
                        String title="";
                        for (Text fragment : fragments) {
                            title+=fragment;
                        }
                        sourceAsMap.put("name",title);
                    }
                    mapList.add(sourceAsMap);
                }
        );

        return mapList;
    }


}
