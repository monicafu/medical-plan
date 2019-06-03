package com.medical.plan.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan extends SuperPlan implements Serializable {
    private String planType;
    private String creationDate;
    private MemberCostShare planCostShares;
    private PlanService[] linkedPlanServices;

    public Plan(String org, String date,String type, String planType) {
        this.set_org(org);
        this.setObjectType(type);
        this.planType = planType;
        this.creationDate = date;
    }
}
