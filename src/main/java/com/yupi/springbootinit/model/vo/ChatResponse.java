package com.yupi.springbootinit.model.vo;

import lombok.Data;

/**
 * chat 返回结果
 */
@Data
public class ChatResponse {

    private String genChart;

    private String genResult;

    private Long chartId;

}
