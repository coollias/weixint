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
import java.util.Optional;
import java.util.List;


@RestController
@RequestMapping("/api")
public class pushController {

    @PostMapping("/push")
    public ResponseEntity<?> handlePush(
            @RequestBody Map<String,Object> jsonPayload,  // JSON模式
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce) {

        // 1. 验证签名
        if(!checkSignature(signature, timestamp, nonce)) {
            return ResponseEntity.status(403).build();
        }

        // 2. 处理消息逻辑
        System.out.println("收到推送：" + jsonPayload);
        return ResponseEntity.ok().build();
    }
}