package com.medical.plan.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanService extends SuperPlan implements Serializable {
    private Service linkedService;
    private MemberCostShare planserviceCostShares;

}
