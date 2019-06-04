package com.medical.plan.demo.controller;

import com.medical.plan.demo.Tools.Utils;
import com.medical.plan.demo.service.PlanServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;


@RestController
@RequestMapping("/plan")
public class PlanResource {
    @Autowired
    private PlanServices planServices;
    public PlanResource(PlanServices planServices) {
        this.planServices = planServices;
    }


    @GetMapping("/all")
    public Map<String,Map> findAll() {
        return planServices.findAll();
    }

    @GetMapping("/{id}")
    public Map findById(@PathVariable("id") String id) {
        return planServices.findById(id);
    }

    @PostMapping("/add")
    public ModelAndView add(@RequestBody String planJson) {

        //validate schema
        Map plan = Utils.convertStrToMap(planJson);
        String message = Utils.validate("plan_schema",plan);

        ModelAndView modelAndView = new ModelAndView("redirect:/plan/success");
        modelAndView.addObject("message", message);
        if (message.equals("success")){
            planServices.save(plan);
            return modelAndView;
        }else {
            modelAndView.setViewName("redirect:/plan/error");
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
