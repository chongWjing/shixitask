#!/bin/bash

# 花之恋电商平台 - 自动化测试脚本

echo "========================================="
echo "  花之恋电商平台 - 完整测试"
echo "========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 测试计数器
PASS=0
FAIL=0

# 测试函数
test_api() {
    local name=$1
    local method=$2
    local url=$3
    local data=$4
    local token=$5

    echo -e "\n${YELLOW}[测试] $name${NC}"

    if [ "$method" = "GET" ]; then
        if [ -n "$token" ]; then
            response=$(curl -s -H "Authorization: Bearer $token" "$url")
        else
            response=$(curl -s "$url")
        fi
    elif [ "$method" = "POST" ]; then
        if [ -n "$token" ]; then
            response=$(curl -s -X POST -H "Content-Type: application/json; charset=UTF-8" -H "Authorization: Bearer $token" -d "$data" "$url")
        else
            response=$(curl -s -X POST -H "Content-Type: application/json; charset=UTF-8" -d "$data" "$url")
        fi
    elif [ "$method" = "PUT" ]; then
        response=$(curl -s -X PUT -H "Content-Type: application/json; charset=UTF-8" -H "Authorization: Bearer $token" -d "$data" "$url")
    elif [ "$method" = "DELETE" ]; then
        response=$(curl -s -X DELETE -H "Authorization: Bearer $token" "$url")
    fi

    # 检查响应
    if echo "$response" | grep -q '"code":200'; then
        echo -e "${GREEN}✓ 通过${NC}"
        ((PASS++))
    else
        echo -e "${RED}✗ 失败${NC}"
        echo "响应: $response"
        ((FAIL++))
    fi
}

# 1. 环境检查
echo -e "\n${YELLOW}===== 1. 环境检查 =====${NC}"

echo -e "\n检查 MySQL..."
if netstat -an 2>/dev/null | grep -q "3306"; then
    echo -e "${GREEN}✓ MySQL 运行中${NC}"
else
    echo -e "${RED}✗ MySQL 未运行${NC}"
    exit 1
fi

echo -e "\n检查后端..."
if curl -s http://localhost:8080/api/product/list | grep -q '"code":200'; then
    echo -e "${GREEN}✓ 后端运行中${NC}"
else
    echo -e "${RED}✗ 后端未运行${NC}"
    exit 1
fi

echo -e "\n检查前端..."
if curl -s http://localhost:5173 | grep -q "app"; then
    echo -e "${GREEN}✓ 前端运行中${NC}"
else
    echo -e "${YELLOW}⚠ 前端可能未运行${NC}"
fi

# 2. 用户模块测试
echo -e "\n${YELLOW}===== 2. 用户模块测试 =====${NC}"

test_api "用户注册" "POST" "http://localhost:8080/api/user/register" \
    '{"username":"testuser_'$RANDOM'","password":"123456","phone":"13800001234"}'

test_api "普通用户登录" "POST" "http://localhost:8080/api/user/login" \
    '{"username":"test","password":"123456"}'

test_api "管理员登录" "POST" "http://localhost:8080/api/user/login" \
    '{"username":"admin","password":"123456"}'

# 获取 Token
TOKEN=$(curl -s -X POST "http://localhost:8080/api/user/login" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -d '{"username":"test","password":"123456"}' | \
    grep -o '"token":"[^"]*"' | cut -d'"' -f4)

ADMIN_TOKEN=$(curl -s -X POST "http://localhost:8080/api/user/login" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -d '{"username":"admin","password":"123456"}' | \
    grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo -e "\n获取的 Token: ${TOKEN:0:20}..."

# 3. 商品模块测试
echo -e "\n${YELLOW}===== 3. 商品模块测试 =====${NC}"

test_api "商品列表" "GET" "http://localhost:8080/api/product/list?page=1&size=10"

test_api "商品详情" "GET" "http://localhost:8080/api/product/1"

test_api "商品搜索" "GET" "http://localhost:8080/api/product/search?keyword=%E7%8E%AB%E7%91%B0&page=1&size=5"

test_api "分类列表" "GET" "http://localhost:8080/api/category/list"

# 4. 订单模块测试
echo -e "\n${YELLOW}===== 4. 订单模块测试 =====${NC}"

test_api "创建订单" "POST" "http://localhost:8080/api/order/create" \
    '{"productId":1,"quantity":1,"receiverName":"test","receiverPhone":"13900001111","receiverAddress":"test address"}' \
    "$TOKEN"

test_api "我的订单" "GET" "http://localhost:8080/api/order/my?page=1&size=5" "" "$TOKEN"

# 5. 购物车模块测试
echo -e "\n${YELLOW}===== 5. 购物车模块测试 =====${NC}"

test_api "添加购物车" "POST" "http://localhost:8080/api/cart/add" \
    '{"productId":2,"quantity":1}' "$TOKEN"

test_api "购物车列表" "GET" "http://localhost:8080/api/cart/list" "" "$TOKEN"

# 6. 评价模块测试
echo -e "\n${YELLOW}===== 6. 评价模块测试 =====${NC}"

test_api "评价列表" "GET" "http://localhost:8080/api/review/list?productId=1" "" "$TOKEN"

# 7. 管理后台测试
echo -e "\n${YELLOW}===== 7. 管理后台测试 =====${NC}"

test_api "商品管理列表" "GET" "http://localhost:8080/api/admin/product/list?page=1&size=5" "" "$ADMIN_TOKEN"

test_api "订单管理列表" "GET" "http://localhost:8080/api/admin/order/list?page=1&size=5" "" "$ADMIN_TOKEN"

test_api "数据概览" "GET" "http://localhost:8080/api/admin/stats/overview" "" "$ADMIN_TOKEN"

test_api "订单趋势" "GET" "http://localhost:8080/api/admin/stats/orderTrend" "" "$ADMIN_TOKEN"

test_api "分类销售" "GET" "http://localhost:8080/api/admin/stats/categorySales" "" "$ADMIN_TOKEN"

# 8. AI 功能测试（需要 API Key 和管理员权限）
echo -e "\n${YELLOW}===== 8. AI 功能测试 =====${NC}"
echo -e "${YELLOW}注意：AI 功能需要配置 DEEPSEEK_API_KEY，以下测试可能因未配置而失败${NC}"

test_api "AI 智能查询(管理员)" "POST" "http://localhost:8080/api/ai/query" \
    '{"question":"how many products"}' "$ADMIN_TOKEN"

test_api "AI 对话" "POST" "http://localhost:8080/api/ai/chat" \
    '{"message":"hello"}' "$TOKEN"

# 9. 权限测试
echo -e "\n${YELLOW}===== 9. 权限测试 =====${NC}"

echo -e "\n${YELLOW}[测试] 未登录访问受保护页面${NC}"
response=$(curl -s "http://localhost:8080/api/cart/list")
if echo "$response" | grep -q '"code":401\|"code":403'; then
    echo -e "${GREEN}✓ 通过 - 正确返回 401/403${NC}"
    ((PASS++))
else
    echo -e "${RED}✗ 失败 - 应该返回 401/403${NC}"
    echo "响应: $response"
    ((FAIL++))
fi

# 10. 测试结果汇总
echo -e "\n========================================="
echo -e "  测试结果汇总"
echo -e "========================================="
echo -e "${GREEN}通过: $PASS${NC}"
echo -e "${RED}失败: $FAIL${NC}"
echo -e "总计: $((PASS + FAIL))"
echo -e "========================================="

if [ $FAIL -eq 0 ]; then
    echo -e "\n${GREEN}✓ 所有测试通过！${NC}"
    exit 0
else
    echo -e "\n${RED}✗ 有 $FAIL 个测试失败${NC}"
    exit 1
fi
