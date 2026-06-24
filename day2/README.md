# Day2 - 流式输出与前端AI实现

## 任务目标

实现 SSE（Server-Sent Events）流式输出，前端实时显示 AI 回复。

## 完成内容

**教程文档**: `Day2_流式输出与前端AI实现.md`

## 技术点

- Spring AI 流式输出（`Flux` / `Stream`）
- SSE（Server-Sent Events）协议
- 前端 EventSource 实时接收
- 打字机效果实现

## 核心代码

```java
// 后端流式输出
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> streamChat(@RequestParam String message) {
    return chatClient.prompt()
            .user(message)
            .stream()
            .content();
}
```

```javascript
// 前端接收
const eventSource = new EventSource('/api/chat/stream?message=你好');
eventSource.onmessage = (event) => {
    content += event.data;  // 逐字显示
};
```
