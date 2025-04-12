package com.tencent.wxcloudrun.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CounterRequest;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.List;

/**
 * counter控制器
 */
@RestController

public class pushController {



    @PostMapping(value = "/api/push" ,produces = "application/xml;charset=UTF-8")
    Map<String, Object> create(@RequestBody Map<String, Object> payload,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce) {
        logger.info("收到推送消息：{}", payload);
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

}