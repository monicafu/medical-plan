package com.medical.plan.demo.Tools;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.*;
import com.nimbusds.jwt.*;
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
    private static RSAKey rsaJWK;

    static {
        try {
            rsaJWK = new RSAKeyGenerator(2048).generate();
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private static RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

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
        return "success, the objectId is " + Utils.getIndex((Map) payload) ;
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
    
    public static void main(String[] args) throws JOSEException {
        //Map val = Utils.convertStrToMap("{\"planCostShares\": { \"deductible\": 2000, \"_org\": \"example.com\", \"copay\": 23, \"objectId\": \"1234vxc2324sdf-501\", \"objectType\": \"membercostshare\" }, \"linkedPlanServices\": [{ \"linkedService\": { \"_org\": \"example.com\", \"objectId\": \"1234520xvc30asdf-502\", \"objectType\": \"service\", \"name\": \"Yearly physical\" }, \"planserviceCostShares\": { \"deductible\": 10, \"_org\": \"example.com\", \"copay\": 0, \"objectId\": \"1234512xvc1314asdfs-503\", \"objectType\": \"membercostshare\" }, \"_org\": \"example.com\", \"objectId\": \"27283xvx9asdff-504\", \"objectType\": \"planservice\" }, { \"linkedService\": { \"_org\": \"example.com\", \"objectId\": \"1234520xvc30sfs-505\", \"objectType\": \"service\", \"name\": \"well baby\" }, \"planserviceCostShares\": { \"deductible\": 10, \"_org\": \"example.com\", \"copay\": 175, \"objectId\": \"1234512xvc1314sdfsd-506\", \"objectType\": \"membercostshare\" }, \"_org\": \"example.com\", \"objectId\": \"27283xvx9sdf-507\", \"objectType\": \"planservice\" } ], \"_org\": \"example.com\", \"objectId\": \"12xvxc345ssdsds-508\", \"objectType\": \"plan\", \"planType\": \"inNetwork\", \"creationDate\": \"12-12-2017\" }");
        //Utils.validate("plan_schema", val);
        //Utils.test();
        //SchemaTest();
        String str = createJWT();
        System.out.println(str);
        System.out.println(verifyJWT(str));
    }

    public static String getIndex(Map<String, String> map) {
        return map.get("objectType") + "_" + map.get("objectId");
    }

    public static String createJWT() throws JOSEException {
        JWSSigner signer = new RSASSASigner(rsaJWK);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(CONSTANTS.JWT_SUBJECT)
                .issuer(CONSTANTS.JWT_ISSUER)
                .expirationTime(new Date(new Date().getTime() + 3600 * 1000))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                claimsSet);

        signedJWT.sign(signer);

        String s = signedJWT.serialize();

        return s;
    }

    public static boolean verifyJWT(String jwt) {
        SignedJWT signedJWT = null;
        try {
            signedJWT = SignedJWT.parse(jwt);

            JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);

            if(!signedJWT.verify(verifier)) {
                return false;
            }

            if(!signedJWT.getJWTClaimsSet().getSubject().equals(CONSTANTS.JWT_SUBJECT)) {
                return false;
            }

            if(!signedJWT.getJWTClaimsSet().getIssuer().equals(CONSTANTS.JWT_ISSUER)) {
                return false;
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        return true;
    }
}
