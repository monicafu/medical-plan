package com.medical.plan.demo.service;

import java.util.Map;

public interface PlanServices {
    Map<String,Map> findAll();

    Map<String,Map> findById(String id);

    Map<String,Map> save(Map map);

    void remove(String id);
}
