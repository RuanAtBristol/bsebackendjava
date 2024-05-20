package com.yupi.springbootinit.model.dto.bse;

import lombok.Data;

import java.util.Map;

@Data
public class BSEAndAiParameterRequest {
    private int startTime;
    private int endTime;
    private int sellerRangeFrom;
    private int sellerRangeTo;
    private int buyerRangeFrom;
    private int buyerRangeTo;
    private String stepMode;
    private String timeMode;
    private int orderInterval;
    private Map<String, Integer> sellersSpec;
    private Map<String, Integer> buyersSpec;

    // experiment id
    private String trialId;

    /**
     * table name
     */
    private String name;

    /**
     * analysis goal
     */
    private String goal;

    /**
     * chart type
     */
    private String chartType;

    private static final long serialVersionUID = 1L;
}
