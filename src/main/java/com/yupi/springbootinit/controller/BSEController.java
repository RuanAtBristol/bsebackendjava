package com.yupi.springbootinit.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.constant.AiConstant;
import com.yupi.springbootinit.constant.BSEConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.dto.bse.BSEAlgorithmParameterRequest;
import com.yupi.springbootinit.model.dto.bse.BSEAndAiParameterRequest;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.enums.CallbackState;
import com.yupi.springbootinit.model.vo.ChatResponse;
import com.yupi.springbootinit.service.BSEService;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/bse")
@Slf4j
public class BSEController {

    @Resource
    private AiManager aiManager;

    @Resource
    private ChartService chartService;

    @Resource
    BSEService bseService;

    @Resource
    private UserService userService;

    @PostMapping("/run")
    public BaseResponse<ChatResponse> receiveAlgorithmParameter(@RequestBody BSEAndAiParameterRequest bseAndAiParameterRequest,
                                                                HttpServletRequest request) {
        ThrowUtils.throwIf(bseAndAiParameterRequest == null, ErrorCode.PARAMS_ERROR, "parameter is null");

        BSEAlgorithmParameterRequest bseAlgorithmParameterRequest = new BSEAlgorithmParameterRequest();
        BeanUtil.copyProperties(bseAndAiParameterRequest, bseAlgorithmParameterRequest);
        String callStateString = bseService.transferAlgorithmParameter(bseAlgorithmParameterRequest);

        ThrowUtils.throwIf(!callStateString.equals(CallbackState.Success.getMessage()), ErrorCode.SYSTEM_ERROR, "Run algorithm failed");

        // 调用AI模型
        String filePath = BSEConstant.BSE_AVG_BALANCE_CSV_FILEPATH_PREFIX
                + bseAlgorithmParameterRequest.getTrialId() + "_avg_balance.csv";
        String csvData = convertCsv(filePath);

        User loginUser = userService.getLoginUser(request);
        long modelId = AiConstant.MODEL_ID;

        // 用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        String chartType = bseAndAiParameterRequest.getChartType();
        String name = bseAndAiParameterRequest.getName();
        String goal = bseAndAiParameterRequest.getGoal();
        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，图表类型：" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        // append csv data from bse
        userInput.append(csvData).append("\n");

        String result = aiManager.doChat(modelId, userInput.toString());
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "BSE Running succeed, but AI generation failed");
        }

        String genChart = splits[1].trim(); // option object for ECharts drawing chart
        String genResult = splits[2].trim();
        // 传输数据
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(loginUser.getId());

        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "BSE Running succeed, but chart saving failed");

        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setGenChart(genChart);
        chatResponse.setGenResult(genResult);
        chatResponse.setChartId(chart.getId());
        return ResultUtils.success(chatResponse);
    }

    private String convertCsv(String filePath){

        // 使用Hutool的CsvReader读取CSV文件
        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(FileUtil.file(filePath), StandardCharsets.UTF_8);

        // 使用Map来存储每个时间点的最后一行数据
        Map<String, String[]> latestRows = new LinkedHashMap<>();

        // 提取表头的算法名称并根据规律添加
        CsvRow firstRow = data.getRow(0);
        List<String> headers = new ArrayList<>();
        headers.add("时间");
        int colIndex = 4; // 从第5列开始，第5列的索引为4
        while (colIndex < firstRow.size()) {
            String algorithmName = firstRow.get(colIndex);
            headers.add(algorithmName);
            colIndex += 4; // 每隔3列是下一个算法名
        }

        // 提取数据行并保留同一时间的最后一行
        for (CsvRow row : data.getRows()) {
            String time = row.get(1); // 获取时间列，索引为1
            List<String> newRow = new ArrayList<>();
            newRow.add(time);

            colIndex = 4; // 从第5列开始，第5列的索引为4
            while (colIndex + 3 < row.size()) {
                newRow.add(row.get(colIndex + 3)); // 添加对应的利润列
                colIndex += 4; // 每隔3列是下一个算法名
            }
            latestRows.put(time, newRow.toArray(new String[0]));
        }

        // 初始化新的CSV数据
        List<String[]> newCsvData = new ArrayList<>();
        newCsvData.add(headers.toArray(new String[0]));

        // 添加最新的每个时间点的数据行
        for (String[] line : latestRows.values()) {
            newCsvData.add(line);
        }

        // 将新的CSV数据转换为字符串
        StringBuilder csvContent = new StringBuilder();
        for (String[] line : newCsvData) {
            csvContent.append(String.join(",", line)).append("\n");
        }

        // 输出CSV内容
        String csvString = csvContent.toString();
        return csvString;
    }

}
