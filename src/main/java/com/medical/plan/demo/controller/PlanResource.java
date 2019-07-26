package com.medical.plan.demo.controller;

import com.medical.plan.demo.Tools.Utils;
import com.medical.plan.demo.service.PlanServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    public Map findById(@PathVariable("id") String id) {
        return planServices.findById(id);
    }

    @PostMapping("/")
    public ModelAndView add(@RequestBody String planJson) {

        //validate schema
        Map plan = Utils.convertStrToMap(planJson);
        String message = Utils.validate("plan_schema",plan);

        ModelAndView modelAndView = new ModelAndView("redirect:/plans/success");
        modelAndView.addObject("message", message);
        if (message.startsWith("success")){
            planServices.save(plan);
            return modelAndView;
        }else {
            modelAndView.setViewName("redirect:/plans/error");
            return modelAndView;
        }
    }

    @PatchMapping("/")
    public String patch(@RequestBody String planJson) {
        Map plan = Utils.convertStrToMap(planJson);

        String id = Utils.getIndex(plan);

        Map searchRes = findById(id);
        if(searchRes == null || searchRes.size() == 0) {

            String message = Utils.validate("plan_schema",plan);
            if (message.startsWith("success")){
                planServices.save(plan);
                return "not find this plan, success add, the objectId is " + Utils.getIndex(plan) ;
            } else {
                return "not find this plan, failure add because the object is not valid";
            }

        } else {
            planServices.patch(plan);
            return "success merge, the objectId is " + Utils.getIndex(plan) ;
        }
    }

    @PutMapping("/")
    public ModelAndView put(@RequestBody String planJson) {
        Map plan = Utils.convertStrToMap(planJson);
        String message = Utils.validate("plan_schema",plan);

        ModelAndView modelAndView = new ModelAndView("redirect:/plans/success");
        modelAndView.addObject("message", message);
        if (message.startsWith("success")){
            planServices.put(plan);
            return modelAndView;
        }else {
            modelAndView.setViewName("redirect:/plans/error");
            return modelAndView;
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        planServices.remove(id);
    }

    @RequestMapping("/error")
    public String error(String message) {
        return "The post has some error: " + message;
    }

    @RequestMapping("/success")
    public String success(String message) {
        return message;
    }

}
