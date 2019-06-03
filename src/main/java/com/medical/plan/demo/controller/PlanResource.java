package com.medical.plan.demo.controller;

import com.medical.plan.demo.Repository.JsonValidation;
import com.medical.plan.demo.model.Plan;
import com.medical.plan.demo.model.UUIDGenerator;
import com.medical.plan.demo.service.PlanServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/plan")
public class PlanResource {
    @Autowired
    private PlanServices planServices;
    private UUIDGenerator uuidGenerator;
    private JsonValidation jsonValidation;

    public PlanResource(PlanServices planServices) {
        this.planServices = planServices;
    }


    @GetMapping("/all")
    public Map<String,Plan> findAll() {
        return planServices.findAll();
    }

    @GetMapping("/{id}")
    public Plan findById(@PathVariable("id") String id) {
        return planServices.findById(id);
    }

    @PostMapping("/add")
    public Plan add(@RequestBody Plan plan) {
        String id = uuidGenerator.randomUUID(16);
        plan.setObjectId(id);
        //validate schema
        String message = jsonValidation.validate("plan_schema",plan);
        if (message.equals("success")){
            return planServices.save(plan);
        }
        return plan;
    }

    @PutMapping("/add/{id}")
    public Plan update(@PathVariable("id") String id, @RequestBody Plan plan) {
        Plan item = planServices.findById(id);
        item.setCreationDate(plan.getCreationDate());
        item.set_org(plan.get_org());
        item.setPlanType(plan.getPlanType());
        item.setLinkedPlanServices(plan.getLinkedPlanServices());
        item.setPlanCostShares(plan.getPlanCostShares());
        return planServices.update(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        planServices.remove(id);
    }

}
