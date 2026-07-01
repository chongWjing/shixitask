package com.lbya.backend.entity;

import lombok.Data;

/**
 * 学生信息实体（用于AI结构化输出）
 */
@Data
public class StudentInfo {
    /** 姓名 */
    private String name;
    /** 宿舍楼号 */
    private String building;
    /** 房间号 */
    private String room;
    /** 手机号 */
    private String phone;
    /** 考勤状态 */
    private String attendanceStatus;
}
