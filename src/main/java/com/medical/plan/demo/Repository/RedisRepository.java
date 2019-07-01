package com.medical.plan.demo.Repository;


import java.util.Map;

public interface RedisRepository<String,Object> {

    Map<String,Object> findAll();

    Object findById(String id);

    Object save(Object plan);

    void removeOne(String id);

    void patch(Object plan);

    void put(Object plan);
}
