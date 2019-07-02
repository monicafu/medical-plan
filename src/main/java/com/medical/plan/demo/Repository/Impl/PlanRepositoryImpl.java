package com.medical.plan.demo.Repository.Impl;

import com.medical.plan.demo.Repository.PlanRepository;
import com.medical.plan.demo.Tools.Utils;
import org.json.JSONObject;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.util.*;

@Repository
public class PlanRepositoryImpl implements PlanRepository {

    private RedisTemplate<String,Map> redisTemplate;
    private HashOperations hashOperations;
    private Jedis jedis = new Jedis("127.0.0.1",6379);
    private JSONObject schema = Utils.getSchema("plan_schema2");

    public PlanRepositoryImpl(RedisTemplate<String,Map> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }
    @Override
    public Map<String,Map> findAll() {
        return hashOperations.entries("PLAN");
    }

//    @Override
//    public Map findById(String id) {
//        return (Map)hashOperations.get("PLAN",id);
//    }

    @Override
    public Map findById(String id) {
        Map<String, String> simpleProperties = jedis.hgetAll(id);

        Map<String, Object> res = new HashMap<String, Object>();

        for(String key : simpleProperties.keySet()) {
            res.put(key, simpleProperties.get(key));
        }

        List<String> objectPropertyKeyList = getAllObjectProperties(schema, simpleProperties.get("objectType"));

        for(String objectPropertyKey : objectPropertyKeyList) {
            List<Object> list = findSourceNode(Utils.getIndex(simpleProperties) + "_" +objectPropertyKey);
            if(!list.isEmpty()){
                res.put(objectPropertyKey, list);
            }
        }

        return res;
    }

    private List<Object> findSourceNode(String edgeKey) {
        Set<String> list = jedis.smembers(edgeKey);
        List<Object> res = new ArrayList<Object>();

        for(String sourceNode : list) {
            res.add(findById(sourceNode));
        }

        return res;
    }

    private  List<String> getAllObjectProperties(JSONObject Schema, String objectType) {
        JSONObject properties = (JSONObject)((JSONObject)((JSONObject)((JSONObject)Schema.get("definitions")).get(objectType)).get("properties"));
        List<String> res = new ArrayList<String>();
        for(String key : properties.keySet()) {
            JSONObject property = (JSONObject) properties.get(key);
            if(!property.has("type") || property.get("type").equals("array")) {
                res.add(key);
            }
        }

        return res;
    }

//    @Override
//    public Map save(Map plan) {
//        //jedis.hmset(String.valueOf(plan.get("objectId")), plan);
//        hashOperations.put("PLAN",plan.get("objectId"),plan);
//        return plan;
//    }

    @Override
    public Map save(Map plan) {
        createIndex(plan, "");
        return plan;
    }

    public void createIndex(Map map, String prefix) {
        Map<String, String> pair = new HashMap<String, String>();
        prefix += (prefix.isEmpty()?"":"_") + Utils.getIndex(map);
        for(Object key : map.keySet()) {
            Object value = map.get(key);
            if(value instanceof List) {
                saveObjectJson(prefix +"_"+ (String)key, (List)value);
            } else if(value instanceof  Map){   //process the case when the object property is not a list
                List<Object> list = new ArrayList<Object>();
                list.add(value);
                saveObjectJson(prefix +"_"+ (String)key, list);
            }
            else {
                pair.put((String)key, String.valueOf(value));
            }
        }

        saveSimpleJson(pair);
    }

    private void saveObjectJson(String key, List list) {
        for(Object object:list) {
            createIndex((Map)object, "");
            jedis.sadd(key, Utils.getIndex((Map)object));
        }
    }

    private void saveSimpleJson(Map<String, String> map) {
        jedis.hmset(Utils.getIndex(map), map);
    }

//    @Override
//    public void removeOne(String id) {
//        hashOperations.delete("PLAN",id);
//    }

    @Override
    public void removeOne(String id) {
        Map<String, String> simpleProperties = jedis.hgetAll(id);

        List<String> objectPropertyKeyList = getAllObjectProperties(schema, simpleProperties.get("objectType"));

        for(String objectPropertyKey : objectPropertyKeyList) {
            List<Object> list = findSourceNode(Utils.getIndex(simpleProperties) + "_" +objectPropertyKey);
            for(Object sourceNode : list) {
                removeOne(Utils.getIndex((Map)sourceNode));
            }
            jedis.del(Utils.getIndex(simpleProperties) + "_" +objectPropertyKey);
        }

        jedis.del(id);
    }

    @Override
    public void patch(Map plan) {
        String index = Utils.getIndex(plan);
        for(Object key : plan.keySet()) {
            Object value = plan.get(key);

            if(value instanceof List) {
                for(Object o : (List)value) {
                    patch((Map)o);
                }
            } else if(value instanceof Map) {
                patch((Map)value);
            } else {
                jedis.hset(index, (String)key, String.valueOf(value));
            }
        }
    }

    @Override
    public void put(Map plan) {
        removeOne(Utils.getIndex(plan));
        save(plan);
    }
}
