package com.lbya.mcp.tool;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * 邮件发送 MCP 工具
 * 当用户说"发一封入住通知邮件"时，AI 自动调用此工具
 */
@Component
public class EmailTool {

    private final JavaMailSender mailSender;

    // 发件人邮箱（从配置文件的 spring.mail.username 读取，也可硬编码）
    private static final String FROM_EMAIL = "your-qq@qq.com";

    public EmailTool(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Tool(name = "sendCheckInEmail",
          description = "发送入住通知邮件：给指定学生发送入住确认邮件，包含楼号、房间号等信息。当用户要求发送入住通知、入住确认邮件时使用")
    public String sendCheckInEmail(
            @ToolParam(name = "to", description = "收件人邮箱地址") String to,
            @ToolParam(name = "studentName", description = "学生姓名") String studentName,
            @ToolParam(name = "building", description = "楼号，例如：3号楼") String building,
            @ToolParam(name = "room", description = "房间号，例如：301") String room) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(to);
            helper.setSubject("入住通知 - " + building + room + "室");

            String htmlContent = """
                    <div style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2 style="color: #2c3e50;">入住通知</h2>
                        <p><strong>%s</strong> 同学，您好！</p>
                        <p>您已成功入住 <strong>%s %s室</strong>，请按时办理入住手续。</p>
                        <hr style="border: 1px solid #eee;">
                        <p style="color: #888; font-size: 12px;">此邮件由智能宿舍管理系统自动发送，请勿回复。</p>
                    </div>
                    """.formatted(studentName, building, room);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            return "入住通知邮件发送成功！收件人：" + to + "，学生：" + studentName;

        } catch (MessagingException e) {
            return "邮件发送失败：" + e.getMessage();
        }
    }
}
