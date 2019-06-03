package com.medical.plan.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  SuperPlan implements Serializable {
    private String _org;
    private String objectId;
    private String objectType;
}
