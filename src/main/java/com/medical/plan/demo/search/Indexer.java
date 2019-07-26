package com.medical.plan.demo.search;

import com.medical.plan.demo.Tools.Utils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Map;

public class Indexer {

    private RestHighLevelClient client;
    private Jedis jedis = new Jedis("127.0.0.1",6379);

    public Indexer(){
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
    }

    public void process() {
        while(true) {
            String plan = jedis.brpoplpush("source", "destination", 0);
            try {
                putInES(Utils.convertStrToMap(plan));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private IndexResponse putInES(Map plan) throws IOException {
        String id = Utils.getIndex(plan);
        System.out.println(plan.toString());
        IndexRequest indexRequest = new IndexRequest("planindex").type("plans")
                .id(id).source(plan);
        return client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public static void main(String[] args) {
        Indexer indexer = new Indexer();
        indexer.process();
    }
}


