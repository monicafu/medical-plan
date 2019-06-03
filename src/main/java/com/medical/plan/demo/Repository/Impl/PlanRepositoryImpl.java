package com.medical.plan.demo.Repository.Impl;

import com.medical.plan.demo.model.Plan;
import com.medical.plan.demo.Repository.PlanRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class PlanRepositoryImpl implements PlanRepository {

    private RedisTemplate<String,Plan> redisTemplate;
    private HashOperations hashOperations;

    public PlanRepositoryImpl(RedisTemplate<String,Plan> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }
    @Override
    public Map<String,Plan> findAll() {
        return hashOperations.entries("PLAN");
    }

    @Override
    public Plan findById(String id) {
        return (Plan)hashOperations.get("PLAN",id);

    }

    @Override
    public Plan save(Plan plan) {
        hashOperations.put("PLAN",plan.getObjectId(),plan);
        return plan;
    }

    @Override
    public Plan update(Plan plan) {
        return save(plan);
    }

    @Override
    public void removeOne(String id) {
        hashOperations.delete("PLAN",id);
    }
}
