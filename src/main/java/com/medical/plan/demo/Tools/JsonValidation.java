package com.medical.plan.demo.Tools;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.util.Map;


public class JsonValidation {

    public static String validate(String schemaName,Object payload) {
        String schema_src = "src/main/java/com/medical/plan/demo/Repository/JsonSchema/"+schemaName+".json";
        try {
            InputStreamReader standard = new InputStreamReader(new FileInputStream(new File(schema_src)));

            JSONObject Schema = new JSONObject(new JSONTokener(standard));
            JSONObject data = new JSONObject(payload);
            System.out.println(data.toString());
            Schema schema = SchemaLoader.load(Schema);
            schema.validate(data);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return "success";
    }


    public static void main(final String[] args) throws IOException {
        //Plan plan = new Plan("example","2019-03-20","plan","costservice");
        //JsonValidation v = new JsonValidation();
        //System.out.println(v.validate("plan_schema", plan));

        JsonValidation.convert("{\"first\": 123, \"second\": {\"test\":\"123\"}, \"third\": 789}");
    }

    public static void convert(String jsonStr) {
        JSONParser parser = new JSONParser();
        Map jsonObject = null;
        try {
            jsonObject = (Map)parser.parse(jsonStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //return jsonObject;
        System.out.println(((Map)jsonObject.get("second")).get("test"));
    }

}
