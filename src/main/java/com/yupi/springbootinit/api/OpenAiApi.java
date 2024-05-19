package com.yupi.springbootinit.api;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;

public class OpenAiApi {


    public static void main(String[] args) {

        String url = "https://api.openai.com/v1/chat/completions";
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("prompt", "你好");
        String json = JSONUtil.toJsonStr(hashMap);
        String result = HttpRequest.post(url)
                .header("Authorization", "Bearer openai-api-key")
                .body(json)
                .execute()
                .body();

        // 返回结果示例
        // {"id":"chatcmpl-6p468J2161265","object":"chat.completion","created":1677777777,"model":"gpt-3.5-turbo","usage":{"prompt_tokens":3,"completion_tokens":6,"total_tokens":9},"choices":
    }

}
