package com.lbya.smart.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * SSE高级演示控制器
 * 演示ServerSentEvent自定义事件类型 + 报修进度分阶段推送
 */
@Slf4j
@RestController
@RequestMapping("/api/sse")
public class SSEChatController {

    /**
     * 报修进度推送（SSE自定义事件类型）
     * GET /api/sse/repair-progress?repairId=R001
     */
    @GetMapping(value = "/repair-progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> repairProgress(@RequestParam(defaultValue = "R001") String repairId) {

        ServerSentEvent<String> submitted = ServerSentEvent.<String>builder()
                .event("submitted")
                .id("1")
                .data("报修单 " + repairId + " 已提交成功")
                .build();

        ServerSentEvent<String> assigned = ServerSentEvent.<String>builder()
                .event("assigned")
                .id("2")
                .data("维修工李师傅已接单，预计30分钟到达")
                .build();

        ServerSentEvent<String> repairing = ServerSentEvent.<String>builder()
                .event("repairing")
                .id("3")
                .data("李师傅正在维修，请稍候...")
                .build();

        ServerSentEvent<String> completed = ServerSentEvent.<String>builder()
                .event("completed")
                .id("4")
                .data("维修完成！感谢您的耐心等待。")
                .build();

        return Flux.just(submitted, assigned, repairing, completed)
                .delayElements(Duration.ofSeconds(2));
    }
}
