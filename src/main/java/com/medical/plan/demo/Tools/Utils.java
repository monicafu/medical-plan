package com.medical.plan.demo.Tools;

import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class Utils {

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
        Map val = Utils.convertStrToMap("{\"planCostShares\": { \"deductible\": 2000, \"_org\": \"example.com\", \"copay\": 23, \"objectId\": \"1234vxc2324sdf-501\", \"objectType\": \"membercostshare\" }, \"linkedPlanServices\": [{ \"linkedService\": { \"_org\": \"example.com\", \"objectId\": \"1234520xvc30asdf-502\", \"objectType\": \"service\", \"name\": \"Yearly physical\" }, \"planserviceCostShares\": { \"deductible\": 10, \"_org\": \"example.com\", \"copay\": 0, \"objectId\": \"1234512xvc1314asdfs-503\", \"objectType\": \"membercostshare\" }, \"_org\": \"example.com\", \"objectId\": \"27283xvx9asdff-504\", \"objectType\": \"planservice\" }, { \"linkedService\": { \"_org\": \"example.com\", \"objectId\": \"1234520xvc30sfs-505\", \"objectType\": \"service\", \"name\": \"well baby\" }, \"planserviceCostShares\": { \"deductible\": 10, \"_org\": \"example.com\", \"copay\": 175, \"objectId\": \"1234512xvc1314sdfsd-506\", \"objectType\": \"membercostshare\" }, \"_org\": \"example.com\", \"objectId\": \"27283xvx9sdf-507\", \"objectType\": \"planservice\" } ], \"_org\": \"example.com\", \"objectId\": \"12xvxc345ssdsds-508\", \"objectType\": \"plan\", \"planType\": \"inNetwork\", \"creationDate\": \"12-12-2017\" }");
        Utils.validate("plan_schema", val);
    }
}
