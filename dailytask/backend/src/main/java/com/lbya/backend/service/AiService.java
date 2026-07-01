package com.lbya.backend.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AiService {

    @Autowired(required = false)
    private OpenAiChatModel chatModel;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SYSTEM_PROMPT = """
        你是一个SQL专家。根据用户的问题，生成对应的MySQL SQL查询语句。
        数据库表结构如下：
        - product(id, name, price, original_price, description, category, image, stock, sales, status, create_time)
        - orders(id, order_no, user_id, product_id, product_name, product_image, product_price, quantity, total_price, receiver_name, receiver_phone, receiver_address, status, create_time, update_time)
        - user(id, username, phone, role, status, create_time)
        - category(id, name, sort_order, status, create_time, update_time)
        规则：1.只生成SELECT语句 2.最多50行LIMIT 3.只返回纯SQL 4.字段用下划线命名 5.不查password字段
        """;

    @Transactional(readOnly = true)
    public Map<String, Object> query(String question) {
        if (chatModel == null) throw new RuntimeException("AI服务未配置，请设置DEEPSEEK_API_KEY环境变量");
        String sql = generateSql(question);
        validateSql(sql);
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        if (results.size() > 50) results = results.subList(0, 50);
        Map<String, Object> data = new HashMap<>();
        data.put("sql", sql);
        data.put("results", results);
        data.put("columns", results.isEmpty() ? Collections.emptyList() : new ArrayList<>(results.get(0).keySet()));
        return data;
    }

    private String generateSql(String question) {
        Prompt prompt = new Prompt(List.of(new SystemMessage(SYSTEM_PROMPT), new UserMessage(question)));
        ChatResponse response = chatModel.call(prompt);
        String sql = response.getResult().getOutput().getText().trim();
        sql = sql.replaceAll("```sql\\s*", "").replaceAll("```\\s*", "").trim();
        // 去除末尾分号（AI常生成带分号的SQL）
        if (sql.endsWith(";")) sql = sql.substring(0, sql.length() - 1).trim();
        return sql;
    }

    private void validateSql(String sql) {
        String upper = sql.toUpperCase().trim();
        if (!upper.startsWith("SELECT")) throw new RuntimeException("仅允许SELECT查询");
        if (sql.contains(";")) throw new RuntimeException("不允许分号");
        if (sql.contains("--") || sql.contains("/*")) throw new RuntimeException("不允许注释");
        for (String kw : new String[]{"UNION", "DROP ", "DELETE ", "UPDATE ", "INSERT ", "ALTER ", "CREATE ", "TRUNCATE", "EXEC ", "GRANT ", "REVOKE "}) {
            if (upper.contains(kw)) throw new RuntimeException("不允许的关键字: " + kw.trim());
        }
        if (upper.contains("PASSWORD") && upper.contains("USER")) throw new RuntimeException("不允许查询密码");
    }
}
