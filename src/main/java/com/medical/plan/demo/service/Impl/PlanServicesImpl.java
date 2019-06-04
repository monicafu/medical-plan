package com.medical.plan.demo.service.Impl;

import com.medical.plan.demo.Repository.PlanRepository;
import com.medical.plan.demo.service.PlanServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class PlanServicesImpl implements PlanServices {
    @Autowired
    private PlanRepository planRepository;

    @Override
    public Map<String, Map> findAll() {
        return planRepository.findAll();
    }

    @Override
    public Map findById(String id) {
        return planRepository.findById(id);
    }

    @Override
    public Map save(Map plan) {
        return planRepository.save(plan);
    }

    @Override
    public void remove(String id) {
        planRepository.removeOne(id);
    }
}
