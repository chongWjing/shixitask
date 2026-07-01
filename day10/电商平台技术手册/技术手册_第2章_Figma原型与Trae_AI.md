# 第2章 Figma原型设计 + Trae AI工具

> **学习目标：** 掌握Figma AI对话式原型设计方法，学会使用Trae AI辅助编程，完成Vue3+ElementPlus项目初始化
> **前置条件：** 完成第1章的开发环境搭建
> **本章产出：** 通过AI对话生成的4个商城页面原型图、Trae AI工具掌握、Vue3+ElementPlus项目创建并运行

---

## 2.1 Figma原型图设计 —— 手动绘制与AI对话生成（60分钟）

"同学们，在真正写代码之前，优秀的设计师和开发者都会先做一件事——画原型图。原型图就像盖房子之前的设计图纸，帮你把页面的布局、元素、交互先想清楚，写代码的时候就心中有数。"

"今天我们学习两种画原型的方式：手动绘制和AI对话生成。手动绘制帮你理解页面结构，AI对话生成帮你快速出原型。两种都要掌握！"

### 步骤1：注册Figma账号

**操作：**

1. 打开浏览器，访问 https://www.figma.com/
2. 点击右上角 "Get started for free"
3. 使用 Google 账号或邮箱注册
4. 注册完成后进入 Figma 工作台

**预期效果：** 看到 Figma 的工作台界面，左侧显示项目列表，中间为空白画布

---

### 步骤2：创建商城项目

**操作：**

1. 点击左上角 "New design file"
2. 点击文件名 "Untitled"，重命名为 "花之恋鲜花电商平台原型"
3. 在画布中按 `F` 键创建第一个 Frame（画框），选择 `Desktop` 尺寸（1440×1024），因为是PC端商城

**预期效果：** 画布上出现一个PC端桌面尺寸的白色画框

---

### 步骤3：设计商城首页原型

"我们先设计商城首页的原型。首页是PC端布局，采用顶部导航+左侧分类+右侧商品列表的经典电商布局——"

**首页结构（PC端）：**

```
┌──────────────────────────────────────────────────────────────┐
│  花之恋 Logo  │ 首页 │ 鲜花 │ 花束 │ 绿植 │  🔍搜索   │ 登录 │  ← 顶部导航栏
├──────────────────────────────────────────────────────────────┤
│          │                                                    │
│  鲜花    │   ┌──────────────────────────────────────────┐     │
│  永生花  │   │          轮播图/Banner区域               │     │
│  花束    │   └──────────────────────────────────────────┘     │
│  花篮    │                                                    │
│  绿植    │   ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐        │
│  礼品花  │   │鲜花1  │  │鲜花2  │  │鲜花3  │  │鲜花4  │        │
│          │   │¥399  │  │¥259  │  │¥199  │  │¥699  │        │
│          │   └──────┘  └──────┘  └──────┘  └──────┘        │
│          │   ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐        │
│          │   │鲜花5  │  │鲜花6  │  │鲜花7  │  │鲜花8  │        │
│          │   │¥599  │  │¥129  │  │¥329  │  │¥899  │        │
│          │   └──────┘  └──────┘  └──────┘  └──────┘        │
├──────────────────────────────────────────────────────────────┤
│  花之恋鲜花电商平台 © 2026  │  关于我们  │  联系客服          │  ← 底部Footer
└──────────────────────────────────────────────────────────────┘
```

**在Figma中绘制（PC端布局）：**

1. **顶部导航栏：** 画一个全宽矩形（1440×60），填充白色，左侧放Logo文字"花之恋"，中间放导航链接，右侧放搜索框和登录按钮
2. **左侧分类栏：** 画一个矩形（200×auto），填充浅色，列6个分类：鲜花、永生花、花束、花篮、绿植、礼品花
3. **轮播图区域：** 画一个矩形（1200×300），填充浅色，添加文字"轮播图/Banner区域"
4. **商品卡片网格：** 每行4个卡片，每个卡片包含图片区域+商品名+价格，价格用红色#C71526
5. **底部Footer：** 全宽矩形，深色背景，包含版权信息

> 💡 如果使用Figma AI功能，可以尝试在Figma的AI对话框中输入："Design a desktop e-commerce homepage with top navigation bar, left category sidebar, banner carousel area, 4-column product grid, and footer"，AI会自动生成布局。

---

### 步骤4：使用Figma AI通过对话生成原型（重点）

"同学们，刚才我们是一笔一笔手动画的原型。Figma现在有一个非常强大的AI功能——你可以直接用自然语言对话，让AI帮你生成页面原型！这就是AI时代的设计方式。"

#### 4.1 打开Figma AI对话面板

**重要前提：必须先创建并打开一个设计文件！**

如果你现在看到的界面是项目列表/仪表盘页面（显示"Team project"、"All projects"等），说明你还在文件管理层面，**需要先进入设计编辑器**。

**正确操作流程：**

1. 在当前页面点击已有的 **"Untitled"** 文件卡片（或者点击右上角蓝色 **"+ Create"** → **"Design file"** 创建新文件）
2. 等待页面跳转——你会进入一个全新的界面：中间是白色画布，左侧有工具栏（形状、文字等工具），顶部有菜单栏
3. **只有在这个界面中，Figma AI 才会出现**

**预期效果（进入设计编辑器后）：** 界面中央出现白色画布区域，左侧有工具面板（矩形R、文字T、椭圆O等），顶部有菜单栏。此时才算真正进入了Figma的设计环境。

> ⚠️ 常见误区：很多同学在项目仪表盘页面找AI按钮，那是找不到的。AI功能只在**设计编辑器内部**可用。

---

**找到Figma AI —— 两种情况：**

### 情况一：你有 Figma 付费版（Professional / Organization）

在付费版中，Figma 内置了原生 AI 功能。找到它的方式：

**方式一：通过 Actions 面板（推荐）**
- 在设计编辑器内，按快捷键 `Ctrl + /`（Mac: `Cmd + /`）
- 或点击左上角搜索图标
- 输入 **"Generate"** 或 **"AI"**
- 选择 **"Ask Figma AI"** 或 **"Generate design"**

**方式二：通过工具栏按钮**
- 在顶部工具栏右侧，寻找带有 **✨ 星星/闪光图标** 的按钮

**方式三：通过右键菜单**
- 在画布空白处右键 → **"AI" → "Generate design"**

### 情况二：你用的是 Figma 免费版（大多数同学）

**免费版没有内置的 Figma AI 功能！** 当你在 Actions 中搜索 "AI" 时，你看到的都是**第三方社区插件**，类似这样的列表：

```
┌─────────────────────────────────────────────┐
│  🔍  AI                              [×]    │
│                                             │
│  All   Assets   Plugins & widgets           │
│                                             │
│  From Community                             │
│                                             │
│  🌐 Builder.io - Figma to Code & AI Apps    │
│     Export designs to clean, responsive...  │
│     Plugin · 1.2M users · ⭐22.9k            │
│                                             │
│  🔮 Stark - Contrast & Accessibility AI      │
│     Fix Color Contrast and Typography...    │
│     Plugin · 576k users · ⭐9.9k             │
│                                             │
│  🎨 Text to Design AI UI Copilot             │
│     Watch AI design your prompt in real...  │
│     Plugin · 92.3K users · ⭐609             │
│                                             │
│  📄 Figma to Fully Responsive HTML & Clean..│
│     Convert Figma to Fully Responsive HTML..│
│     Plugin                                   │
└─────────────────────────────────────────────┘
```

**别担心！我们可以安装免费的第三方 AI 插件来实现同样的效果。推荐使用以下两个插件之一：**

#### 推荐插件 A：Text to Design AI UI Copilot（最推荐）

这个插件可以直接用自然语言生成UI设计，和内置Figma AI功能最接近。

**安装步骤：**

1. 在上述 Plugins 搜索结果中，找到 **"Text to Design AI UI Copilot - uidesign.ai"**
2. 点击该条目
3. 点击 **"+ Install"** 按钮安装
4. 安装完成后，在左侧工具栏或右键菜单中可以找到它
5. 点击运行后，会出现一个对话框，你可以输入自然语言描述来生成设计

**使用方法：**
- 安装后在画布上点击右键 → **Plugins → Text to Design AI UI Copilot**
- 或者按快捷键调出插件面板
- 输入英文 Prompt（和后面步骤4.2-4.5中的Prompt一样），点击生成即可

> 💡 这个插件由 uidesign.ai 提供，有免费使用额度，足够完成我们的原型设计任务。

#### 推荐插件 B：Builder.io（备选）

如果上面的插件不可用，可以使用 Builder.io：

1. 在 Plugins 搜索结果中找到 **"Builder.io - Figma to Code & AI Apps"**
2. 点击安装
3. 使用其 AI 设计生成功能

---

> 💡 **总结一下你的情况：**
> - 如果你搜到的是 **"Ask Figma AI"** → 你有付费版，直接用步骤4.2-4.8
> - 如果你只看到 **第三方插件列表** → 你是免费版，安装 **"Text to Design AI UI Copilot"** 插件，同样可以用后面的 Prompt 来生成原型
> - 如果连插件都不想装 → 跳过AI部分，直接用手动绘制（步骤3的方式），不影响后续编码

---

#### 4.2 用AI对话生成商城首页原型

"我们用自然语言描述想要的页面，AI就会自动生成！来看——"

**在AI对话输入框中输入以下Prompt：**

```
Design a desktop e-commerce homepage for a flower shop called "花之恋" with the following sections:
1. A top navigation bar with logo "花之恋", navigation links (Home, Flowers, Bouquets, Plants), search input, and login/register buttons
2. A left sidebar with 6 category links: 鲜花(Fresh Flowers), 永生花(Preserved Flowers), 花束(Bouquets), 花篮(Baskets), 绿植(Plants), 礼品花(Gift Flowers)
3. A main content area with a large banner/carousel at the top
4. A product grid section showing products in 4 columns, each product card has an image, product name, and price in red color
5. A footer with copyright info and links

Use modern desktop UI style with clean spacing. The primary color is #C71526 (deep red/crimson).
```

**按回车或点击发送按钮。**

**预期效果：** Figma AI会在几秒钟内自动生成一个完整的PC端商城首页布局，包含顶部导航栏、左侧分类栏、轮播图区域、商品四列卡片和底部Footer。生成的内容会直接出现在画布上，以Frame的形式呈现。

"看到了吗？AI自动帮我们把整个首页的布局都画出来了！导航栏、分类栏、轮播图、商品列表、底部——全部一气呵成。"

---

#### 4.3 用AI对话生成商品详情页原型

**在AI对话输入框中输入：**

```
Design a desktop product detail page for a flower shop with:
1. A top navigation bar with logo and back navigation
2. A two-column layout: left side shows a large product image with thumbnail gallery below, right side shows product info
3. Right side includes: product name in bold, price in large red text (e.g., ¥399.00), a category tag (鲜花/永生花/花束 etc.), a quantity selector, and two buttons "Add to Cart" (outlined style) and "Buy Now" (filled #C71526 red style)
4. Below the two columns, a product description section with detailed text

Desktop-first design, clean and modern style with generous spacing.
```

**预期效果：** AI生成一个PC端商品详情页，左右两栏布局，左侧商品大图+缩略图，右侧价格+名称+数量选择+购买按钮。

---

#### 4.4 用AI对话生成下单页面原型

**在AI对话输入框中输入：**

```
Design a desktop order confirmation page for a flower shop with:
1. A top navigation bar with logo and breadcrumb navigation
2. A main content area with three sections from top to bottom:
3. A shipping address section showing recipient name, phone number and full address, with an "Edit" button
4. A product info section showing product thumbnail image on the left, product name, price and quantity on the right
5. A price summary section showing subtotal, shipping (Free Shipping), and total amount in red
6. A bottom action bar with a large red "Submit Order" button aligned to the right

Use a clean, minimal desktop checkout design style.
```

**预期效果：** AI生成一个PC端下单确认页面，包含收货地址、商品信息、价格汇总和提交按钮，桌面端宽屏布局。

---

#### 4.5 用AI对话生成登录页面原型

**在AI对话输入框中输入：**

```
Design a desktop login page for a flower shop called "花之恋" with:
1. A two-column layout: left side shows a decorative flower image or brand illustration, right side shows the login form
2. On the right side: a centered brand logo "花之恋" with a flower icon at the top
3. Two input fields: username and password, with rounded corners and subtle border
4. A large red "Login" button (color #C71526) below the inputs
5. A "Register" text link below the login button
6. Clean, modern, centered layout with proper spacing

The primary color is #C71526 (deep red/crimson). Minimal and elegant design suitable for a flower e-commerce brand.
```

**预期效果：** AI生成一个PC端登录页面，左右分栏布局，左侧品牌插图，右侧登录表单，品牌色#C71526。

---

#### 4.6 调整和优化AI生成的原型

"AI生成的原型虽然快，但通常需要微调。我们来调整一些细节——"

**常见调整操作：**

1. **调整间距：** 选中元素，在右侧面板的"Auto layout"或"Spacing"中调整间距
2. **修改颜色：** 选中元素，在右侧"Fill"面板中修改颜色
3. **调整大小：** 选中元素，拖拽边角调整尺寸，或在右侧面板输入精确数值
4. **修改文字：** 双击文字元素，直接编辑内容
5. **添加缺失元素：** 手动用 `R`（矩形）、`T`（文字）、`O`（椭圆）工具补充AI遗漏的部分

**优化技巧：**

```
AI生成原型的优化要点：

1. 统一间距 —— 所有区域之间保持16px或24px的统一间距
2. 对齐检查 —— 使用顶部工具栏的"Align"功能，确保元素对齐
3. 文字规范 —— 标题用18px加粗，正文用14px常规，辅助文字用12px灰色
4. 颜色统一 —— 主色用#C71526（花之恋品牌深红），背景用#f5f5f5，卡片白色#ffffff
5. 圆角一致 —— 卡片圆角8px，按钮圆角20px，输入框圆角8px
```

"记住，AI是你的助手，不是替代品。它帮你快速出初稿，你负责把关和优化。这个过程就像——AI是画师，你是艺术总监。"

---

#### 4.7 AI生成 vs 手动绘制对比

"我们来对比一下两种方式——"

| 对比项 | 手动绘制 | AI对话生成 |
|--------|---------|-----------|
| 速度 | 一个页面约15-20分钟 | 一个页面约1-2分钟 |
| 精确度 | 完全按你想法来 | 接近你描述，但需要微调 |
| 个性化 | 高度自由 | 受限于描述的表达 |
| 学习门槛 | 需要掌握Figma操作 | 只需要会描述需求 |
| 适用场景 | 精细设计、品牌定制 | 快速出原型、页面探索 |

"在实际工作中，推荐先用AI快速生成原型，再手动精修。这样既快又好。"

---

#### 4.8 补充：用Figma AI的"Make Changes"功能迭代优化

"如果AI生成的原型有些地方不满意，你不需要重新生成。可以直接告诉AI你想改什么——"

**在AI对话中继续输入修改指令，例如：**

```
Make the product cards smaller and add a "Sold 256" text below each price
```

```
Change the carousel height to be shorter, and make the category icons use circles instead of squares
```

```
Add a "Hot" badge on the top-right corner of the first product card
```

**预期效果：** AI会在当前原型的基础上进行局部修改，而不是重新生成整个页面。这种迭代式设计非常高效。

> 💡 **实用建议：** 每次只提一个修改要求，这样AI更容易理解并正确执行。如果一次性提多个修改，AI可能会遗漏某些要求。

---

### 步骤5：导出原型图

**操作：**

1. 选中整个 Frame
2. 右侧面板点击 "Export" 区域
3. 点击 "+" 添加导出设置，选择 PNG 格式，2x 倍率
4. 点击 "Export" 按钮导出

**预期效果：** 得到4张PNG格式的原型图文件（首页、商品详情、下单、登录），可以用于开发参考

> 💡 原型图是设计参考，不是最终效果。我们编码时会根据ElementPlus组件的实际效果进行调整。

---

### 步骤6：整理原型设计成果

"我们来整理一下今天用Figma AI完成的原型设计成果——"

| 页面 | 核心区域 | AI对话关键词 |
|------|---------|-------------|
| 首页 | 导航栏+分类栏+轮播图+商品网格+Footer | desktop e-commerce homepage, navigation, sidebar, product grid |
| 商品详情 | 商品大图+价格+名称+购买按钮 | product detail, image gallery, buy button |
| 下单页 | 收货地址+商品信息+价格汇总+提交按钮 | order confirmation, shipping address, submit |
| 登录页 | 品牌插图+登录表单+登录按钮+注册链接 | login page, brand illustration, login form |

"有了这些原型图，接下来写代码的时候就有了明确的参考。这就是'先设计，后编码'的好习惯。"

---

## 2.2 Trae AI工具介绍与使用（45分钟）

"原型图设计好了，接下来要开始写代码了。但是一行一行手敲太慢了——我们有一个强大的帮手：Trae AI。"

### 什么是Trae AI

"Trae AI是字节跳动推出的**AI原生IDE**——它不是一个插件，而是一个独立的代码编辑器，就像Cursor一样。它的核心能力——"

| 功能 | 说明 | 举个例子 |
|------|------|---------|
| 代码生成 | 根据自然语言描述生成代码 | "用ElementPlus写一个商品卡片组件" |
| 代码补全 | 输入部分代码，AI自动补全剩余 | 输入 `el-` 自动补全为 `<el-button>` |
| 代码解释 | 选中代码，AI生成中文注释 | 选中一段函数，AI解释其功能 |
| 代码重构 | AI帮你优化代码结构 | "把这段代码拆分成多个组件" |
| 聊天问答 | 在编辑器中直接问AI问题 | "Vue3中怎么监听路由变化" |

> ⚠️ **重要提醒：** Trae AI 是一个**独立的IDE**，不是IDEA或VSCode的插件！不能"在IDEA中安装Trae AI插件"。它需要单独下载安装，和IDEA、VSCode是完全独立的软件。

---

### 步骤1：下载安装Trae AI

**操作：**

1. 打开浏览器，访问 Trae AI 官网 https://trae.ai/
2. 点击 **"Download"** 按钮，选择 Windows 版本下载安装包
3. 运行安装包，按提示完成安装（安装过程和VSCode类似）
4. 安装完成后，桌面会出现 Trae AI 的图标

**预期效果：** 双击桌面图标，打开Trae AI编辑器界面。界面布局和VSCode非常相似，左侧是文件管理器，右侧是代码编辑区，但多了一个**AI对话面板**。

> 💡 Trae AI 基于VSCode内核开发，所以界面操作和快捷键与VSCode几乎一致，会VSCode的同学可以无缝上手。

---

### 步骤2：在Trae AI中打开项目

**操作：**

1. 启动 Trae AI
2. 点击菜单 **File → Open Folder...**
3. 选择 `E:\HuaShan\ecommerce\frontend` 文件夹，点击"选择文件夹"
4. 项目文件会出现在左侧资源管理器中

**预期效果：** 左侧显示项目目录结构，可以展开 src、views 等文件夹查看代码文件。

---

### 步骤3：使用Trae AI的AI对话功能

"Trae AI最大的特色就是内置的AI对话面板——你可以直接用中文告诉它你想写什么代码。"

**打开AI对话面板：**

- 点击右侧的 **AI 图标**（或按快捷键 `Ctrl + Shift + I`）
- 在弹出的对话框中输入你的需求

**使用流程：**

```
在AI对话框中输入Prompt
        ↓
  AI生成代码片段
        ↓
  检查代码是否正确
        ↓
  复制代码到项目文件中
```

**在AI对话框中输入：**

```
使用Vue3的Options API和ElementPlus组件库，写一个简单的商品卡片组件ProductCard.vue，显示商品图片、名称和价格。图片使用el-image组件，价格用红色#C71526显示。
```

**Trae AI会生成类似代码：**

```vue
<template>
  <el-card class="product-card" :body-style="{ padding: '0px' }" shadow="hover">
    <el-image
      :src="product.image"
      fit="cover"
      style="width: 100%; height: 200px;"
    />
    <div class="product-info">
      <div class="product-name">{{ product.name }}</div>
      <div class="product-price">¥{{ product.price }}</div>
    </div>
  </el-card>
</template>

<script>
export default {
  name: 'ProductCard',
  props: {
    product: {
      type: Object,
      required: true
    }
  }
}
</script>

<style scoped>
.product-info {
  padding: 12px;
}
.product-name {
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-price {
  font-size: 18px;
  color: #C71526;
  font-weight: bold;
  margin-top: 8px;
}
</style>
```

"看到没？AI生成的代码结构清晰，还自动加了样式。当然，AI生成的代码不一定100%正确，我们还需要检查和调整。但效率确实高了很多。"

---

### 步骤4：学习Trae AI的Prompt编写技巧

"AI不是万能的，你需要学会写好Prompt——也就是给AI的指令。写好Prompt有几个技巧——"

**Prompt编写四原则：**

```
1. 明确技术栈 —— 告诉AI你用什么框架和版本
   ❌ "写一个轮播图"
   ✅ "使用Vue3和ElementPlus的Carousel组件写一个轮播图"

2. 提供上下文 —— 告诉AI你的项目结构
   ❌ "写一个商品列表"
   ✅ "在Vue3项目中，使用ElementPlus的Card组件写一个四列商品列表"

3. 分步骤请求 —— 复杂功能拆分成多步
   ❌ "做一个完整的首页"
   ✅ 第一步："先写轮播图组件"
      第二步："再加分类导航"
      第三步："最后加商品列表"

4. 给出示例 —— 让AI理解你想要的效果
   ❌ "卡片要好看"
   ✅ "卡片使用圆角12px，阴影0 2px 12px rgba(0,0,0,0.1)，底部显示价格用红色"
```

---

### 不想安装Trae AI？替代方案

"如果你不想额外安装一个编辑器，完全没关系！以下替代方案同样可以实现AI辅助编程——"

| 替代方案 | 操作方式 | 优点 | 缺点 |
|---------|---------|------|------|
| IDEA + ChatGPT网页版 | 在IDEA中写代码，遇到问题打开ChatGPT网页提问 | 不用装新软件 | 需要在两个窗口之间切换 |
| IDEA + DeepSeek网页版 | 在IDEA中写代码，打开DeepSeek网页提问 | DeepSeek对中文理解好，免费 | 同样需要切换窗口 |
| VSCode + GitHub Copilot | 在VSCode中安装Copilot插件 | 行内代码补全体验最好 | 需要付费订阅 |

> 💡 **建议：** 如果你的电脑配置够用，推荐安装Trae AI体验一下AI原生IDE的工作方式。如果觉得麻烦，用IDEA+DeepSeek网页版也完全可以完成本课程的所有任务。关键是**学会用AI辅助编程的思路**，工具可以灵活选择。

---

## 2.3 Vue3+ElementPlus项目构建（45分钟）

"现在，我们从零创建一个Vue3+ElementPlus项目。先确认一下环境——"

### 前置检查：Node.js版本

**操作：** 打开命令提示符，输入：

```bash
node -v
```

**预期效果：** 显示版本号 ≥ 18.x，例如 `v18.20.0` 或 `v20.11.0`

> ⚠️ **Node.js版本必须 ≥ 18.x！** 如果你的版本低于18，请回到第1章重新安装Node.js 18.x LTS版本。本项目不支持Node.js 16。

---

### 步骤1：创建Vue3项目

**操作：** 打开命令提示符，进入 `E:\HuaShan\ecommerce` 目录

```bash
cd /d E:\HuaShan\ecommerce
```

使用Vite在 `frontend` 子目录中创建Vue3项目：

```bash
npm create vite@latest frontend -- --template vue
```

**预期效果：**

```
✔ Scaffolding project in E:\HuaShan\ecommerce\frontend...
Done. Now run:

  cd frontend
  npm install
  npm run dev
```

进入项目目录并安装基础依赖：

```bash
cd frontend
npm install
```

**预期效果：** 下载并安装所有依赖包，最后显示 `added xxx packages`

---

### 步骤2：安装ElementPlus、Vue Router和自动导入插件

"一次性把所有需要的依赖都装上——ElementPlus、Vue Router，还有ElementPlus组件自动导入需要的两个插件。"

```bash
npm install element-plus vue-router@4
npm install unplugin-vue-components unplugin-auto-import -D
```

**预期效果：**

```
added xx packages in 10s
```

**说明：**
- `element-plus`：PC端UI组件库
- `vue-router@4`：Vue3路由管理
- `unplugin-vue-components`：组件按需自动引入插件
- `unplugin-auto-import`：API按需自动引入插件
- `-D`：这两个是开发依赖，只在开发时使用，不会打包到生产环境

> ⚠️ **安装顺序很重要！** 必须先安装完依赖，再配置 vite.config.js。否则配置中引用的插件找不到，启动会报错。

---

### 步骤3：配置Vite（vite.config.js）

"依赖装好了，现在来配置Vite。打开项目根目录下的 vite.config.js——"

打开 `E:\HuaShan\ecommerce\frontend\vite.config.js`，替换为以下内容：

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // ElementPlus 按需自动引入
    AutoImport({
      resolvers: [ElementPlusResolver()]
    }),
    Components({
      resolvers: [ElementPlusResolver()]
    })
  ],
  server: {
    port: 5173,         // 前端开发服务器端口
    host: '0.0.0.0',   // 允许局域网访问
    proxy: {
      // 代理后端API请求，解决跨域问题
      '/api': {
        target: 'http://localhost:8080',  // 后端SpringBoot地址
        changeOrigin: true
      }
    }
  }
})
```

**说明：**
- `ElementPlusResolver`：实现ElementPlus组件和API按需自动引入，不需要手动import每个组件。这样你在模板中写 `<el-button>` 就能自动引入，无需手动注册。
- `proxy`：将前端的 `/api` 请求代理到后端的8080端口，解决跨域问题。后续联调时会用到。

> 💡 如果启动时报错 `Cannot find module 'unplugin-vue-components'`，说明步骤2的依赖没装成功，回到上一步重新执行 npm install 命令。

---

### 步骤4：创建项目目录结构

**操作：** 在 `E:\HuaShan\ecommerce\frontend\src` 目录下创建以下文件夹

```
src/
├── views/           # 页面组件
├── components/      # 公共组件
├── router/          # 路由配置
├── api/             # API接口封装
├── assets/          # 静态资源
├── layouts/         # 布局组件（ShopLayout.vue等）
└── utils/           # 工具函数
```

可以用命令创建：

```bash
cd /d E:\HuaShan\ecommerce\frontend\src
mkdir views components router api assets utils layouts
```

---

### 步骤5：配置Vue Router

创建文件 `E:\HuaShan\ecommerce\frontend\src\router\index.js`：

```javascript
import { createRouter, createWebHistory } from 'vue-router'

// 导入页面组件
import Home from '../views/Home.vue'
import ProductDetail from '../views/ProductDetail.vue'
import Order from '../views/Order.vue'
import Login from '../views/Login.vue'
import MyOrders from '../views/MyOrders.vue'

// 定义路由规则
const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '首页' }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail,
    meta: { title: '商品详情' }
  },
  {
    path: '/order',
    name: 'Order',
    component: Order,
    meta: { title: '确认订单', requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/my-orders',
    name: 'MyOrders',
    component: MyOrders,
    meta: { title: '我的订单', requiresAuth: true }
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：需要登录的页面，未登录则跳转到登录页
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '花之恋鲜花电商平台'

  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      // 未登录，跳转到登录页，并记录原目标路径
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
```

**说明：**
- `createWebHistory()`：使用HTML5的History模式路由，URL不带#号，更美观
- 路由路径说明：`/` 首页、`/product/:id` 商品详情（带动态参数）、`/order` 下单页、`/login` 登录页、`/my-orders` 我的订单
- `meta.requiresAuth`：标记该路由需要登录，路由守卫会自动检查token
- `beforeEach`：全局前置守卫，在每次路由跳转前执行。未登录用户访问需要认证的页面时，会自动跳转到登录页，并记录原来的目标路径，登录成功后可以跳回来

---

### 步骤6：修改main.js

打开 `E:\HuaShan\ecommerce\frontend\src\main.js`，替换为以下内容：

```javascript
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 引入ElementPlus全局样式（组件按需自动引入，但样式需要全局导入）
import 'element-plus/dist/index.css'

const app = createApp(App)

// 使用路由
app.use(router)

// 挂载应用
app.mount('#app')
```

**说明：**
- `import 'element-plus/dist/index.css'`：虽然组件是按需引入的，但ElementPlus的样式需要全局导入，否则组件样式会错乱
- `app.use(router)`：注册路由插件，让整个应用支持路由跳转

---

### 步骤7：修改App.vue

打开 `E:\HuaShan\ecommerce\frontend\src\App.vue`，替换为以下内容：

```vue
<template>
  <div id="app">
    <!-- 路由视图：根据URL显示对应页面 -->
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto,
    'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
  color: #333;
  font-size: 14px;
  -webkit-font-smoothing: antialiased;
}

/* 清除链接默认样式 */
a {
  text-decoration: none;
  color: inherit;
}
</style>
```

**说明：**
- `<router-view />`：路由出口，所有页面组件都会渲染在这个位置
- 全局样式重置：消除浏览器默认样式差异，确保所有浏览器中显示一致

---

### 步骤8：创建临时首页测试

创建文件 `E:\HuaShan\ecommerce\frontend\src\views\Home.vue`：

```vue
<template>
  <div class="home">
    <h2>花之恋鲜花电商平台首页</h2>
    <p>项目初始化成功！</p>
    <el-button type="primary" @click="handleClick">测试ElementPlus组件</el-button>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'

export default {
  name: 'HomeView',
  methods: {
    handleClick() {
      ElMessage.success('ElementPlus组件正常工作！')
    }
  }
}
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  padding: 20px;
}
.home h2 {
  margin-bottom: 12px;
  color: #333;
}
.home p {
  margin-bottom: 20px;
  color: #666;
}
</style>
```

---

### 步骤9：创建其他页面占位组件

"先把其他几个页面创建出来，内容是占位的，后面章节再逐步完善。"

创建 `E:\HuaShan\ecommerce\frontend\src\views\ProductDetail.vue`：

```vue
<template>
  <div class="product-detail">
    <h2>商品详情页</h2>
    <p>即将在第4章实现</p>
  </div>
</template>

<script>
export default {
  name: 'ProductDetailView'
}
</script>

<style scoped>
.product-detail {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>
```

创建 `E:\HuaShan\ecommerce\frontend\src\views\Order.vue`：

```vue
<template>
  <div class="order">
    <h2>下单页面</h2>
    <p>即将在第5章实现</p>
  </div>
</template>

<script>
export default {
  name: 'OrderView'
}
</script>

<style scoped>
.order {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>
```

创建 `E:\HuaShan\ecommerce\frontend\src\views\Login.vue`：

```vue
<template>
  <div class="login">
    <h2>登录页面</h2>
    <p>即将在第5章实现</p>
  </div>
</template>

<script>
export default {
  name: 'LoginView'
}
</script>

<style scoped>
.login {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>
```

创建 `E:\HuaShan\ecommerce\frontend\src\views\MyOrders.vue`：

```vue
<template>
  <div class="my-orders">
    <h2>我的订单</h2>
    <p>即将在第5章实现</p>
  </div>
</template>

<script>
export default {
  name: 'MyOrdersView'
}
</script>

<style scoped>
.my-orders {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>
```

---

### 步骤10：启动项目

**操作：** 在命令提示符中进入项目目录并启动

```bash
cd /d E:\HuaShan\ecommerce\frontend
npm run dev
```

**预期效果：**

```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: http://192.168.x.x:5173/
```

打开Chrome浏览器，访问 http://localhost:5173/

**预期效果：** 页面显示"花之恋鲜花电商平台首页"、"项目初始化成功！"和一个按钮。点击按钮弹出消息提示"ElementPlus组件正常工作！"

> 💡 本项目是PC端商城，直接在浏览器中查看桌面端效果即可。

> 💡 如果浏览器打开后页面空白，按 F12 打开开发者工具，查看Console中是否有报错信息。常见原因是路由配置或组件引入有误。

---

### 步骤11：项目目录结构验证

在IDEA或VSCode中打开 `E:\HuaShan\ecommerce\frontend`，确认项目结构如下：

```
frontend/
├── public/
│   └── vite.svg
├── src/
│   ├── api/                  # API接口封装（空）
│   ├── assets/               # 静态资源（空）
│   ├── components/           # 公共组件（空）
│   ├── layouts/              # 布局组件（ShopLayout.vue等）
│   ├── router/
│   │   └── index.js          # 路由配置
│   ├── utils/                # 工具函数（空）
│   ├── views/
│   │   ├── Home.vue          # 首页
│   │   ├── ProductDetail.vue # 商品详情（占位）
│   │   ├── Order.vue         # 下单页（占位）
│   │   ├── Login.vue         # 登录页（占位）
│   │   └── MyOrders.vue      # 我的订单（占位）
│   ├── App.vue               # 根组件
│   └── main.js               # 入口文件
├── index.html
├── package.json
├── vite.config.js
└── ...其他配置文件
```

---

## 本章常见问题

| 问题 | 原因 | 解决方案 |
|------|------|---------|
| npm create vite很慢 | 网络问题 | 配置npm镜像：`npm config set registry https://registry.npmmirror.com` |
| npm install报错 | Node.js版本不兼容 | 确保Node.js版本 ≥ 18.x，用 `node -v` 检查。本项目不支持Node.js 16 |
| Vant组件不显示 | 没有配置自动导入 | 检查vite.config.js中是否配置了ElementPlusResolver和unplugin-vue-components |
| 页面空白 | 路由配置问题 | 检查router/index.js中路由路径是否正确，main.js中是否use(router) |
| 访问页面404 | 路由模式问题 | 确认使用createWebHistory()，且路由path正确 |
| Vant样式错乱 | 没有引入Vant CSS | 在main.js中添加 `import 'element-plus/dist/index.css'` |
| 启动报错Cannot find module 'unplugin-vue-components' | 依赖未安装就配置了vite.config.js | 先执行 `npm install unplugin-vue-components unplugin-auto-import -D`，再启动项目 |
| Figma AI功能不可用 | 免费版限制 | Figma AI需要Professional或Organization付费版；免费用户可安装"Text to Design AI UI Copilot"插件实现同样功能 |
| 在项目仪表盘找不到AI按钮 | 还没进入设计文件 | **必须先点击"Untitled"或新建Design File进入编辑器界面**，AI只在设计画布内可用 |
| 搜索AI只看到第三方插件列表 | 免费版无内置AI | 这是正常的！免费版没有原生Figma AI。推荐安装"Text to Design AI UI Copilot"(uidesign.ai)插件 |
| 插件安装后无法使用 | 网络或权限问题 | 部分AI插件需要注册账号（如uidesign.ai），按提示注册即可；如果网络不通可跳过改用手动绘制 |
| Figma AI生成效果不理想 | Prompt描述不够清晰 | 按步骤4的Prompt模板格式，明确列出每个区域、样式和颜色要求 |
| AI对话面板找不到 | Figma版本过旧 | 更新Figma到最新版本，或按Ctrl+/搜索"AI"功能 |
| AI生成后修改困难 | 不熟悉Figma操作 | 用AI的"Make Changes"功能，直接用自然语言告诉AI要改什么 |
| Trae AI和IDEA是什么关系？ | 概念混淆 | Trae AI是**独立的AI IDE**，不是IDEA的插件。两者是完全独立的软件，可以同时安装在同一台电脑上 |
| Trae AI中怎么运行npm命令？ | 不熟悉Trae AI | Trae AI内置终端，按 Ctrl+` 打开终端，和VSCode操作一样 |
| 不想装Trae AI怎么办？ | 个人偏好 | 完全可以！用IDEA+DeepSeek网页版（或ChatGPT网页版）替代即可，把AI生成的代码复制到IDEA项目中 |

---

## 本章小结

- ✅ 学会了使用Figma手动绘制PC端商城页面原型
- ✅ 掌握了Figma AI通过自然语言对话生成原型的完整流程（免费版用插件，付费版用内置AI）
- ✅ 学会了4个页面的AI Prompt编写：首页、商品详情、下单页、登录页（PC端布局）
- ✅ 了解了AI生成原型的迭代优化方法（Make Changes）
- ✅ 了解了Trae AI——字节跳动推出的AI原生IDE，掌握了下载安装和AI对话生成代码的方法
- ✅ 了解了AI辅助编程的替代方案（IDEA+DeepSeek网页版）
- ✅ 创建了Vue3+ElementPlus项目，安装了所有必要依赖（包括自动导入插件）
- ✅ 配置了Vue Router（createWebHistory模式）和5个页面路由
- ✅ 安装并验证了ElementPlus组件库的正常工作
- ✅ 配置了开发服务器代理，为后续前后端联调做好准备
- ✅ 创建了layouts目录，预留ShopLayout.vue商城布局页

**下一章预告：** 第3章将在首页上实现轮播图、分类导航和商品列表，完成商城首页的上半部分。
