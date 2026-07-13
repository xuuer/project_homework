package edu.fdzc.project.config;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Configuration
public class CozeConfig {

    @Value("${coze.url}")
    private String url;

    @Value("${coze.url2}")
    private String url2;

    @Value("${coze.token}")
    private String token;

    @Value("${coze.workflowId}")
    private String workflowId;

    @Value("${coze.workflowId2}")
    private String workflowId2;

    @Value("${coze.workflowId3}")
    private String workflowId3;

    @Value("${coze.workflowId4}")
    private String workflowId4;

    @Value("${coze.workflowId5}")
    private String workflowId5;

    public Flux<String> getAIAnswer(JSONArray array, String msg) {
        JSONObject parameters = JSONUtil.createObj()
                .putOnce("memory", array)
                .putOnce("msg", msg);

        JSONObject requestBody = JSONUtil.createObj()
                .putOnce("workflow_id", workflowId)
                .putOnce("parameters", parameters);

        return WebClient.create()
                .method(HttpMethod.POST)
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class);
    }

    public JSONArray markPaper(JSONArray array) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");

        JSONObject parameters = JSONUtil.createObj()
                .putOnce("input", array);

        JSONObject requestBody = JSONUtil.createObj()
                .putOnce("workflow_id", workflowId2)
                .putOnce("parameters", parameters);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url2, entity, String.class);
        JSONObject a = JSONUtil.parseObj(response.getBody());
        JSONObject b = JSONUtil.parseObj(a.getStr("data"));
        JSONArray c = JSONUtil.parseArray(b.getStr("output"));
        return c;
    }

    public String analyzeTag(String msg) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");

        JSONObject parameters = JSONUtil.createObj()
                .putOnce("input", msg);

        JSONObject requestBody = JSONUtil.createObj()
                .putOnce("workflow_id", workflowId3)
                .putOnce("parameters", parameters);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url2, entity, String.class);
        JSONObject a = JSONUtil.parseObj(response.getBody());
        JSONObject b = JSONUtil.parseObj(a.getStr("data"));
        String s = b.getStr("data");
        return s;
    }

    public JSONObject makePractice(String tag, Double mastery) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");

        JSONObject parameters = JSONUtil.createObj()
                .putOnce("tag", tag)
                .putOnce("mastery", mastery);

        JSONObject requestBody = JSONUtil.createObj()
                .putOnce("workflow_id", workflowId4)
                .putOnce("parameters", parameters);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url2, entity, String.class);
        JSONObject a = JSONUtil.parseObj(response.getBody());
        JSONObject b = JSONUtil.parseObj(a.getStr("data"));
        JSONObject c = JSONUtil.parseObj(b.getStr("output"));
        return c;
    }

    public Object markHomework(String json) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer " + token);
        headers.set("Content-Type","application/json");

        JSONObject parameters = JSONUtil.createObj()
                .putOnce("input", json);

        JSONObject requestBody = JSONUtil.createObj()
                .putOnce("workflow_id", workflowId5) // 设置请求的工作流id
                .putOnce("parameters", parameters); //设置请求头

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url2, entity, String.class);//调用工作流
        JSONObject a = JSONUtil.parseObj(response.getBody());
        JSONObject b = JSONUtil.parseObj(a.getStr("data"));
        JSONObject c = JSONUtil.parseObj(b.getStr("output"));
        return c; //输出结果
    }
}