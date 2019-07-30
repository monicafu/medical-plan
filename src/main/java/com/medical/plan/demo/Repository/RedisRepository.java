package com.medical.plan.demo.Repository;


import java.util.Map;

public interface RedisRepository<String,Object> {

    Map<String,Object> findAll();

    Object findById(String id);

    Object save(Object plan);

    void removeOne(String id);

    void patch(Object plan);

    void patch(Object source, Object target);

    void put(Object plan);
}
