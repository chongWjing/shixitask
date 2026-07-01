package com.lbya.backend.controller;

import com.lbya.backend.service.AiService;
import com.lbya.backend.service.UserService;
import com.lbya.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired private AiService aiService;
    @Autowired private UserService userService;

    @PostMapping("/query")
    public Result<?> query(HttpServletRequest request, @RequestBody Map<String, String> params) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (!userService.checkAdmin(userId)) return Result.error(403, "无权限");
        String question = params.get("question");
        if (question == null || question.trim().isEmpty()) return Result.error("请输入查询问题");
        try {
            return Result.success(aiService.query(question));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
