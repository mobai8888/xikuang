package com.sun;


import com.alibaba.fastjson.JSON;
import com.sun.pojo.User;
import org.apache.el.stream.Stream;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.client.Requests.searchRequest;

@SpringBootTest
class SpringbootEsApplicationTests {
	//测试crud操作

	@Autowired
	@Qualifier("restHighLevelClient")
	RestHighLevelClient client;

	// 测试索引的创建 Request PUT kuang_index
	@Test
	void testCreateIndex() throws IOException {
		// 1、创建索引请求
		CreateIndexRequest request = new CreateIndexRequest("j_goods");
		//设置等待所有节点确认的超时时间
		request.setTimeout(TimeValue.timeValueSeconds(30));
		// 2、客户端执行请求 IndicesClient,请求后获得响应
		CreateIndexResponse createresponse = client.indices().create(request, RequestOptions.DEFAULT);
		System.out.println(createresponse);
	}
	// 测试获取索引,判断其是否存在
	@Test
	void testExistIndex() throws IOException {
		GetIndexRequest request = new GetIndexRequest("j_goods");
		boolean exists = client.indices().exists(request,RequestOptions.DEFAULT);
		System.out.println("是否存在索引:"+exists);
	}
	// 测试删除索引
	@Test
	void deleteIndex() throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest("j_goods");
		AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
		System.out.println("是否删除:"+delete.isAcknowledged());
	}
    // 测试添加文档
    @Test
    void testAddDocument() throws IOException {
        // 创建对象
		User user=new User("张晓峰",18);
        // 创建请求
		IndexRequest request = new IndexRequest("kuangshen_index");
		// 规则 put /kuang_index/_doc/1
		request.id("1");
		request.timeout(TimeValue.timeValueSeconds(5));
        // 将我们的数据放入请求 json
		 request.source(JSON.toJSONString(user), XContentType.JSON);
		// 客户端发送请求 , 获取响应的结果
		IndexResponse index = client.index(request, RequestOptions.DEFAULT);
		System.out.println(index);
		System.out.println("响应的结果:"+index.toString());
		// 对应我们命令返回的状态CREATED
		System.out.println("响应的结果:"+index.status());
	}
	// 获取文档，判断是否存在 get /index/doc/1
	@Test
	void testIsExists() throws IOException {
		GetRequest getRequest = new GetRequest("kuangshen_index", "1");
		// 不获取返回的 _source 的上下文了
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");
		boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
		System.out.println("文档是否存在:"+exists);
	}
	// 获得文档的信息
	@Test
	void testGetDocument() throws IOException {
		GetRequest getRequest = new GetRequest("kuangshen_index","1");
		GetResponse documentFields = client.get(getRequest, RequestOptions.DEFAULT);
		System.out.println("获取结果:"+documentFields.getSourceAsString());
		/*System.out.println(documentFields);*/
	}
	// 更新文档的信息
	@Test
	void testUpdateRequest() throws IOException {
		UpdateRequest updateRequest = new UpdateRequest("kuangshen_index","1");
		updateRequest.timeout("1s");
		User user = new User("詹三3", 18);
		updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
		UpdateResponse updateResponse = client.update(updateRequest,
				RequestOptions.DEFAULT);
		System.out.println("更新:"+updateResponse);
		System.out.println("更新结果:"+updateResponse.status());
	}
	// 删除文档记录
	@Test
	void testDeleteRequest() throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest("kuangshen_index", "1");
		deleteRequest.timeout(TimeValue.timeValueSeconds(1));
		DeleteResponse delete = client.delete(deleteRequest, RequestOptions.DEFAULT);
		System.out.println("删除:"+delete.status());
	}
	// 特殊的，真的项目一般都会批量插入数据！
	@Test
	void testBulkRequest() throws IOException {

		BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.timeout(TimeValue.timeValueSeconds(1));
		User user0 = new User("zhangsan1", 18);
		User user1 = new User("zhangsan2", 19);
		User user2 = new User("zhangsan3", 18);
		User user3 = new User("zhangsan4", 20);
		User user4 = new User("zhangsan5", 18);
		User user5 = new User("zhangsan6", 21);
		User user6 = new User("zhangsan7", 22);
		User user7 = new User("zhangsan8", 21);
		List<User> users = Arrays.asList(user0, user1, user2, user3, user4, user5, user6, user7);
		// 批处理请求
		users.stream().forEach(
				// 批量更新和批量删除，就在这里修改对应的请求就可以了
				user->bulkRequest.add(
						new IndexRequest("kuangshen_index")
						.id(user.getName())
						.source(JSON.toJSONString(user),XContentType.JSON)
				)
		);

			BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
			// 是否失败，返回 false 代表成功！
			System.out.println(bulk.hasFailures());
		/*users.stream().forEach(
				user->bulkRequest.add(
						// 批量更新和批量删除，就在这里修改对应的请求就可以了
						new DeleteRequest("kuangshen_index")
								.id(user.getName())
				)
		);

		BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
		// 是否失败，返回 false 代表成功！
		System.out.println(bulk.hasFailures());*/
		// 批处理请求
		/*for (int i = 0; i < users.size(); i++) {
		// 批量更新和批量删除，就在这里修改对应的请求就可以了
			bulkRequest.add(
					new IndexRequest("kuangshen_index")
							.id("" + (i + 1))
							.source(JSON.toJSONString(users.get(i)), XContentType.JSON));
		}
		BulkResponse bulkResponse = client.bulk(bulkRequest,
				RequestOptions.DEFAULT);
		System.out.println(bulkResponse.hasFailures()); // 是否失败，返回 false 代表成功！*/
	}
	// 查询
	// SearchRequest 搜索请求
	// SearchSourceBuilder 条件构造
	// HighlightBuilder 构建高亮
	// TermQueryBuilder 精确查询
	// MatchAllQueryBuilder
	// xxx QueryBuilder 对应我们刚才看到的命令！
	@Test
	void testSearch() throws IOException {
		SearchRequest searchRequest = new SearchRequest("kuangshen_index");
		//searchAll(searchRequest); //查询所有
		termQuery(searchRequest);//精确查询
	}
	//查询所有
	public void searchAll(SearchRequest searchRequest) throws IOException {
		// 构建搜索条件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.highlighter();//构建高亮
		// 查询条件，我们可以使用 QueryBuilders 工具来实现
		QueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();// 匹配所有
		//将条件加入构建中
		sourceBuilder.query(matchAllQueryBuilder);
		//将条件的构建放到请求中
		searchRequest.source(sourceBuilder);
		SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
		Arrays.stream(search.getHits().getHits()).forEach(s->System.out.println(s.getSourceAsMap()));
	}
	// 精确查询
	@Test
	public void termQuery(SearchRequest searchRequest) throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.highlighter();
		QueryBuilder term = QueryBuilders.termQuery("name", "zhangsan3");
		sourceBuilder.query(term);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchRequest.source(sourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		System.out.println("精确查询：" + JSON.toJSONString(searchResponse.getHits()));
		Arrays.stream(searchResponse.getHits().getHits()).forEach(s->System.out.println(s.getSourceAsMap()));
	}

}
