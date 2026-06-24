package com.lbya.mcp.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 计算器 MCP 工具
 * 提供加减乘除、平均值、宿舍容量计算等 MCP 工具
 */
@Component
public class CalculatorTool {

    @Tool(name = "add", description = "执行加法：计算两个数的和。当用户询问两数之和时使用")
    public String add(
            @ToolParam(name = "a", description = "第一个数") int a,
            @ToolParam(name = "b", description = "第二个数") int b) {
        return String.valueOf(a + b);
    }

    @Tool(name = "subtract", description = "执行减法：计算两个数的差。当用户询问两数之差时使用")
    public String subtract(
            @ToolParam(name = "a", description = "被减数") int a,
            @ToolParam(name = "b", description = "减数") int b) {
        return String.valueOf(a - b);
    }

    @Tool(name = "multiply", description = "执行乘法：计算两个数的积。当用户询问两数之积时使用")
    public String multiply(
            @ToolParam(name = "a", description = "第一个数") int a,
            @ToolParam(name = "b", description = "第二个数") int b) {
        return String.valueOf(a * b);
    }

    @Tool(name = "divide", description = "执行除法：计算两个数的商。当用户询问两数之商时使用。注意：除数不能为0")
    public String divide(
            @ToolParam(name = "a", description = "被除数") int a,
            @ToolParam(name = "b", description = "除数（不能为0）") int b) {
        if (b == 0) {
            return "错误：除数不能为0";
        }
        double result = (double) a / b;
        return String.valueOf(result);
    }

    @Tool(name = "average", description = "计算平均值：计算一组数字的平均值。当用户询问平均数时使用")
    public String average(
            @ToolParam(name = "numbers", description = "数字列表，用逗号分隔，例如：1,2,3,4,5") String numbers) {
        String[] parts = numbers.split(",");
        double sum = 0;
        for (String part : parts) {
            sum += Double.parseDouble(part.trim());
        }
        return String.valueOf(sum / parts.length);
    }

    @Tool(name = "calculateDormitoryCapacity",
          description = "计算宿舍容量：根据楼号、房间数、每间容量和已住人数，计算总容量、空床位数和入住率。当用户询问宿舍容量、入住率、空床位时使用")
    public String calculateDormitoryCapacity(
            @ToolParam(name = "building", description = "楼号，例如：3") String building,
            @ToolParam(name = "totalRooms", description = "总房间数") int totalRooms,
            @ToolParam(name = "capacityPerRoom", description = "每间房的床位数（容量）") int capacityPerRoom,
            @ToolParam(name = "currentOccupancy", description = "当前已住人数") int currentOccupancy) {
        int totalCapacity = totalRooms * capacityPerRoom;
        int emptyBeds = totalCapacity - currentOccupancy;
        double occupancyRate = (double) currentOccupancy / totalCapacity * 100;
        return String.format(
                "楼号：%s，总容量：%d人，已住：%d人，空床位：%d个，入住率：%.1f%%",
                building, totalCapacity, currentOccupancy, emptyBeds, occupancyRate
        );
    }

    @Tool(name = "calculateEmptyBeds",
          description = "计算空床位数：根据楼号、总容量和已住人数，计算空床位数。当用户只询问空床位数量时使用，比calculateDormitoryCapacity更简洁")
    public String calculateEmptyBeds(
            @ToolParam(name = "building", description = "楼号，例如：3") String building,
            @ToolParam(name = "totalCapacity", description = "总容量（总床位数）") int totalCapacity,
            @ToolParam(name = "currentOccupancy", description = "当前已住人数") int currentOccupancy) {
        int emptyBeds = totalCapacity - currentOccupancy;
        return String.format("楼号：%s，空床位：%d个", building, emptyBeds);
    }
}
