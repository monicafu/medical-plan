package com.medical.plan.demo.Repository;


import java.util.Map;

public interface RedisRepository<String,Object> {

    Map<String,Object> findAll();

    Object findById(String id);

    Object save(Object plan);

    Object update(Object plan);

    void removeOne(String id);
}
