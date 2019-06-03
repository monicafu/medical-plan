package com.medical.plan.demo.Repository;

import com.medical.plan.demo.model.Plan;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;


import java.io.*;


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
        Plan plan = new Plan("example","2019-03-20","plan","costservice");
        JsonValidation v = new JsonValidation();
        System.out.println(v.validate("plan_schema", plan));
    }

}
