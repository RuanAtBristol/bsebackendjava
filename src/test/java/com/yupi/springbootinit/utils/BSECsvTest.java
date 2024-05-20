package com.yupi.springbootinit.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class BSECsvTest {
    @Test
    void test1() {
        // 假设CSV文件路径为"example.csv"
        String filePath = "/Users/yhj19/developer/MY_Java/bsebackendjava/src/main/resources/testcsv2.csv";

        // 使用Hutool的CsvReader读取CSV文件
        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(FileUtil.file(filePath), StandardCharsets.UTF_8);

        // 初始化新的CSV数据
        List<String[]> newCsvData = new ArrayList<>();

        // 处理表头
        List<String> headers = new ArrayList<>();
        headers.add("时间");

        // 提取表头的算法名称并根据规律添加
        CsvRow firstRow = data.getRow(0);
        int colIndex = 4; // 从第5列开始，第5列的索引为4
        while (colIndex < firstRow.size()) {
            String algorithmName = firstRow.get(colIndex);
            headers.add(algorithmName);
            colIndex += 4; // 每隔3列是下一个算法名
        }
        newCsvData.add(headers.toArray(new String[0]));

        // 提取指定列的数据
        for (CsvRow row : data.getRows()) {
            List<String> newRow = new ArrayList<>();
            newRow.add(row.get(1)); // 添加时间列，索引为1

            // 提取每个算法的利润列
            colIndex = 4; // 从第5列开始，第5列的索引为4
            while (colIndex + 3 < row.size()) {
                newRow.add(row.get(colIndex + 3)); // 添加对应的利润列
                colIndex += 4; // 每隔3列是下一个算法名
            }
            newCsvData.add(newRow.toArray(new String[0]));
        }

        // 将新的CSV数据转换为字符串
        StringBuilder csvContent = new StringBuilder();
        for (String[] line : newCsvData) {
            csvContent.append(String.join(",", line)).append("\n");
        }

        // 输出CSV内容
        String csvString = csvContent.toString();
        System.out.println(csvString);
    }

    @Test
    void test2() {
        // 假设CSV文件路径为"example.csv"
        String filePath = "/Users/yhj19/developer/MY_Java/bsebackendjava/src/main/resources/testcsv2.csv";

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
        System.out.println(csvString);
    }
}
