package com.medical.plan.demo.service;

import com.medical.plan.demo.model.Plan;

import java.util.Map;

public interface PlanServices {
    Map<String,Plan> findAll();

    Plan findById(String id);

    Plan save(Plan plan);

    Plan update(Plan plan);

    void remove(String id);
}
