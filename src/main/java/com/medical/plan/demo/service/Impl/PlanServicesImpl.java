package com.medical.plan.demo.service.Impl;

import com.medical.plan.demo.Repository.PlanRepository;
import com.medical.plan.demo.model.Plan;
import com.medical.plan.demo.service.PlanServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class PlanServicesImpl implements PlanServices {
    @Autowired
    private PlanRepository planRepository;

    @Override
    public Map<String, Plan> findAll() {
        return planRepository.findAll();
    }

    @Override
    public Plan findById(String id) {
        return planRepository.findById(id);
    }

    @Override
    public Plan save(Plan plan) {
        return planRepository.save(plan);
    }

    @Override
    public Plan update(Plan plan) {
        return planRepository.update(plan);
    }

    @Override
    public void remove(String id) {
        planRepository.removeOne(id);
    }
}
