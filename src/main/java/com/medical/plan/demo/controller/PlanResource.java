package com.medical.plan.demo.controller;

import com.medical.plan.demo.Tools.Utils;
import com.medical.plan.demo.service.PlanServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/plans")
public class PlanResource {
    @Autowired
    private PlanServices planServices;
    public PlanResource(PlanServices planServices) {
        this.planServices = planServices;
    }


    @GetMapping("/")
    public Map<String,Map> findAll() {
        return planServices.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        Map res = findByIds(id);
        return new ResponseEntity<Object>(res,HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> add(@RequestBody String planJson) {

        //validate schema
        Map plan = Utils.convertStrToMap(planJson);
        String message = Utils.validate("plan_schema",plan);

        if (message.startsWith("success")){
            planServices.save(plan);
            return new ResponseEntity<Object>(message,HttpStatus.OK);
        }else {
            return new ResponseEntity<Object>(message,HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("/")
    public ResponseEntity<?> patch(@RequestBody String planJson) {
        Map plan = Utils.convertStrToMap(planJson);

        String id = Utils.getIndex(plan);

        Map searchRes =  findByIds(id);
        String res = "";
        if(searchRes == null || searchRes.size() == 0) {

            String message = Utils.validate("plan_schema",plan);
            if (message.startsWith("success")){
                planServices.save(plan);
                res = "Can't find this plan, success add, the objectId is " + Utils.getIndex(plan);
                return new ResponseEntity<Object>(res, HttpStatus.OK);
            } else {
                res = "Can't find this plan, failure add because the object is not valid";
                return new ResponseEntity<Object>(res,HttpStatus.EXPECTATION_FAILED);
            }

        } else {
            planServices.patch(plan, searchRes);
            res = "Success merge, the objectId is " + Utils.getIndex(plan);
            return new ResponseEntity<Object>(res,HttpStatus.OK);
        }
    }

    @PutMapping("/")
    public ResponseEntity<?> put(@RequestBody String planJson) {
        Map plan = Utils.convertStrToMap(planJson);
        String message = Utils.validate("plan_schema",plan);

        if (message.startsWith("success")){
            planServices.put(plan);
            return new ResponseEntity<Object>(message,HttpStatus.OK);
        }else {
            return new ResponseEntity<Object>(message,HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        planServices.remove(id);
        return new ResponseEntity<Object>(null,HttpStatus.OK);
    }

    private Map findByIds(String id) {
        return planServices.findById(id);
    }

}
