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

        if(simpleProperties == null || simpleProperties.size() == 0) {
            return null;
        }

        List<String> listPropertyKeyList = getAllListProperties(schema, simpleProperties.get("objectType"));
        List<String> objectPropertyKeyList = getAllObjectProperties(schema, simpleProperties.get("objectType"));
        Map<String, String> simplePropertyType = getAllSimpleProperties(schema, simpleProperties.get("objectType"));

        for(String key : simpleProperties.keySet()) {
            if(simplePropertyType.get(key).equals("integer")) {
                res.put(key, Integer.valueOf(simpleProperties.get(key)));
            } else {
                res.put(key, simpleProperties.get(key));
            }

        }

        for(String objectPropertyKey : listPropertyKeyList) {
            List<Object> list = findSourceNode(Utils.getIndex(simpleProperties) + "_" +objectPropertyKey);
            if(!list.isEmpty()){
                res.put(objectPropertyKey, list);
            }
        }

        //Because the return list of the object property only have one element
        for(String objectPropertyKey : objectPropertyKeyList) {
            List<Object> list = findSourceNode(Utils.getIndex(simpleProperties) + "_" +objectPropertyKey);
            if(!list.isEmpty()){
                res.put(objectPropertyKey, list.get(0));
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

    private  List<String> getAllListProperties(JSONObject Schema, String objectType) {
        JSONObject properties = (JSONObject)((JSONObject)((JSONObject)((JSONObject)Schema.get("definitions")).get(objectType)).get("properties"));
        List<String> res = new ArrayList<String>();
        for(String key : properties.keySet()) {
            JSONObject property = (JSONObject) properties.get(key);
            if(property.has("type") && property.get("type").equals("array")) {
                res.add(key);
            }
        }

        return res;
    }

    private  Map<String, String> getAllSimpleProperties(JSONObject Schema, String objectType) {
        JSONObject properties = (JSONObject)((JSONObject)((JSONObject)((JSONObject)Schema.get("definitions")).get(objectType)).get("properties"));
        Map<String, String> res = new HashMap<String, String>();
        for(String key : properties.keySet()) {
            JSONObject property = (JSONObject) properties.get(key);
            if(property.has("type") && !property.get("type").equals("array") && !property.get("type").equals("object")) {
                res.put(key, (String)property.get("type"));
            }
        }

        return res;
    }

    private  List<String> getAllObjectProperties(JSONObject Schema, String objectType) {
        JSONObject properties = (JSONObject)((JSONObject)((JSONObject)((JSONObject)Schema.get("definitions")).get(objectType)).get("properties"));
        List<String> res = new ArrayList<String>();
        for(String key : properties.keySet()) {
            JSONObject property = (JSONObject) properties.get(key);
            if(!property.has("type") || property.get("type").equals("object")) {
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
        Utils.enQueue(plan);
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
        List<String> listPropertyKeyList = getAllListProperties(schema, simpleProperties.get("objectType"));
        listPropertyKeyList.addAll(getAllObjectProperties(schema, simpleProperties.get("objectType")));

        for(String objectPropertyKey : listPropertyKeyList) {
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

        Utils.enQueue(findById(index));
    }

    @Override
    public void patch(Map source, Map target) {
        Map plan = patchHelper(source, target, new HashMap<>());
        save(plan);
    }

    private Map patchHelper(Map source, Map target, Map map) {
        if( (source == null && target == null) || (source.size() == 0 && target.size() == 0)) {
            return map;
        }

        Set<Object> keySet = new HashSet<Object>();

        keySet.addAll(source.keySet());
        keySet.addAll(target.keySet());

        for(Object key : keySet) {
            System.out.println(String.valueOf(key));
            if(!target.containsKey(key)) {
                map.put(key, source.get(key));
            } else if(!source.containsKey(key)){
                map.put(key, target.get(key));
            } else if(target.get(key) instanceof Map){
                if(target.get(key) instanceof  Map) {
                    map.put(key, new HashMap<>());
                    patchHelper((Map)source.get(key), (Map)target.get(key), (Map)map.get(key));
                } else {
                    map.put(key, source.get(key));
                }
            } else if(target.get(key) instanceof List) {
                List<Object> list = new ArrayList<>();
                map.put(key, list);

                Map sourceMap = new HashMap<>();
                Map targetMap = new HashMap<>();
                Map mergeMap = new HashMap<>();
                for(Object object : (List)target.get(key)) {
                    String index = Utils.getIndex((Map)object);
                    targetMap.put(index, object);
                }

                for(Object object : (List)source.get(key)) {
                    String index = Utils.getIndex((Map)object);
                    sourceMap.put(index, object);
                }

                for(Object sourceKey : sourceMap.keySet()) {
                    if(targetMap.containsKey(sourceKey)) {
                        mergeMap.put(sourceKey, patchHelper((Map)sourceMap.get(sourceKey), (Map)targetMap.get(sourceKey), new HashMap<>()));
                    } else {
                        mergeMap.put(sourceKey, sourceMap.get(sourceKey));
                    }
                }

                for(Object targetKey : targetMap.keySet()) {
                    if(!mergeMap.containsKey(targetKey)) {
                        mergeMap.put(targetKey, targetMap.get(targetKey));
                    }
                }

                list.addAll(mergeMap.values());
            } else {
                map.put(key, source.get(key));
            }
        }

        return map;
    }

    @Override
    public void put(Map plan) {
        removeOne(Utils.getIndex(plan));
        save(plan);
    }

}
