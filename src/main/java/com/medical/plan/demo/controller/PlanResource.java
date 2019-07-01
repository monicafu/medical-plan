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
        System.out.println(id);
        return planServices.findById(id);
    }

    @PostMapping("/add")
    public ModelAndView add(@RequestBody String planJson) {

        //validate schema
        Map plan = Utils.convertStrToMap(planJson);
        String message = Utils.validate("plan_schema",plan);

        ModelAndView modelAndView = new ModelAndView("redirect:/plan/success");
        modelAndView.addObject("message", message);
        if (message.startsWith("success")){
            planServices.save(plan);
            return modelAndView;
        }else {
            modelAndView.setViewName("redirect:/plan/error");
            return modelAndView;
        }
    }

    @PatchMapping("/add")
    public ModelAndView patch(@RequestBody String planJson) {
        Map plan = Utils.convertStrToMap(planJson);

        ModelAndView modelAndView = new ModelAndView("redirect:/plan/success");
        planServices.patch(plan);
        //modelAndView.addObject(planServices.patch(plan));
        return modelAndView;
    }

    @PutMapping("/add")
    public ModelAndView put(@RequestBody String planJson) {
        Map plan = Utils.convertStrToMap(planJson);
        String message = Utils.validate("plan_schema",plan);

        ModelAndView modelAndView = new ModelAndView("redirect:/plan/success");
        modelAndView.addObject("message", message);
        if (message.startsWith("success")){
            planServices.put(plan);
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
