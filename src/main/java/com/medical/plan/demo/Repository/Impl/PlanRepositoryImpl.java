package com.medical.plan.demo.Repository.Impl;

import com.medical.plan.demo.Repository.PlanRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class PlanRepositoryImpl implements PlanRepository {

    private RedisTemplate<String,Map> redisTemplate;
    private HashOperations hashOperations;

    public PlanRepositoryImpl(RedisTemplate<String,Map> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }
    @Override
    public Map<String,Map> findAll() {
        return hashOperations.entries("PLAN");
    }

    @Override
    public Map<String,Map> findById(String id) {
        return (Map)hashOperations.get("PLAN",id);

    }

    @Override
    public Map<String,Map> save(Map plan) {
        hashOperations.put("PLAN",plan.get("objectId"),plan);
        return plan;
    }

    @Override
    public Map<String,Map> update(Map plan) {
        return save(plan);
    }

    @Override
    public void removeOne(String id) {
        hashOperations.delete("PLAN",id);
    }
}
