package com.medical.plan.demo.Tools;

import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Utils {
    private static Jedis jedis = new Jedis("127.0.0.1",6379);

    public static JSONObject getSchema(String schemaName) {
        JSONObject schema = null;
        String schema_src = "src/main/java/com/medical/plan/demo/Repository/JsonSchema/"+schemaName+".json";
        try {
            InputStreamReader standard = new InputStreamReader(new FileInputStream(new File(schema_src)));

            schema = new JSONObject(new JSONTokener(standard));
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return schema;
    }

    public static String validate(String schemaName,Object payload) {
        String schema_src = "src/main/java/com/medical/plan/demo/Repository/JsonSchema/"+schemaName+".json";
        try {
            InputStreamReader standard = new InputStreamReader(new FileInputStream(new File(schema_src)));

            JSONObject Schema = new JSONObject(new JSONTokener(standard));
            JSONObject data = new JSONObject((Map)payload);
            System.out.println(data.toString());
            org.everit.json.schema.Schema schema = SchemaLoader.load(Schema);
            schema.validate(data);

        }catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return "success, the objectId is " + ((Map) payload).get("objectId") ;
    }

    public static Map convertStrToMap(String jsonStr) {
        
        JSONParser parser = new JSONParser();
        Map jsonObject = null;
        try {
            jsonObject = (Map)parser.parse(jsonStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
    
    public static void main(String[] args) {
        //Map val = Utils.convertStrToMap("{\"planCostShares\": { \"deductible\": 2000, \"_org\": \"example.com\", \"copay\": 23, \"objectId\": \"1234vxc2324sdf-501\", \"objectType\": \"membercostshare\" }, \"linkedPlanServices\": [{ \"linkedService\": { \"_org\": \"example.com\", \"objectId\": \"1234520xvc30asdf-502\", \"objectType\": \"service\", \"name\": \"Yearly physical\" }, \"planserviceCostShares\": { \"deductible\": 10, \"_org\": \"example.com\", \"copay\": 0, \"objectId\": \"1234512xvc1314asdfs-503\", \"objectType\": \"membercostshare\" }, \"_org\": \"example.com\", \"objectId\": \"27283xvx9asdff-504\", \"objectType\": \"planservice\" }, { \"linkedService\": { \"_org\": \"example.com\", \"objectId\": \"1234520xvc30sfs-505\", \"objectType\": \"service\", \"name\": \"well baby\" }, \"planserviceCostShares\": { \"deductible\": 10, \"_org\": \"example.com\", \"copay\": 175, \"objectId\": \"1234512xvc1314sdfsd-506\", \"objectType\": \"membercostshare\" }, \"_org\": \"example.com\", \"objectId\": \"27283xvx9sdf-507\", \"objectType\": \"planservice\" } ], \"_org\": \"example.com\", \"objectId\": \"12xvxc345ssdsds-508\", \"objectType\": \"plan\", \"planType\": \"inNetwork\", \"creationDate\": \"12-12-2017\" }");
        //Utils.validate("plan_schema", val);
        //Utils.test();
        SchemaTest();
    }

    public static void test() {
        String input = "{"+
                "\"planCostShares\": {"+
                "\"deductible\": 3000,"+
                "\"_org\": \"example.com\","+
                "\"copay\": 23,"+
                "\"objectId\": \"1234vxc2324sdf-501\","+
                "\"objectType\": \"membercostshare\""+
                "},"+
                "\"_org\": \"example.com\","+
                "\"objectId\": \"12xvxc345ssdsds-508\","+
                "\"objectType\": \"plan\","+
                "\"planType\": \"inNetwork\","+
                "\"creationDate\": \"12-12-2017\""+
                "}";

        Map jsonObject = convertStrToMap(input);

        //createIndex(jsonObject, "");
        patch(convertStrToMap(input));
    }

    public static void createIndex(Map map, String prefix) {
        Map<String, String> pair = new HashMap<String, String>();
        prefix += (prefix.isEmpty()?"":"_") + getIndex(map);
        for(Object key : map.keySet()) {
            Object value = map.get(key);
            if(value instanceof List) {
                saveObjectJson(prefix +"_"+ (String)key, (List)value);
                //pair.put((String)key,  prefix +"_"+ (String)key);
            } else if(value instanceof Map){
                System.out.println(value);
            }else {
                pair.put((String)key, String.valueOf(value));
            }
        }

        saveSimpleJson(pair);
    }

    private static void saveObjectJson(String key, List list) {
        for(Object object:list) {
            createIndex((Map)object, "");
            jedis.sadd(key, getIndex((Map)object));
        }
    }

    private static void saveSimpleJson(Map<String, String> map) {
        jedis.hmset(getIndex(map), map);
    }

    public static String getIndex(Map<String, String> map) {
        return map.get("objectType") + "_" + map.get("objectId");
    }

    private static Map<String, Object> findById(String id, JSONObject schema) {
        Map<String, String> simpleProperties = jedis.hgetAll(id);

        Map<String, Object> res = new HashMap<String, Object>();

        for(String key : simpleProperties.keySet()) {
            res.put(key, simpleProperties.get(key));
        }

        List<String> objectPropertyKeyList = getAllObjectProperties(schema, simpleProperties.get("objectType"));

        for(String objectPropertyKey : objectPropertyKeyList) {
            List<Object> list = findSourceNode(getIndex(simpleProperties) + "_" +objectPropertyKey, schema);
            if(!list.isEmpty());
            res.put(objectPropertyKey, list);
        }

        return res;
    }

    private static List<Object> findSourceNode(String edgeKey, JSONObject schema) {
        Set<String> list = jedis.smembers(edgeKey);
        List<Object> res = new ArrayList<Object>();

        for(String sourceNode : list) {
            res.add(findById(sourceNode, schema));
        }

        return res;
    }

    public static void SchemaTest() {
        String schema_src = "src/main/java/com/medical/plan/demo/Repository/JsonSchema/plan_schema2.json";
        try {
            InputStreamReader standard = new InputStreamReader(new FileInputStream(new File(schema_src)));

            JSONObject schema = new JSONObject(new JSONTokener(standard));
            System.out.println(findById("plan_12xvxc345ssdsds-508", schema));
            delete("plan_12xvxc345ssdsds-508", schema);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static List<String> getAllObjectProperties(JSONObject Schema, String objectType) {
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

    public static void patch(Map map) {
        String index = getIndex(map);
        for(Object key : map.keySet()) {
            Object value = map.get(key);

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

    public static void delete(String id, JSONObject schema) {
        Map<String, String> simpleProperties = jedis.hgetAll(id);

        List<String> objectPropertyKeyList = getAllObjectProperties(schema, simpleProperties.get("objectType"));

        for(String objectPropertyKey : objectPropertyKeyList) {
            List<Object> list = findSourceNode(getIndex(simpleProperties) + "_" +objectPropertyKey, schema);
            for(Object sourceNode : list) {
                delete(getIndex((Map)sourceNode), schema);
            }
            jedis.del(getIndex(simpleProperties) + "_" +objectPropertyKey);
        }

        jedis.del(id);
    }
}
