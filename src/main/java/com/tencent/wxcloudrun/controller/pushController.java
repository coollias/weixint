package com.tencent.wxcloudrun.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WechatPushController {

    private final Logger logger = LoggerFactory.getLogger(WechatPushController.class);

    // 配置参数（需在云托管环境变量设置）
    @Value("${WECHAT_TOKEN}")
    private String token;


    @GetMapping("/api/push")
    public String validateServer(
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr) {

        logger.info("微信验证请求：signature={}", signature);

        // 1. 参数排序
        String[] params = {token, timestamp, nonce};
        Arrays.sort(params);

        // 2. 生成签名
        String calculatedSignature = DigestUtils.sha1Hex(String.join("", params));

        // 3. 返回验证结果
        return calculatedSignature.equals(signature) ? echostr : "验证失败";
    }


    @PostMapping("/api/push")
    public Map<String, Object> handleMessage(
            @RequestBody Map<String, Object> payload,
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce) {

        logger.info("收到推送消息：{}", payload);

        // 1. 验证签名（生产环境必须）
        if (!validateSignature(signature, timestamp, nonce)) {
            logger.error("签名验证失败");
            return errorResponse(403, "Invalid signature");
        }

        // 2. 处理消息类型
        String msgType = (String) payload.get("MsgType");
        Map<String, Object> response = new HashMap<>();

        switch (msgType) {
            case "text":
                response.put("ToUserName", payload.get("FromUserName"));
                response.put("FromUserName", payload.get("ToUserName"));
                response.put("Content", "已收到：" + payload.get("Content"));
                break;
            case "event":
                handleEvent(payload);
                response.put("status", "success");
                break;
            default:
                return errorResponse(400, "Unsupported message type");
        }

        return response;
    }


    private void handleEvent(Map<String, Object> payload) {
        String eventType = (String) payload.get("Event");
        logger.info("处理事件：{}", eventType);

        // 实现具体事件处理逻辑
        if ("subscribe".equals(eventType)) {
            // 处理关注事件
        }
    }


    private boolean validateSignature(String signature, String timestamp, String nonce) {
        String[] params = {token, timestamp, nonce};
        Arrays.sort(params);
        String calculated = DigestUtils.sha1Hex(String.join("", params));
        return calculated.equals(signature);
    }


    private Map<String, Object> errorResponse(int code, String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("errcode", code);
        res.put("errmsg", message);
        return res;
    }
}