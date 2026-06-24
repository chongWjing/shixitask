# Day3：MCP 全实操 + 多模型 + 三天总结

> **日期**：6月18日 9:00-12:00
> **时长**：3小时（课间休息10分钟）
> **目标**：从零创建独立 MCP Server 项目，完成 MCP Client 工具调用全流程，实现邮件 MCP 与多模型智能路由，串联三天知识体系
> **版本**：Spring Boot 3.5.15 + Spring AI 1.0.0 GA + JDK 17 + DeepSeek API

---

## Part 1：MCP Server 开发（9:00-10:00）

### 步骤1：MCP 概念 5 分钟速览（9:00-9:05）

> **💡 前两天我们做了什么？**
>
> Day1——项目搭建 + Spring AI 五种同步调用模式（最简调用、System Prompt、ChatMemory 多轮对话、参数化模板、结构化输出）。
>
> Day2——流式 Flux 输出、提示词工程（Few-Shot / CoT / RTF 框架）、SSE 服务器推送。
>
> 你有没有发现一个问题——我们写的 AI，**只会聊天**。你问它问题，它回答。但它不能帮你查数据库、不能帮你发邮件、不能帮你做任何实际操作。
>
> 今天我们学一个新技术，让 AI 从"只会聊天"变成"能干活"。这个技术叫——**MCP**。

**MCP 核心概念速览：**

```
MCP = Model Context Protocol（模型上下文协议）

让 AI 从"只会聊天" → "能调用工具"
```

> **💡 USB-C 类比理解 MCP**
>
> 你手机充电，以前每个厂商有自己的充电接口——苹果 Lightning、安卓 Micro-USB、华为自己的接口。换一根线就要换一个充电器。
>
> 后来有了 **USB-C 统一标准**——所有手机都用同一个接口，一根线充所有设备。
>
> MCP 就是 AI 世界的 USB-C：
> - **没有 MCP 时**：每个 AI 平台有自己的工具调用方式（Function Calling），换 AI 就要重写工具代码
> - **有了 MCP 后**：统一协议，工具端只需实现一次，所有 AI 都能用
>
> | 对比 | Function Calling | MCP |
> |------|-----------------|-----|
> | 性质 | OpenAI 专有 | 开放标准 |
> | 换模型 | 要重写代码 | 不用改代码 |
> | 类比 | 苹果 Lightning | USB-C |

**MCP Client-Server 架构：**

```
┌─────────────┐    JSON-RPC 2.0    ┌─────────────┐
│             │ ◄─────────────────► │             │
│  MCP Client │                     │  MCP Server │
│  (AI应用端)  │                     │  (工具端)    │
│             │                     │             │
│  发送请求    │                     │  提供工具    │
│  接收结果    │                     │  执行操作    │
└─────────────┘                     └─────────────┘
      │                                    │
      ▼                                    ▼
┌─────────────┐                     ┌─────────────┐
│ Spring AI   │                     │ 数据库/API   │
│ DeepSeek    │                     │ 邮件服务     │
│ ChatModel   │                     │ 文件系统     │
└─────────────┘                     └─────────────┘
```

**MCP 交互三步流程：**

```
1. init         → Client 和 Server 建立连接，交换能力信息
2. tools/list   → Client 获取 Server 提供的工具列表
3. tools/call   → Client 调用指定工具，Server 执行并返回结果
```

> **💡 关键认知：AI 自己决定何时调用工具**
>
> 我们不需要写 if-else 判断"用户问加法就调 add 工具"。AI 会根据工具的 `description` 自己判断是否需要调用、调用哪个工具。
>
> 这就像你请了一个聪明的助手——你不用告诉他"遇到 A 情况用工具甲"，你只需要把工具清单给他，他自己就能判断。

---

### 步骤2：创建 MCP Server Maven 项目（9:05-9:10）

> **💡 为什么 MCP Server 要独立项目？**
>
> MCP 的核心思想是**解耦**——工具服务（Server）和 AI 应用（Client）分开。
>
> 就像公司的"IT 部门"和"业务部门"分开一样——IT 部门提供工具能力，业务部门决定什么时候用、怎么用。
>
> 好处：
> 1. **独立部署**：Server 可以单独升级、重启，不影响 AI 应用
> 2. **复用**：一个 Server 可以被多个 AI 应用使用
> 3. **职责清晰**：Server 只管"提供工具"，Client 只管"决定调用"
>
> 在实际开发中，MCP Server 通常是一个独立的 jar 包，由 Client 通过 stdio（标准输入输出）方式启动。

1. 打开 [Spring Initializr](https://start.spring.io/)
2. 配置如下：
   - **Project**: Maven
   - **Language**: Java
   - **Spring Boot**: 3.5.15
   - **Group**: com.huashan
   - **Artifact**: mcp-calculator-server
   - **Name**: mcp-calculator-server
   - **Package name**: com.huashan.mcp
   - **Packaging**: Jar
   - **Java**: 17
3. 添加依赖：**Spring Web**
4. 点击 **GENERATE** 下载并解压到工作目录（与 smart 项目平级）
5. 用 IDEA 打开项目，等待 Maven 索引完成

> ⚠️ **注意**：这个项目和 smart 主项目是完全独立的！Group 是 `com.huashan`，Artifact 是 `mcp-calculator-server`，包名是 `com.huashan.mcp`。它不依赖 smart 项目。

---

### 步骤3：pom.xml 添加 MCP Server 依赖（9:10-9:14）

> **💡 MCP Server 需要什么依赖？**
>
> MCP Server 需要 `spring-ai-starter-mcp-server` 这个 starter。它做了三件事：
> 1. 自动配置 MCP Server 端点（让 Server 能接收 Client 的 JSON-RPC 请求）
> 2. 自动扫描 `@Tool` 注解的方法并注册为 MCP 工具
> 3. 提供 MCP 协议的通信能力（stdio / SSE 两种传输方式）
>
> 这和 smart 主项目用的 `spring-ai-starter-model-openai`（连接 AI 模型）是不同的依赖——Server 端不需要连 AI 模型，它只提供工具。

打开 `pom.xml`，完整内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- Spring Boot 父工程：统一管理 Spring Boot 相关依赖版本 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.15</version>
        <relativePath/>
    </parent>

    <!-- MCP Server 项目坐标：com.huashan:mcp-calculator-server -->
    <groupId>com.huashan</groupId>
    <artifactId>mcp-calculator-server</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>mcp-calculator-server</name>
    <description>MCP Calculator Server for Smart Dormitory</description>

    <properties>
        <java.version>17</java.version>
        <!-- Spring AI 版本号：和 smart 主项目保持一致 -->
        <spring-ai.version>1.0.0</spring-ai.version>
    </properties>

    <dependencies>
        <!-- SpringBoot Web：提供 HTTP 能力（MCP Server 的 SSE 传输方式需要） -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- ⭐ 核心：Spring AI MCP Server Starter -->
        <!-- 这个依赖让项目具备 MCP Server 的能力：
             1. 自动配置 MCP Server 端点
             2. 自动扫描 @Tool 注解并注册为 MCP 工具
             3. 支持 stdio 和 SSE 两种传输方式 -->
        <!-- ⚠️ 注意：这是 Server 端的 starter，不是 Client 端的 -->
        <!-- 依赖名：spring-ai-starter-mcp-server（Server）
             区别于：spring-ai-starter-mcp-client（Client，在 smart 主项目中用） -->
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-starter-mcp-server</artifactId>
            <version>${spring-ai.version}</version>
        </dependency>

        <!-- SpringBoot Test：单元测试框架 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Spring Boot Maven 插件：打包为可执行 jar -->
            <!-- MCP Client 通过 stdio 方式启动这个 jar -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

> ⚠️ **版本要点**：
> - `spring-ai.version` 必须和 smart 主项目一致（1.0.0），否则 Client 和 Server 之间可能出现协议不兼容
> - `spring-ai-starter-mcp-server` 是 Server 端依赖，不要和 Client 端的 `spring-ai-starter-mcp-client` 混淆
> - 这里不需要 `spring-ai-starter-model-openai`，因为 Server 不连接 AI 模型

---

### 步骤4：配置 application.yml（9:14-9:18）

> **💡 MCP Server 需要配置什么？**
>
> MCP Server 的配置很简单，只需要告诉 Client "我叫什么名字、版本号是多少"。
>
> 就像你去参加一个会议——你的工牌上写着"姓名：张三，职位：工程师"。
> Client 看到 Server 的名字和版本，就知道连接的是哪个服务。
>
> 注意：Server 不需要配置 AI 模型的 API Key——因为它不调用 AI，它只提供工具给 AI 用。

删除 `src/main/resources/application.properties`，创建 `application.yml`：

```yaml
server:
  port: 8081  # MCP Server 端口：避免和 smart 主项目(8080)冲突

spring:
  ai:
    mcp:
      server:
        # MCP Server 的名称——Client 连接时通过这个名字识别 Server
        # 就像蓝牙设备名：你的手机搜索蓝牙时看到的名字
        name: calculator-server
        # MCP Server 的版本号——方便后续升级管理
        version: 1.0.0
```

> 💡 **端口冲突注意**：MCP Server 用 8081 端口，smart 主项目用 8080 端口。如果两个项目同时运行，端口不能冲突。

---

### 步骤5：创建 CalculatorTool——add / subtract（9:18-9:28）

> **💡 @Tool 注解是什么？**
>
> `@Tool` 是 Spring AI 1.0.0 GA 提供的注解，用来把一个普通 Java 方法标记为 MCP 工具。
>
> **类比理解**：就像 `@RestController` 把一个类标记为 REST 控制器一样，`@Tool` 把一个方法标记为 MCP 工具。加上这个注解，Spring AI 就会自动把这个方法注册到 MCP Server 中，AI 就能调用它。
>
> **@Tool 注解的两个关键属性**：
> - `name`：工具名称，AI 调用时使用。不指定则默认用方法名
> - `description`：工具描述，**这是 AI 决定是否调用工具的依据**
>
> **@ToolParam 注解**：
> - 标注方法参数的含义，帮助 AI 理解应该传什么值
> - `name`：参数名称
> - `description`：参数描述
>
> **为什么 description 这么重要？**
> 因为 AI 不看你的代码，它只看 description 来决定"这个问题要不要调工具、调哪个工具"。
> 写得好 → AI 精准调用；写得差 → AI 调错工具或不调工具。
>
> 就像淘宝商品标题——标题写得好，用户才能搜到你；标题写得差，再好的商品也没人看到。

创建包 `com.huashan.mcp.tool`，新建 `CalculatorTool.java`：

```java
package com.huashan.mcp.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 计算器 MCP 工具
 *
 * 提供加减乘除四种数学运算，以及宿舍容量计算功能。
 * 用 @Tool 注解标注方法，Spring AI 自动注册为 MCP 工具。
 *
 * 核心优势：1.0.0 GA 用 @Tool + @ToolParam 注解，框架自动推断参数类型，
 * 声明式编程更直观，代码量少且不易出错。
 *
 * 核心模式（所有 MCP 工具都是这个结构）：
 * 1. @Component       → 让 Spring 管理这个 Bean
 * 2. @Tool(description="...") → 标记为 MCP 工具，description 是 AI 决策依据
 * 3. @ToolParam(description="...") → 标注参数含义
 * 4. 方法体           → 实际的业务逻辑
 */
@Component  // 让 Spring 管理这个 Bean，Spring AI 自动发现并注册
public class CalculatorTool {

    /**
     * 加法工具
     *
     * @Tool 注解读解：
     * - name = "add"：工具名称。AI 调用时用这个名字。不指定则默认用方法名
     * - description = "执行加法..."：工具描述。AI 看到这个描述来决定是否调用
     *
     * ⚠️ description 写法技巧：
     * - 好的写法："执行加法：计算两个数的和。当用户询问两数之和时使用"
     *   → AI 知道"什么时候用"（用户问加法）和"怎么用"（输入两个数求和）
     * - 差的写法："计算"
     *   → AI 不知道这是加法还是乘法还是什么
     *
     * 返回值类型 String：
     * - MCP 工具的返回值必须是 String 类型
     * - 因为 AI 接收的是文本，不是 Java 对象
     * - 所以计算结果要用 String.valueOf() 转换
     */
    @Tool(name = "add", description = "执行加法：计算两个数的和。当用户询问两数之和时使用")
    public String add(
            // @ToolParam：标注参数的含义，帮助 AI 理解应该传什么值
            // name = "a"：参数名称（AI 调用时传的 key）
            // description = "第一个数"：参数描述（AI 看到这个知道传什么值）
            @ToolParam(name = "a", description = "第一个数") int a,
            @ToolParam(name = "b", description = "第二个数") int b) {
        // 执行加法运算
        int result = a + b;
        // 返回字符串格式的结果（MCP 工具必须返回 String）
        return String.valueOf(result);
    }

    /**
     * 减法工具
     *
     * 和 add 工具结构完全一样，只是运算符和描述不同
     */
    @Tool(name = "subtract", description = "执行减法：计算两个数的差。当用户询问两数之差时使用")
    public String subtract(
            @ToolParam(name = "a", description = "被减数") int a,
            @ToolParam(name = "b", description = "减数") int b) {
        int result = a - b;
        return String.valueOf(result);
    }
}
```

> 💡 **代码解析**：
> - `@Component`：让 Spring 管理 Bean，Spring AI 自动发现并注册为 MCP 工具
> - `@Tool(name, description)`：标记方法为 MCP 工具。`description` 是 AI 决策的核心依据
> - `@ToolParam(name, description)`：标注参数含义，帮助 AI 正确传参
> - 返回值必须是 `String`：因为 AI 接收文本，不接收 Java 对象
> - `String.valueOf(result)`：将计算结果转为字符串

---

### 步骤6：添加 multiply / divide（含除零判断）（9:28-9:36）

> **💡 为什么除法要判断除零？**
>
> 在数学中，除数不能为 0。如果 AI 传入 b=0，程序会抛出 ArithmeticException 异常。
> MCP 工具必须对异常情况进行处理，返回友好的错误提示而不是让程序崩溃。
>
> 这就像 ATM 机——你输入取款金额为 0，它不会崩溃，而是提示"请输入有效金额"。

继续在 `CalculatorTool.java` 中添加：

```java
    /**
     * 乘法工具
     */
    @Tool(name = "multiply", description = "执行乘法：计算两个数的积。当用户询问两数之积时使用")
    public String multiply(
            @ToolParam(name = "a", description = "第一个数") int a,
            @ToolParam(name = "b", description = "第二个数") int b) {
        int result = a * b;
        return String.valueOf(result);
    }

    /**
     * 除法工具
     *
     * ⚠️ 特殊处理：除零判断
     * - AI 可能传入 b=0（比如用户问"5除以0"）
     * - 如果不判断，程序会崩溃
     * - 判断后返回友好提示，AI 会把这个提示转达给用户
     *
     * 这体现了 MCP 工具的设计原则：
     * 工具内部要处理异常情况，返回清晰的错误信息
     * 不要让异常抛到 MCP 框架层
     */
    @Tool(name = "divide", description = "执行除法：计算两个数的商。当用户询问两数之商时使用。注意：除数不能为0")
    public String divide(
            @ToolParam(name = "a", description = "被除数") int a,
            @ToolParam(name = "b", description = "除数（不能为0）") int b) {
        // 除零判断：防止程序崩溃
        if (b == 0) {
            return "错误：除数不能为0";
        }
        // 使用 double 类型避免整数除法截断（5/2=2.5 而不是 2）
        double result = (double) a / b;
        return String.valueOf(result);
    }
```

> 💡 **除法工具的两个关键设计**：
> - **除零判断**：`if (b == 0) return "错误：除数不能为0"`——不让异常抛出，返回友好提示
> - **使用 double 类型**：`(double) a / b` 避免整数除法截断（5/2=2.5 而非 2）

---

### 步骤7：添加 average + calculateDormitoryCapacity（9:36-9:44）

> **💡 为什么要做宿舍容量计算工具？**
>
> 前面的加减乘除是通用计算工具，任何场景都能用。但 MCP 的真正价值在于——**提供业务专属工具**。
>
> 在宿舍管理系统中，最常见的问题是"这栋楼还有多少空床位"。如果让 AI 自己算，它可能编一个数字。但有了 `calculateDormitoryCapacity` 工具，AI 就能调用工具得到真实数据。
>
> 这就是 MCP 解决 AI 幻觉问题的核心方案——**涉及数据，让 AI 去查，而不是让 AI 编**。

继续在 `CalculatorTool.java` 中添加：

```java
    /**
     * 平均值工具
     *
     * 计算多个数的平均值
     * 使用方式：AI 传入一组数字，返回平均值
     */
    @Tool(name = "average", description = "计算平均值：计算一组数字的平均值。当用户询问平均数时使用")
    public String average(
            @ToolParam(name = "numbers", description = "数字列表，用逗号分隔，例如：1,2,3,4,5") String numbers) {
        // 将逗号分隔的字符串解析为数字数组
        String[] parts = numbers.split(",");
        double sum = 0;
        for (String part : parts) {
            // trim() 去除空格，防止 "1, 2, 3" 中的空格导致解析错误
            sum += Double.parseDouble(part.trim());
        }
        // 计算平均值
        double result = sum / parts.length;
        return String.valueOf(result);
    }

    /**
     * 宿舍容量计算工具
     *
     * 这是业务专属工具——计算宿舍的入住率、空床位数等。
     * AI 看到用户问"3号楼还有多少空床位"时，会自动调用这个工具。
     *
     * ⚠️ description 写法要点：
     * - 要包含触发条件："当用户询问宿舍容量、入住率、空床位时使用"
     * - 要说明输入输出："输入楼号、房间数、每间容量、已住人数"
     * - 写得越清楚，AI 越能精准选择
     */
    @Tool(name = "calculateDormitoryCapacity",
          description = "计算宿舍容量：根据楼号、房间数、每间容量和已住人数，计算总容量、空床位数和入住率。当用户询问宿舍容量、入住率、空床位时使用")
    public String calculateDormitoryCapacity(
            @ToolParam(name = "building", description = "楼号，例如：3") String building,
            @ToolParam(name = "totalRooms", description = "总房间数") int totalRooms,
            @ToolParam(name = "capacityPerRoom", description = "每间房的床位数（容量）") int capacityPerRoom,
            @ToolParam(name = "currentOccupancy", description = "当前已住人数") int currentOccupancy) {
        // 计算总容量 = 房间数 × 每间容量
        int totalCapacity = totalRooms * capacityPerRoom;
        // 计算空床位数 = 总容量 - 已住人数
        int emptyBeds = totalCapacity - currentOccupancy;
        // 计算入住率 = 已住人数 / 总容量 × 100%
        double occupancyRate = (double) currentOccupancy / totalCapacity * 100;

        // 返回格式化的结果字符串，包含所有关键信息
        // AI 拿到这个字符串后，会组织成自然语言回答用户
        return String.format(
                "楼号：%s，总容量：%d人，已住：%d人，空床位：%d个，入住率：%.1f%%",
                building, totalCapacity, currentOccupancy, emptyBeds, occupancyRate
        );
    }
```

> 💡 **两种工具的设计思路**：
> - **通用工具**（add/subtract/multiply/divide/average）：不依赖业务，任何场景都能用
> - **业务工具**（calculateDormitoryCapacity）：针对特定业务场景，提供精准数据
>
> 实际项目中，业务工具更重要——因为通用计算 AI 自己就能做（虽然可能算错），但业务数据 AI 编不出来。

---

### 步骤8：启动 MCP Server + 验证工具列表（9:44-9:50）

1. 运行 `McpCalculatorServerApplication.main()`
2. 确认控制台输出：`Started McpCalculatorServerApplication in x.xxx seconds`
3. 观察 Spring AI 的自动注册日志——所有 `@Tool` 注解的方法被自动扫描并注册

> **💡 启动时发生了什么？**
>
> ```
> Spring Boot 启动
>   → Spring AI 扫描所有 @Component Bean
>   → 发现 CalculatorTool 上有 @Tool 注解的方法
>   → 自动注册为 MCP 工具：
>       - add（加法）
>       - subtract（减法）
>       - multiply（乘法）
>       - divide（除法）
>       - average（平均值）
>       - calculateDormitoryCapacity（宿舍容量）
>   → MCP Server 就绪，等待 Client 连接
> ```

验证工具是否注册成功——浏览器访问：

```
http://localhost:8081
```

> ⚠️ **常见问题**：
> - 如果启动报 `Port 8081 already in use`：说明端口被占用，改 application.yml 中的端口号
> - 如果启动报 `UnsatisfiedDependencyException`：检查 `spring-ai-starter-mcp-server` 依赖是否正确添加
> - 如果启动报 `Cannot resolve symbol @Tool`：检查 import 是否正确——`org.springframework.ai.tool.annotation.Tool`

---

### 步骤9：打包 mcp-calculator-server.jar（9:50-9:55）

> **💡 为什么要打包成 jar？**
>
> MCP Client（smart 主项目）通过 **stdio 方式**连接 MCP Server。
> stdio 方式的工作原理：Client 用 `java -jar xxx.jar` 命令启动 Server 进程，通过标准输入输出进行通信。
>
> 就像你用命令行运行一个程序——`java -jar app.jar`，程序在后台运行，通过键盘输入和屏幕输出交互。MCP Client 就是"键盘"，Server 就是"程序"。

在项目根目录执行 Maven 打包命令：

```bash
# 进入 mcp-calculator-server 项目目录
cd mcp-calculator-server

# 执行 Maven 打包（跳过测试以加快速度）
mvn clean package -DskipTests
```

打包成功后，在 `target/` 目录下会生成 `mcp-calculator-server-0.0.1-SNAPSHOT.jar`。

> ⚠️ **注意**：记住这个 jar 文件的完整路径，后面 MCP Client 配置需要用到！
> 例如：`E:/HuaShan/mcp-calculator-server/target/mcp-calculator-server-0.0.1-SNAPSHOT.jar`

---

### 步骤10：MCP Server 独立运行测试（9:55-10:00）

> **💡 怎么验证 jar 包能正常运行？**
>
> 在命令行中用 `java -jar` 启动 jar 文件，确认它和 IDEA 中启动的效果一样。
> 如果 jar 包能正常启动，说明打包没问题，后面 Client 就能通过 stdio 方式连接。

```bash
# 在命令行中启动 MCP Server
java -jar target/mcp-calculator-server-0.0.1-SNAPSHOT.jar
```

确认输出：
```
Started McpCalculatorServerApplication in x.xxx seconds
```

确认后按 `Ctrl+C` 停止 Server。

> 💡 **MCP Server 开发完成！** 总结一下我们做了什么：
> 1. 创建了独立的 Maven 项目 `mcp-calculator-server`
> 2. 添加了 `spring-ai-starter-mcp-server` 依赖
> 3. 用 `@Tool` + `@ToolParam` 注解定义了 6 个工具
> 4. 配置了 `application.yml`（Server 名称 + 版本）
> 5. 打包成 jar，验证可独立运行

---

## Part 2：MCP Client + 工具调用（10:00-10:50）

### 步骤11：Spring AI 主项目添加 MCP Client 依赖（10:00-10:06）

> **💡 MCP Client 和 MCP Server 的区别**
>
> | 角色 | 依赖 | 职责 | 项目 |
> |------|------|------|------|
> | MCP Server | `spring-ai-starter-mcp-server` | 提供工具 | mcp-calculator-server（独立项目） |
> | MCP Client | `spring-ai-starter-mcp-client` | 调用工具 | smart（主项目） |
>
> 就像餐厅——Server 是后厨（做菜），Client 是服务员（点菜、上菜）。
> 后厨不需要知道客人是谁，只需要按单做菜；服务员不需要会做菜，只需要把客人的需求传达给后厨。

打开 smart 主项目的 `pom.xml`，在 `<dependencies>` 中添加 MCP Client 依赖：

```xml
        <!-- ⭐ Spring AI MCP Client Starter -->
        <!-- 这个依赖让 smart 主项目具备 MCP Client 的能力：
             1. 自动配置 MCP Client
             2. 支持 stdio 方式连接 MCP Server
             3. 将 MCP Server 的工具注册到 AI 的可用工具列表中 -->
        <!-- ⚠️ 注意：这是 Client 端的 starter，不是 Server 端的
             Server 端用 spring-ai-starter-mcp-server（在 mcp-calculator-server 项目中） -->
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-starter-mcp-client</artifactId>
            <version>${spring-ai.version}</version>
        </dependency>
```

刷新 Maven，等待依赖下载完成。

---

### 步骤12：配置 application.yml 连接 MCP Server（10:06-10:11）

> **💡 stdio 方式连接的工作原理**
>
> stdio（Standard I/O）方式：Client 通过启动 Server 的 jar 进程，使用标准输入输出通信。
>
> 工作流程：
> ```
> 1. Spring AI 读取 application.yml 中的 MCP Client 配置
> 2. 发现 command: java, args: [-jar, xxx.jar]
> 3. 用 ProcessBuilder 启动 Server 进程：java -jar xxx.jar
> 4. 通过 Server 进程的 stdin/stdout 进行 JSON-RPC 2.0 通信
> 5. 获取 Server 注册的工具列表
> 6. AI 调用工具时，通过 stdin 发送请求，通过 stdout 接收结果
> ```
>
> 类比理解：就像你用命令行 `java -jar app.jar` 启动一个程序，然后通过键盘输入命令、从屏幕读取输出。Client 就是"键盘+屏幕"，Server 就是"程序"。

修改 smart 主项目的 `application.yml`，添加 MCP Client 配置：

```yaml
server:
  port: 8080

spring:
  ai:
    openai:
      api-key: ${DEEPSEEK_API_KEY:sk-your-api-key-here}
      base-url: https://api.deepseek.com
      chat:
        options:
          model: deepseek-chat
          temperature: 0.7
    # ⭐ MCP Client 配置
    mcp:
      client:
        servers:
          # 配置名为 "calculator" 的 MCP Server 连接
          # 这个名字是自定义的，后面注册工具时用不到（框架自动关联）
          calculator:
            # stdio 方式：用 java -jar 命令启动 Server 进程
            command: java
            args:
              - -jar
              # ⚠️ 这里填你实际的 jar 包路径！
              # 路径中不要有中文或空格
              - E:/HuaShan/mcp-calculator-server/target/mcp-calculator-server-0.0.1-SNAPSHOT.jar
```

> ⚠️ **配置要点**：
> - `command: java`：启动命令（Windows/Linux/macOS 都是 java）
> - `args` 中的 jar 路径必须是**绝对路径**，且不要有中文或空格
> - 确保 jar 文件已存在（Part 1 步骤 9 打包生成的）
> - 启动 smart 主项目时，Spring AI 会自动启动 MCP Server 进程

---

### 步骤13：创建 McpTestController + 注册工具（10:11-10:19）

> **💡 怎么让 AI 使用 MCP 工具？**
>
> Spring AI 1.0.0 中，MCP Client 会自动获取 Server 注册的工具列表。
> 但你需要告诉 ChatClient "这次对话可以使用哪些工具"——通过 `.functions()` 方法注册。
>
> 就像你给助手一份工具清单——"这次任务你可以用计算器和量角器"。助手看到清单后，遇到计算问题就会自动用计算器。
>
> `.functions("add", "subtract", "multiply", "divide", "average", "calculateDormitoryCapacity")`
> 这行代码就是在告诉 AI："你可以使用这 6 个工具"。

创建包 `com.huashan.smart.controller`，新建 `McpTestController.java`：

```java
package com.huashan.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * MCP 工具调用测试控制器
 *
 * 本 Controller 演示 AI 如何自动选择并调用 MCP 工具
 *
 * 核心流程：
 * 1. 用户发送问题（如 "123+456=?"）
 * 2. AI 看到可用的工具列表（从 @Tool 注解自动生成）
 * 3. AI 自己判断是否需要调用工具、调用哪个工具
 * 4. Spring AI 自动执行工具调用，把结果传回 AI
 * 5. AI 基于工具结果组织自然语言回答
 *
 * ⚠️ 关键：我们没有写任何 if-else 来判断用户问的是加法还是除法
 * AI 自己根据 @Tool 的 description 来决定调用哪个工具
 */
@RestController
@RequestMapping("/mcp")
public class McpTestController {

    private final ChatClient chatClient;

    /**
     * 构造器注入 ChatClient.Builder
     *
     * 这里用最基础的 ChatClient（不带 defaultSystem 和 ChatMemory）
     * 因为 MCP 工具调用测试不需要预设角色和多轮记忆
     */
    public McpTestController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * MCP 工具调用测试接口
     * GET /mcp/chat?message=123+456等于多少
     *
     * 参数解读：
     * - message：用户的问题
     *
     * 调用链解读：
     * chatClient.prompt()
     *     .user(message)                    → 设置用户问题
     *     .functions("add", "subtract",     → 注册可用工具列表
     *               "multiply", "divide",      AI 从这个列表中选择合适的工具
     *               "average",
     *               "calculateDormitoryCapacity")
     *     .call()                           → 同步调用
     *     .content()                        → 提取回复文本
     *
     * ⚠️ .functions() 的作用：
     * - 不调用 .functions()：AI 不知道有工具可用，只会用自己的知识回答
     * - 调用 .functions()：AI 知道有这些工具，遇到需要计算的问题会自动调用
     * - 只注册需要的工具：减少 AI 的决策负担，提高选择准确率
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                // 注册所有 MCP 工具——AI 会根据问题自动选择合适的工具
                .functions("add", "subtract", "multiply", "divide",
                           "average", "calculateDormitoryCapacity")
                .call()
                .content();
    }
}
```

> 💡 **`.functions()` vs `.defaultFunctions()` 的区别**：
> - `.functions("add", ...)`：每次调用时指定，适合按需注册不同工具
> - `.defaultFunctions("add", ...)`：在 AiConfig 中预设，所有调用自动带上
> - 本节课用 `.functions()` 演示，方便对比有无工具的差异

---

### 步骤14：测试 "123+456=?" → AI 自动调 add（10:19-10:25）

> **💡 测试时发生了什么？**
>
> ```
> 用户发送："123加456等于多少"
>
> AI 看到可用的工具列表：
>   - add: "执行加法：计算两个数的和。当用户询问两数之和时使用"
>   - subtract: "执行减法：计算两个数的差..."
>   - multiply: "执行乘法..."
>   - divide: "执行除法..."
>   - average: "计算平均值..."
>   - calculateDormitoryCapacity: "计算宿舍容量..."
>
> AI 思考：
>   "用户问'加' → 匹配 add 工具的描述'计算两个数的和'
>    参数：a=123, b=456
>    → 决定调用 add 工具"
>
> Spring AI 自动执行：
>   add(123, 456) → 返回 "579"
>
> AI 拿到结果后组织回答：
>   "123加456等于579"
> ```

1. 启动 smart 主项目
2. 观察控制台日志——Spring AI 会自动启动 MCP Server 进程
3. 浏览器访问：

```
http://localhost:8080/mcp/chat?message=123加456等于多少
```

预期结果：
```
123加456等于579
```

继续测试其他运算：

```
http://localhost:8080/mcp/chat?message=1000减去378等于多少
→ AI 调用 subtract(1000, 378) → 返回 622

http://localhost:8080/mcp/chat?message=25乘以4等于多少
→ AI 调用 multiply(25, 4) → 返回 100

http://localhost:8080/mcp/chat?message=10除以3等于多少
→ AI 调用 divide(10, 3) → 返回 3.333...
```

> ⚠️ **常见问题**：
> - 如果 AI 没有调用工具而是自己算：检查 `.functions()` 是否注册了工具名
> - 如果报 `Tool not found: add`：检查 MCP Server 的 jar 路径是否正确，Server 是否正常启动
> - 如果 AI 选错了工具：优化 `@Tool` 的 `description`，让描述更精确

---

### 步骤15：测试宿舍容量计算（10:25-10:32）

> **💡 这次测试的意义**
>
> 加减乘除是 AI 自己也能算的（虽然可能算错）。但宿舍容量计算是**业务专属工具**——AI 不知道 3 号楼有多少房间、每间住几人，它必须调用工具才能得到真实数据。
>
> 这才是 MCP 的核心价值——**让 AI 基于真实数据回答，而不是编造数据**。

```
http://localhost:8080/mcp/chat?message=3号楼有150间房每间住4人目前住了480人请问还有多少空床位入住率是多少
```

预期结果（AI 自动调用 `calculateDormitoryCapacity`）：
```
3号楼共有150间房，总容量600人，目前已住480人，空床位120个，入住率80.0%。
```

> 💡 **AI 决策过程**：
> - AI 看到用户问"空床位""入住率"→ 匹配 `calculateDormitoryCapacity` 工具的描述
> - AI 自动提取参数：building=3, totalRooms=150, capacityPerRoom=4, currentOccupancy=480
> - 调用工具，得到格式化的结果字符串
> - AI 将结果组织成自然语言回答

---

### 步骤16：AI 决策过程演示（10:32-10:38）

> **💡 不同问题 → AI 选不同工具**
>
> 这是最精彩的演示——我们什么都不用改，只需要问不同的问题，AI 就会自动选择不同的工具。
>
> 就像一个聪明的助手——你不用告诉他"遇到 A 用工具甲"，他看到工具清单后自己判断。

测试对比：

```
http://localhost:8080/mcp/chat?message=88加12等于多少
→ AI 选择 add 工具 → 返回 100

http://localhost:8080/mcp/chat?message=5号楼200间房每间6人住了960人请计算容量
→ AI 选择 calculateDormitoryCapacity 工具 → 返回详细容量信息

http://localhost:8080/mcp/chat?message=今天天气怎么样
→ AI 不调用任何工具 → 用自己的知识回答

http://localhost:8080/mcp/chat?message=85、90、78、92这四个数的平均分是多少
→ AI 选择 average 工具 → 返回 86.25
```

> 💡 **核心认知**：
> - **AI 自己决定是否调用工具**——不需要写 if-else
> - **AI 自己决定调用哪个工具**——基于 `@Tool` 的 `description`
> - **AI 自己提取参数**——从用户自然语言中提取参数值
> - **不相关的问题不会调工具**——AI 有判断力

---

### 步骤17：添加 calculateEmptyBeds 空床位工具 + 测试（10:38-10:45）

> **💡 为什么还需要 calculateEmptyBeds？**
>
> `calculateDormitoryCapacity` 返回的信息很多（总容量、空床位、入住率），但有时候用户只想知道"还有多少空床位"。
>
> 提供更精准的工具，AI 的回答就更直接。就像公司里有"综合查询"和"快速查询"两个入口——简单问题走快速通道。

回到 `mcp-calculator-server` 项目，在 `CalculatorTool.java` 中添加：

```java
    /**
     * 空床位计算工具
     *
     * 比 calculateDormitoryCapacity 更轻量——只关注空床位数
     * 当用户只问"还有多少空床位"时，AI 会选择这个更精准的工具
     *
     * ⚠️ 工具粒度设计原则：
     * - 综合工具（calculateDormitoryCapacity）：一次返回所有信息，适合全面查询
     * - 精准工具（calculateEmptyBeds）：只返回关键信息，适合简单查询
     * - 提供两种粒度的工具，让 AI 根据问题复杂度自主选择
     */
    @Tool(name = "calculateEmptyBeds",
          description = "计算空床位数：根据楼号、总容量和已住人数，计算空床位数。当用户只询问空床位数量时使用，比calculateDormitoryCapacity更简洁")
    public String calculateEmptyBeds(
            @ToolParam(name = "building", description = "楼号，例如：3") String building,
            @ToolParam(name = "totalCapacity", description = "总容量（总床位数）") int totalCapacity,
            @ToolParam(name = "currentOccupancy", description = "当前已住人数") int currentOccupancy) {
        // 计算空床位数
        int emptyBeds = totalCapacity - currentOccupancy;
        return String.format("楼号：%s，空床位：%d个", building, emptyBeds);
    }
```

添加后需要重新打包：

```bash
cd mcp-calculator-server
mvn clean package -DskipTests
```

重启 smart 主项目测试：

```
http://localhost:8080/mcp/chat?message=3号楼总共600个床位住了480人还有多少空床位
→ AI 选择 calculateEmptyBeds 工具 → 返回 "楼号：3，空床位：120个"

http://localhost:8080/mcp/chat?message=3号楼150间房每间4人住了480人请全面分析
→ AI 选择 calculateDormitoryCapacity 工具 → 返回全面信息
```

---

### 步骤18：优化 @Tool 的 description 提升准确率（10:45-10:50）

> **💡 description 优化是 MCP 开发中最重要的技巧**
>
> `@Tool` 的 `description` 是 AI 决策的唯一依据。写得不好，AI 可能：
> - 该调工具时不调（描述太模糊，AI 不知道这是干什么的）
> - 不该调时调了（描述太宽泛，AI 误以为需要调用）
> - 调错了工具（多个工具描述相似，AI 选错了）
>
> **description 优化的三个原则**：
>
> | 原则 | 说明 | 示例 |
> |------|------|------|
> | 说明做什么 | 工具的核心功能 | "执行加法：计算两个数的和" |
> | 说明何时用 | 触发条件 | "当用户询问两数之和时使用" |
> | 区分近似工具 | 和其他工具的不同 | "比 calculateDormitoryCapacity 更简洁" |

**优化前 vs 优化后对比**：

```java
// ❌ 优化前：description 太模糊
@Tool(description = "计算")
// AI 看到后：这是加法？减法？还是什么？→ 可能选错工具

// ✅ 优化后：描述清晰，包含功能和触发条件
@Tool(name = "add", description = "执行加法：计算两个数的和。当用户询问两数之和时使用")
// AI 看到后：用户问加法 → 精准选择 add 工具
```

**calculateDormitoryCapacity vs calculateEmptyBeds 的描述对比**：

```java
// calculateDormitoryCapacity：全面信息，适合"全面分析"类问题
description = "计算宿舍容量：根据楼号、房间数、每间容量和已住人数，计算总容量、空床位数和入住率。当用户询问宿舍容量、入住率、空床位时使用"

// calculateEmptyBeds：精简信息，适合"只问空床位"类问题
description = "计算空床位数：根据楼号、总容量和已住人数，计算空床位数。当用户只询问空床位数量时使用，比calculateDormitoryCapacity更简洁"
```

> 💡 **description 优化总结**：
> 1. 写清楚"工具做什么"——功能描述
> 2. 写清楚"什么时候用"——触发条件
> 3. 写清楚"和其他工具的区别"——区分条件
> 4. 用中文写——DeepSeek 对中文理解更好

---

## 课间休息（10:50-11:00）

---

## Part 3：邮件 MCP + 多模型（11:00-11:50）

### 步骤19：MCP Server 添加邮件依赖（11:00-11:04）

> **💡 为什么要在 MCP Server 中添加邮件功能？**
>
> 之前我们做的计算器工具是"无状态"的——不需要连接外部服务。但邮件发送是"有状态"的——需要连接 SMTP 服务器。
>
> MCP 工具可以封装任何外部操作：发邮件、查数据库、调 API。所有"AI 不会做但工具能做"的事，都可以做成 MCP 工具。
>
> 就像手机 App 一样——天气 App 查天气、邮件 App 发邮件、计算器 App 做运算。MCP 工具就是 AI 的"App 生态"。

打开 `mcp-calculator-server` 项目的 `pom.xml`，添加邮件依赖：

```xml
        <!-- Spring Boot Mail Starter -->
        <!-- 提供 JavaMailSender，用于发送邮件 -->
        <!-- MCP Server 的邮件工具依赖它来通过 SMTP 发送邮件 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
```

---

### 步骤20：配置 SMTP（11:04-11:10）

> **💡 什么是 SMTP？**
>
> SMTP（Simple Mail Transfer Protocol）是发送邮件的协议。就像寄信——你需要知道邮局的地址（SMTP 服务器）、你有合法的寄件人身份（授权码）、信封上要写收件人地址。
>
> QQ 邮箱的 SMTP 配置步骤：
> 1. 登录 QQ 邮箱 → 设置 → 账户
> 2. 找到"POP3/SMTP 服务" → 开启
> 3. 按提示用手机发短信验证
> 4. 获取"授权码"（**不是 QQ 密码！**）
> 5. 记录：smtp.qq.com，端口 465，授权码

修改 `mcp-calculator-server` 项目的 `application.yml`：

```yaml
server:
  port: 8081

spring:
  ai:
    mcp:
      server:
        name: calculator-server
        version: 1.0.0
  # ⭐ SMTP 邮件配置
  mail:
    # QQ 邮箱的 SMTP 服务器地址
    host: smtp.qq.com
    # SSL 端口（465），不是 587
    port: 465
    # 你的 QQ 邮箱地址
    username: your-qq@qq.com
    # ⚠️ 授权码（不是 QQ 密码！）
    # 在 QQ 邮箱设置 → 账户 → POP3/SMTP 服务 中获取
    password: your-auth-code
    # 邮件协议
    protocol: smtp
    # SMTP 连接属性
    properties:
      mail:
        smtp:
          # 启用 SMTP 认证
          auth: true
          # 启用 SSL 加密（QQ 邮箱要求 SSL）
          ssl:
            enable: true
          # SSL Socket 工厂配置
          socketFactory:
            class: javax.net.ssl.SSLSocketFactory
            port: 465
```

> ⚠️ **SMTP 配置要点**：
> - `password` 填**授权码**，不是 QQ 密码！授权码在 QQ 邮箱设置中申请
> - 端口用 **465**（SSL），不是 587（STARTTLS）
> - `ssl.enable: true` 必须开启，QQ 邮箱强制要求 SSL
> - 也可以用环境变量传入：`password: ${MAIL_AUTH_CODE:your-auth-code}`

---

### 步骤21：创建 EmailTool——sendCheckInEmail（11:10-11:22）

> **💡 为什么用 MimeMessage 而不是 SimpleMailMessage？**
>
> `SimpleMailMessage` 只能发送纯文本邮件，不支持 HTML 格式。
> `MimeMessage` 支持富文本（HTML）、附件等，邮件内容更美观。
>
> 入住通知邮件通常需要格式化展示（表格、链接等），所以用 MimeMessage 更合适。

在 `mcp-calculator-server` 项目中创建 `com.huashan.mcp.tool.EmailTool.java`：

```java
package com.huashan.mcp.tool;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * 邮件发送 MCP 工具
 *
 * 提供 AI 可调用的邮件发送功能。
 * 当用户说"发一封邮件"或"发送入住通知"时，AI 自动调用此工具。
 *
 * ⚠️ 邮件工具的设计要点：
 * 1. 工具方法内部必须 try-catch，不能让异常抛到 MCP 框架层
 * 2. 返回清晰的成功/失败信息，方便 AI 转达给用户
 * 3. 使用 MimeMessage 支持富文本邮件
 */
@Component  // 让 Spring 管理，Spring AI 自动注册为 MCP 工具
public class EmailTool {

    // JavaMailSender：Spring Boot Mail 自动配置的邮件发送器
    // 它封装了 SMTP 连接、认证、发送等所有底层操作
    private final JavaMailSender mailSender;

    // 发件人邮箱（从配置文件中读取，也可以硬编码）
    private static final String FROM_EMAIL = "your-qq@qq.com";

    /**
     * 构造器注入 JavaMailSender
     *
     * Spring Boot Mail Starter 自动配置了 JavaMailSender Bean
     * 根据 application.yml 中的 spring.mail 配置创建
     * 我们只需要注入即可，不需要手动创建
     */
    public EmailTool(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 发送入住通知邮件
     *
     * @Tool 注解读解：
     * - description 要写清楚"什么场景下使用"和"输入什么参数"
     * - 这样 AI 才能准确判断何时调用这个工具
     *
     * ⚠️ 邮件发送是"有副作用"的操作——发出去就收不回来了
     * 在测试环境中，建议用测试邮箱或 mock 方式
     */
    @Tool(name = "sendCheckInEmail",
          description = "发送入住通知邮件：给指定学生发送入住确认邮件，包含楼号、房间号等信息。当用户要求发送入住通知、入住确认邮件时使用")
    public String sendCheckInEmail(
            @ToolParam(name = "to", description = "收件人邮箱地址") String to,
            @ToolParam(name = "studentName", description = "学生姓名") String studentName,
            @ToolParam(name = "building", description = "楼号，例如：3号楼") String building,
            @ToolParam(name = "room", description = "房间号，例如：301") String room) {
        try {
            // 创建 MimeMessage 对象（支持 HTML 富文本）
            MimeMessage message = mailSender.createMimeMessage();
            // MimeMessageHelper：简化 MimeMessage 的操作
            // 第二个参数 true 表示支持 multipart（多部分，即支持 HTML）
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 设置发件人（必须是配置的 QQ 邮箱，否则 SMTP 会拒绝）
            helper.setFrom(FROM_EMAIL);
            // 设置收件人
            helper.setTo(to);
            // 设置邮件主题
            helper.setSubject("入住通知 - " + building + room + "室");

            // 设置邮件正文（HTML 格式）
            // 使用 HTML 可以让邮件更美观（加粗、换行、表格等）
            String htmlContent = """
                    <div style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2 style="color: #2c3e50;">入住通知</h2>
                        <p><strong>%s</strong> 同学，您好！</p>
                        <p>您已成功入住 <strong>%s %s室</strong>，请按时办理入住手续。</p>
                        <hr style="border: 1px solid #eee;">
                        <p style="color: #888; font-size: 12px;">此邮件由智能宿舍管理系统自动发送，请勿回复。</p>
                    </div>
                    """.formatted(studentName, building, room);
            // 第一个参数：HTML 内容
            // 第二个参数 true：表示内容是 HTML 格式
            helper.setText(htmlContent, true);

            // 发送邮件
            mailSender.send(message);

            // 返回成功信息（AI 会把这个信息转达给用户）
            return "入住通知邮件发送成功！收件人：" + to + "，学生：" + studentName;

        } catch (MessagingException e) {
            // ⚠️ 必须捕获异常！不能让异常抛到 MCP 框架层
            // 返回友好的错误信息
            return "邮件发送失败：" + e.getMessage();
        }
    }
}
```

> 💡 **EmailTool 核心设计**：
> - `JavaMailSender`：Spring Boot Mail 自动配置的邮件发送器
> - `MimeMessage` + `MimeMessageHelper`：支持 HTML 富文本邮件
> - `helper.setText(htmlContent, true)`：第二个参数 `true` 表示内容是 HTML
> - try-catch 包裹：不让异常抛到 MCP 框架层
> - 返回清晰的成功/失败字符串：AI 基于这个字符串组织回答

---

### 步骤22：注册邮件工具 + 测试（11:22-11:28）

重新打包 MCP Server：

```bash
cd mcp-calculator-server
mvn clean package -DskipTests
```

在 smart 主项目的 `McpTestController.java` 中，更新 `.functions()` 注册邮件工具：

```java
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                // 注册所有 MCP 工具（包括新增的邮件工具）
                .functions("add", "subtract", "multiply", "divide",
                           "average", "calculateDormitoryCapacity",
                           "calculateEmptyBeds", "sendCheckInEmail")  // ← 新增邮件工具
                .call()
                .content();
    }
```

重启 smart 主项目，测试邮件工具：

```
http://localhost:8080/mcp/chat?message=请给zhangsan@example.com发送入住通知，张三同学住在3号楼301室
```

预期结果（AI 自动调用 `sendCheckInEmail`）：
```
入住通知邮件已成功发送给张三同学（zhangsan@example.com），通知他入住3号楼301室。
```

> ⚠️ **测试注意**：
> - 邮件发送需要真实的 SMTP 配置才能工作
> - 如果 SMTP 配置不正确（授权码错误、端口错误），会发送失败
> - 测试时建议用真实可用的邮箱地址
> - AI 会把工具返回的错误信息转达给用户

---

### 步骤23：多模型切换——创建 MultiModelConfig（11:28-11:36）

> **💡 为什么需要多模型？**
>
> DeepSeek 提供了两个模型：
> - **DeepSeek V3**（`deepseek-chat`）：快速、便宜，适合日常对话和简单任务
> - **DeepSeek R1**（`deepseek-reasoner`）：慢但准，擅长复杂推理（数学、逻辑、代码）
>
> 就像公司里有两个员工：
> - 小 V（V3）：干活快，简单任务效率高，但复杂问题可能出错
> - 小 R（R1）：干活慢，但复杂问题分析透彻
>
> **智能路由**：简单问题让 V3 处理（快），复杂推理让 R1 处理（准）。

> **💡 怎么实现多模型？**
>
> Spring AI 的统一抽象让多模型切换非常简单——创建多个 `OpenAiChatModel` Bean，各自连接不同的模型。
>
> 就像 AiConfig 中我们为"宿舍助手"和"编程老师"创建了不同的 ChatClient Bean 一样——
> 这次我们为 V3 和 R1 创建不同的 ChatModel Bean。

在 smart 主项目中创建 `com.huashan.smart.config.MultiModelConfig.java`：

```java
package com.huashan.smart.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 多模型配置类
 *
 * 本类配置两个 AI 模型 Bean：
 * 1. deepseekV3ChatModel（@Primary）—— 快速模型，适合日常对话
 * 2. deepseekR1ChatModel              —— 推理模型，适合复杂推理
 *
 * ⚠️ 关键设计：
 * - 两个模型使用相同的 base-url（https://api.deepseek.com）
 * - 但使用不同的 model 名：deepseek-chat（V3）vs deepseek-reasoner（R1）
 * - @Primary 标注默认模型（V3），@Qualifier 指定非默认模型（R1）
 *
 * 类比理解：
 * 就像公司里有两个员工——V3 是普通员工（干活快），R1 是专家（干活准）
 * 默认找普通员工（@Primary），遇到难题找专家（@Qualifier）
 */
@Configuration
public class MultiModelConfig {

    // 从 application.yml 读取 DeepSeek API 配置
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    /**
     * DeepSeek V3 ChatModel Bean（快速模型）
     *
     * @Primary 注解的作用：
     * - 当有多个同类型 Bean 时，标记哪个是默认的
     * - 其他类注入 ChatModel 时不指定 @Qualifier，就注入这个
     * - V3 作为默认模型，因为大部分场景用 V3 就够了
     *
     * 构建方式解读：
     * 1. OpenAiApi.builder()
     *       .apiKey(apiKey)       → API 密钥（从配置文件读取）
     *       .baseUrl(baseUrl)     → DeepSeek 的 API 地址
     *       .build()
     *    → 创建 OpenAI API 客户端（负责 HTTP 通信）
     *
     * 2. OpenAiChatOptions.builder()
     *       .model("deepseek-chat")     → 模型名：V3（快速模型）
     *       .temperature(0.7)           → 温度：0.7（适度创造）
     *       .build()
     *    → 创建聊天选项（指定用哪个模型、温度等参数）
     *
     * 3. OpenAiChatModel.builder()
     *       .openAiApi(api)             → 注入 API 客户端
     *       .defaultOptions(options)    → 注入默认选项
     *       .build()
     *    → 创建 ChatModel（AI 模型客户端）
     */
    @Bean("deepseekV3ChatModel")
    @Primary  // 标记为默认 Bean：注入 ChatModel 时不指定名称就用这个
    public OpenAiChatModel deepseekV3ChatModel() {
        // 1. 创建 API 客户端（负责与 DeepSeek 服务器通信）
        OpenAiApi api = OpenAiApi.builder()
                .apiKey(apiKey)    // API 密钥
                .baseUrl(baseUrl)  // DeepSeek API 地址
                .build();

        // 2. 创建聊天选项（指定模型和参数）
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("deepseek-chat")   // V3 模型名
                .temperature(0.7)          // 温度参数
                .build();

        // 3. 创建 ChatModel Bean
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(options)
                .build();
    }

    /**
     * DeepSeek R1 ChatModel Bean（推理模型）
     *
     * 没有 @Primary 注解 → 不是默认 Bean
     * 使用时需要通过 @Qualifier("deepseekR1ChatModel") 指定注入
     *
     * 和 V3 的区别：
     * - model = "deepseek-reasoner"（R1 模型名）
     * - temperature 不设置（R1 使用默认值）
     * - R1 内部会进行"思维链推理"，输出更准确但更慢
     */
    @Bean("deepseekR1ChatModel")
    public OpenAiChatModel deepseekR1ChatModel() {
        OpenAiApi api = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("deepseek-reasoner")  // R1 模型名（推理模型）
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(options)
                .build();
    }
}
```

> ⚠️ **关键点**：
> - 两个 Bean 都用 `OpenAiChatModel` 类型，但连接不同模型
> - `@Primary` 标记 V3 为默认——注入 `ChatModel` 时不指定 `@Qualifier` 就用 V3
> - `@Qualifier("deepseekR1ChatModel")` 指定 R1——需要时明确指定
> - 两个模型使用相同的 API Key 和 base-url，只是 model 名不同

---

### 步骤24：两个 ChatModel Bean + @Primary + @Qualifier（11:36-11:42）

> **💡 @Primary 和 @Qualifier 的配合**
>
> 当 Spring 容器中有多个同类型 Bean 时：
>
> | 注入方式 | 效果 |
> |---------|------|
> | `@Autowired ChatModel model` | 注入 @Primary 标注的 Bean（V3） |
> | `@Qualifier("deepseekV3ChatModel") ChatModel model` | 显式指定注入 V3 |
> | `@Qualifier("deepseekR1ChatModel") ChatModel model` | 显式指定注入 R1 |
>
> 类比理解：
> - @Primary = 默认选项（"如果没有特别说明，就用这个"）
> - @Qualifier = 特别指定（"这次我明确要用那个"）
>
> 这和 Day1 中 AiConfig 的 `dormitoryChatClient` / `tutorChatClient` 是同样的模式——
> 多个同类型 Bean，通过名称区分，通过 @Qualifier 注入。

创建 `com.huashan.smart.controller.MultiModelController.java`：

```java
package com.huashan.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多模型对话控制器
 *
 * 演示如何使用不同模型处理不同类型的问题：
 * - V3（deepseek-chat）：快速、便宜，适合日常对话
 * - R1（deepseek-reasoner）：慢但准，适合复杂推理
 *
 * 核心知识点：
 * 1. @Qualifier 注入不同的 ChatModel Bean
 * 2. ChatClient.builder(chatModel) 指定底层模型
 * 3. 简单问题用 V3，复杂推理用 R1
 */
@RestController
@RequestMapping("/model")
public class MultiModelController {

    // V3 快速模型
    private final ChatClient v3Client;
    // R1 推理模型
    private final ChatClient r1Client;

    /**
     * 构造器注入两个 ChatModel Bean
     *
     * ⚠️ 注意：这里注入的是 ChatModel（底层模型），不是 ChatClient
     * ChatClient 需要基于 ChatModel 来构建：
     * ChatClient.builder(chatModel).build()
     *
     * @Qualifier 的作用：
     * - "deepseekV3ChatModel" → 注入 V3 模型
     * - "deepseekR1ChatModel" → 注入 R1 模型
     */
    public MultiModelController(
            @Qualifier("deepseekV3ChatModel") ChatModel v3Model,
            @Qualifier("deepseekR1ChatModel") ChatModel r1Model) {
        // 基于 ChatModel 构建 ChatClient
        // ChatClient.builder(chatModel) 指定底层使用的模型
        this.v3Client = ChatClient.builder(v3Model).build();
        this.r1Client = ChatClient.builder(r1Model).build();
    }

    /**
     * V3 快速模型对话
     * GET /model/v3?message=你好
     *
     * 适合场景：日常对话、简单问答、信息查询
     */
    @GetMapping("/v3")
    public String chatWithV3(@RequestParam String message) {
        return v3Client.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * R1 推理模型对话
     * GET /model/r1?message=请推导勾股定理
     *
     * 适合场景：数学推导、逻辑分析、代码调试
     * R1 内部会进行"思维链推理"——先思考再回答，更准确但更慢
     */
    @GetMapping("/r1")
    public String chatWithR1(@RequestParam String message) {
        return r1Client.prompt()
                .user(message)
                .call()
                .content();
    }
}
```

---

### 步骤25：智能路由——关键词判断选 V3 或 R1（11:42-11:48）

> **💡 什么是智能路由？**
>
> 智能路由：根据用户问题的特征，自动选择最合适的模型。
>
> 就像客服系统——简单问题转给 AI 客服（快），复杂问题转给人工客服（准）。
>
> 实现方式：
> 1. **关键词匹配**（本节课实现）：根据问题中是否包含"推理""推导""分析"等关键词
> 2. **AI 分类**（进阶）：用一个小模型先判断问题类型，再路由到对应模型
> 3. **混合路由**（生产级）：结合关键词 + 分类 + 负载均衡

在 `MultiModelController.java` 中添加智能路由接口：

```java
    /**
     * 智能路由：根据问题关键词自动选择模型
     * GET /model/smart?message=请推导勾股定理
     *
     * 路由策略（关键词匹配）：
     * - 包含推理关键词（"推导""分析""证明""解释原因""为什么"等）→ R1
     * - 其他情况 → V3（默认快速模型）
     *
     * ⚠️ 这是简化版的路由策略，实际生产中可以用：
     * 1. 更丰富的关键词库
     * 2. AI 分类模型判断问题类型
     * 3. 基于历史对话的上下文路由
     */
    @GetMapping("/smart")
    public String smartChat(@RequestParam String message) {
        // 判断是否需要推理模型
        if (needsReasoning(message)) {
            // 复杂推理 → 用 R1（慢但准）
            return r1Client.prompt()
                    .user(message)
                    .call()
                    .content();
        } else {
            // 简单问题 → 用 V3（快且便宜）
            return v3Client.prompt()
                    .user(message)
                    .call()
                    .content();
        }
    }

    /**
     * 判断问题是否需要推理模型
     *
     * 通过关键词匹配判断问题复杂度：
     * - 包含推理类关键词 → 需要推理模型（R1）
     * - 其他 → 用快速模型（V3）即可
     *
     * 实际项目中，可以扩展为：
     * 1. 更大的关键词库
     * 2. 问题长度判断（长问题可能需要推理）
     * 3. AI 预分类（用 V3 先判断问题类型）
     */
    private boolean needsReasoning(String message) {
        // 推理类关键词列表
        String[] reasoningKeywords = {
                "推导", "证明", "分析", "解释原因",
                "为什么", "原理", "逻辑", "深入",
                "比较", "评估", "论证", "推理"
        };
        // 遍历关键词，只要匹配一个就判定需要推理
        for (String keyword : reasoningKeywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
```

---

### 步骤26：测试简单问题→V3快 / 复杂推理→R1准（11:48-11:50）

**测试 V3 快速模型**：

```
http://localhost:8080/model/v3?message=你好，请介绍一下你自己
→ V3 快速回复（1-2秒）

http://localhost:8080/model/r1?message=你好，请介绍一下你自己
→ R1 也会回复，但更慢（3-5秒），因为 R1 会先"思考"
```

**测试 R1 推理模型**：

```
http://localhost:8080/model/r1?message=请推导勾股定理
→ R1 深入推理，给出详细的证明过程

http://localhost:8080/model/v3?message=请推导勾股定理
→ V3 也能回答，但推理过程可能不如 R1 严谨
```

**测试智能路由**：

```
http://localhost:8080/model/smart?message=宿舍几点熄灯
→ 关键词不匹配 → 走 V3（快速回复）

http://localhost:8080/model/smart?message=请分析为什么3号楼入住率比其他楼低
→ 匹配"分析"关键词 → 走 R1（深度推理）

http://localhost:8080/model/smart?message=请推导一元二次方程求根公式
→ 匹配"推导"关键词 → 走 R1（数学推导）
```

> 💡 **多模型总结**：
> - **V3**：快速便宜，日常对话首选
> - **R1**：慢但准，复杂推理用
> - **智能路由**：关键词匹配自动选择，让用户无感切换
> - **Spring AI 统一抽象**：换模型只改配置/Bean，代码零修改

---

## Part 4：三天总结（11:50-12:00）

### 三天知识串联（11:50-11:55）

> **💡 三天课程知识地图——从"认识 AI"到"让 AI 干活"**

```
╔═══════════════════════════════════════════════════════════════╗
║                    三天课程知识地图                              ║
╠═══════════════════════════════════════════════════════════════╣
║                                                               ║
║  Day1: Spring AI 项目搭建 + 五种同步调用                        ║
║  ├── 项目搭建（Spring Boot 3.5 + Spring AI 1.0.0 GA）          ║
║  ├── 最简调用 .prompt().user().call().content()                ║
║  ├── System Prompt .system("角色")                             ║
║  ├── ChatMemory 多轮对话（Advisor 自动维护上下文）               ║
║  ├── 参数化模板 .user(u -> u.text("{key}").param())            ║
║  ├── 结构化输出 .entity(StudentInfo.class)                      ║
║  ├── 多角色管理 @Qualifier 多个 ChatClient Bean                 ║
║  └── RTF 框架（Role-Task-Format）+ 完整 Service 封装            ║
║                                                               ║
║  Day2: 流式输出 + 提示词工程 + SSE                              ║
║  ├── 流式 Flux 输出 .stream().content() → Flux<String>         ║
║  ├── 提示词工程（Few-Shot / CoT / RTF 框架）                    ║
║  └── SSE 服务器推送（HTTP/WebSocket/SSE 对比 + SseEmitter）     ║
║                                                               ║
║  Day3: MCP 全实操 + 多模型 + 总结                               ║
║  ├── MCP Server 开发（独立项目 + @Tool + @ToolParam）            ║
║  ├── MCP Client 配置（stdio 方式连接 Server）                   ║
║  ├── AI 自动工具调用（.functions() 注册 + AI 自动决策）          ║
║  ├── 邮件 MCP（JavaMailSender + MimeMessage）                   ║
║  ├── 多模型切换（V3 快速 + R1 推理 + @Primary + @Qualifier）    ║
║  └── 智能路由（关键词匹配自动选模型）                             ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝
```

**核心知识串联**：

| 序号 | 核心概念 | Day1 | Day2 | Day3 |
|------|---------|------|------|------|
| 1 | ChatClient 链式调用 | `.prompt().user().call().content()` | `.stream().content()` | `.functions().call().content()` |
| 2 | 角色管理 | `defaultSystem("角色")` | RTF 框架 | 多模型不同角色 |
| 3 | 上下文管理 | ChatMemory + Advisor | SSE 会话 | MCP 工具上下文 |
| 4 | 输出方式 | 同步 `.call()` | 流式 `.stream()` | MCP 工具结果 |
| 5 | Bean 管理 | @Qualifier 多角色 | — | @Primary + @Qualifier 多模型 |

**三个关键认知**：

1. **AI 的本质是"下一个词预测器"**——它很强大但不理解，会编造数据
2. **涉及数据必须查真实来源**——MCP 让 AI 能调用工具获取真实数据，解决幻觉问题
3. **Spring AI 统一抽象**——换模型改配置，代码零修改

---

### 课后延伸方向（11:55-12:00）

> **💡 课程结束了，但学习没有结束**

**1. 深入 Spring AI**

```
- 官方文档：https://docs.spring.io/spring-ai/reference/
- 学习 RAG（检索增强生成）：让 AI 基于文档回答
- 学习向量数据库：pgvector、Chroma、Milvus
- 学习 Agent：让 AI 自主规划和执行复杂任务
```

**2. 深入 MCP**

```
- 官方文档：https://modelcontextprotocol.io/
- 学习 SSE 传输方式（替代 stdio，适合远程部署）
- 学习 Resources 和 Prompts 能力（MCP 不只有 Tools）
- 学习 MCP 工具市场：复用社区工具，不需要自己开发
```

**3. 深入提示词工程**

```
- 学习更多技巧：ReAct、Self-Ask、Tree of Thoughts
- 学习 Prompt 测试和评估
- 学习 Prompt 版本管理
```

**4. 项目实战方向**

```
- 把 MCP 工具连接真实数据库（MyBatis / JPA）
- 实现完整的 RAG：AI 基于文档回答问题
- 实现多轮对话 + 工具调用的完整前端界面
- 实现更智能的路由策略（AI 分类 + 负载均衡）
```

> 💡 **最重要的三个带走**：
> 1. **@Tool 注解**——定义工具只需一个注解，Spring AI 自动注册，AI 自动调用
> 2. **MCP 解耦**——Server 提供工具，Client 使用工具，职责分离
> 3. **统一抽象**——Spring AI 让你不绑定任何模型，今天 DeepSeek，明天无痛切换

---

## Day3 完整项目文件清单

### mcp-calculator-server（独立项目）

```
mcp-calculator-server/
├── pom.xml
└── src/
    └── main/
        ├── java/com/huashan/mcp/
        │   ├── McpCalculatorServerApplication.java
        │   └── tool/
        │       ├── CalculatorTool.java       ← 计算器工具（add/subtract/multiply/divide/average/calculateDormitoryCapacity/calculateEmptyBeds）
        │       └── EmailTool.java            ← 邮件工具（sendCheckInEmail）
        └── resources/
            └── application.yml               ← MCP Server + SMTP 配置
```

### smart（主项目新增/修改的文件）

```
smart/src/main/
├── java/com/huashan/smart/
│   ├── config/
│   │   ├── AiConfig.java                    ← （已有）宿舍助手 + 编程老师 ChatClient
│   │   └── MultiModelConfig.java            ← 🆕 多模型配置（V3 + R1）
│   └── controller/
│       ├── ...（Day1/Day2 已有的 Controller）
│       ├── McpTestController.java           ← 🆕 MCP 工具调用测试
│       └── MultiModelController.java        ← 🆕 多模型对话 + 智能路由
└── resources/
    └── application.yml                      ← 📝 新增 MCP Client 配置
```

---

## Day3 知识点总结

| 序号 | 知识点 | 核心代码 | 关键要点 |
|------|--------|---------|---------|
| 1 | MCP 概念 | USB-C 类比 + Client-Server 架构 | MCP 是开放标准，让 AI 能调用工具 |
| 2 | MCP Server 项目 | `spring-ai-starter-mcp-server` | 独立项目，打包成 jar |
| 3 | @Tool 注解 | `@Tool(name, description)` | description 是 AI 决策依据 |
| 4 | @ToolParam 注解 | `@ToolParam(name, description)` | 帮助 AI 理解参数含义 |
| 5 | MCP Client 配置 | `spring.ai.mcp.client.servers` | stdio 方式：command + args 启动 jar |
| 6 | 工具注册 | `.functions("add", "subtract", ...)` | 注册后 AI 自动选择调用 |
| 7 | AI 自动决策 | 无 if-else | AI 根据 description 自己选工具 |
| 8 | description 优化 | 写清楚"做什么+何时用+区别" | 描述越精准，AI 选择越准确 |
| 9 | 邮件 MCP | JavaMailSender + MimeMessage | try-catch 包裹，返回清晰信息 |
| 10 | 多模型 | @Primary + @Qualifier | V3 快速 + R1 推理，Bean 名区分 |
| 11 | 智能路由 | 关键词匹配选模型 | 简单→V3，推理→R1 |
| 12 | Spring AI 统一抽象 | 换模型只改配置 | 代码零修改，不绑定任何厂商 |
