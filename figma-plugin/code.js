// Figma 插件脚本：自动生成花之恋登录页设计稿
// 使用方法：Figma → Plugins → Development → Import plugin from manifest → 选择此目录
// 或者：Figma →ugins → Development → Open console → 粘贴运行

// ========== 方法：直接在 Figma 控制台运行 ==========
// 打开 Figma 文件 → 按 Ctrl+Alt+J 打开控制台 → 粘贴以下代码 → 回车运行

async function createLoginPage() {
  const page = figma.currentPage;

  // ========== 根容器 1920×1208 渐变背景 ==========
  const root = figma.createFrame();
  root.name = "Login Page";
  root.resize(1920, 1208);
  root.layoutMode = "VERTICAL";
  root.primaryAxisAlignItems = "CENTER";
  root.counterAxisAlignItems = "CENTER";
  root.fills = [{
    type: "GRADIENT_LINEAR",
    gradientTransform: [[0.7, 0.3, 0], [-0.3, 0.7, 0]],
    gradientStops: [
      { color: { r: 1, g: 0.878, b: 0.902 }, position: 0 },
      { color: { r: 1, g: 0.714, b: 0.757 }, position: 0.3 },
      { color: { r: 0.78, g: 0.082, b: 0.149 }, position: 1 }
    ]
  }];
  page.appendChild(root);

  // ========== 装饰花朵 ==========
  const flowers = [
    { emoji: "🌸", x: 150, y: 120, size: 64 },
    { emoji: "🌹", x: 80, y: 700, size: 80 },
    { emoji: "🌷", x: 1700, y: 180, size: 72 },
    { emoji: "💐", x: 1750, y: 900, size: 56 }
  ];
  for (const f of flowers) {
    const txt = figma.createText();
    txt.name = "Deco " + f.emoji;
    await figma.loadFontAsync({ family: "Inter", style: "Regular" });
    txt.characters = f.emoji;
    txt.fontSize = f.size;
    txt.opacity = 0.3;
    txt.x = f.x;
    txt.y = f.y;
    root.appendChild(txt);
  }

  // ========== 白色卡片 ==========
  const card = figma.createFrame();
  card.name = "Login Card";
  card.resize(420, 576);
  card.layoutMode = "VERTICAL";
  card.primaryAxisAlignItems = "CENTER";
  card.counterAxisAlignItems = "CENTER";
  card.paddingTop = 28; card.paddingBottom = 16;
  card.paddingLeft = 32; card.paddingRight = 32;
  card.itemSpacing = 18;
  card.cornerRadius = 16;
  card.fills = [{ type: "SOLID", color: { r: 1, g: 1, b: 1 } }];
  card.effects = [{ type: "DROP_SHADOW", color: { r: 0, g: 0, b: 0, a: 0.12 }, offset: { x: 0, y: 0 }, radius: 12, spread: 0 }];
  root.appendChild(card);

  // ========== Logo 区域 ==========
  const logoIcon = figma.createText();
  logoIcon.name = "Logo Icon";
  await figma.loadFontAsync({ family: "Inter", style: "Regular" });
  logoIcon.characters = "🌸";
  logoIcon.fontSize = 56;
  logoIcon.textAlignHorizontal = "CENTER";
  card.appendChild(logoIcon);

  const title = figma.createText();
  title.name = "Title";
  await figma.loadFontAsync({ family: "Inter", style: "Bold" });
  title.characters = "花之恋";
  title.fontSize = 32;
  title.fills = [{ type: "SOLID", color: { r: 0.78, g: 0.082, b: 0.149 } }];
  title.textAlignHorizontal = "CENTER";
  title.letterSpacing = { value: 4, unit: "PIXELS" };
  card.appendChild(title);

  const subtitle = figma.createText();
  subtitle.name = "Subtitle";
  subtitle.characters = "每一束花，都是爱的告白";
  subtitle.fontSize = 14;
  subtitle.fills = [{ type: "SOLID", color: { r: 0.6, g: 0.6, b: 0.6 } }];
  subtitle.textAlignHorizontal = "CENTER";
  card.appendChild(subtitle);

  // ========== Tab 切换 ==========
  const tabsFrame = figma.createFrame();
  tabsFrame.name = "Tabs";
  tabsFrame.layoutMode = "HORIZONTAL";
  tabsFrame.resize(356, 44);
  tabsFrame.fills = [];
  tabsFrame.layoutGrow = 1;
  card.appendChild(tabsFrame);

  const loginTab = figma.createText();
  loginTab.name = "Login Tab";
  loginTab.characters = "登录";
  loginTab.fontSize = 16;
  loginTab.fills = [{ type: "SOLID", color: { r: 0.78, g: 0.082, b: 0.149 } }];
  loginTab.textAlignHorizontal = "CENTER";
  loginTab.layoutGrow = 1;
  tabsFrame.appendChild(loginTab);

  const registerTab = figma.createText();
  registerTab.name = "Register Tab";
  registerTab.characters = "注册";
  registerTab.fontSize = 16;
  registerTab.fills = [{ type: "SOLID", color: { r: 0.6, g: 0.6, b: 0.6 } }];
  registerTab.textAlignHorizontal = "CENTER";
  registerTab.layoutGrow = 1;
  tabsFrame.appendChild(registerTab);

  // ========== 用户名输入框 ==========
  const usernameGroup = figma.createFrame();
  usernameGroup.name = "Username Field";
  usernameGroup.layoutMode = "VERTICAL";
  usernameGroup.resize(356, 78);
  usernameGroup.itemSpacing = 6;
  usernameGroup.fills = [];
  card.appendChild(usernameGroup);

  const usernameLabel = figma.createText();
  usernameLabel.characters = "用户名";
  usernameLabel.fontSize = 14;
  usernameLabel.fills = [{ type: "SOLID", color: { r: 0.2, g: 0.2, b: 0.2 } }];
  usernameGroup.appendChild(usernameLabel);

  const usernameInput = figma.createFrame();
  usernameInput.name = "Input";
  usernameInput.resize(356, 40);
  usernameInput.cornerRadius = 4;
  usernameInput.fills = [{ type: "SOLID", color: { r: 1, g: 1, b: 1 } }];
  usernameInput.strokes = [{ type: "SOLID", color: { r: 0.863, g: 0.875, b: 0.902 } }];
  usernameInput.strokeWeight = 1;
  usernameGroup.appendChild(usernameInput);

  const usernamePlaceholder = figma.createText();
  usernamePlaceholder.characters = "👤 请输入用户名";
  usernamePlaceholder.fontSize = 14;
  usernamePlaceholder.fills = [{ type: "SOLID", color: { r: 0.6, g: 0.6, b: 0.6 } }];
  usernamePlaceholder.x = 12; usernamePlaceholder.y = 10;
  usernameInput.appendChild(usernamePlaceholder);

  // ========== 密码输入框 ==========
  const passwordGroup = figma.createFrame();
  passwordGroup.name = "Password Field";
  passwordGroup.layoutMode = "VERTICAL";
  passwordGroup.resize(356, 78);
  passwordGroup.itemSpacing = 6;
  passwordGroup.fills = [];
  card.appendChild(passwordGroup);

  const passwordLabel = figma.createText();
  passwordLabel.characters = "密码";
  passwordLabel.fontSize = 14;
  passwordLabel.fills = [{ type: "SOLID", color: { r: 0.2, g: 0.2, b: 0.2 } }];
  passwordGroup.appendChild(passwordLabel);

  const passwordInput = figma.createFrame();
  passwordInput.name = "Input";
  passwordInput.resize(356, 40);
  passwordInput.cornerRadius = 4;
  passwordInput.fills = [{ type: "SOLID", color: { r: 1, g: 1, b: 1 } }];
  passwordInput.strokes = [{ type: "SOLID", color: { r: 0.863, g: 0.875, b: 0.902 } }];
  passwordInput.strokeWeight = 1;
  passwordGroup.appendChild(passwordInput);

  const passwordPlaceholder = figma.createText();
  passwordPlaceholder.characters = "🔒 请输入密码";
  passwordPlaceholder.fontSize = 14;
  passwordPlaceholder.fills = [{ type: "SOLID", color: { r: 0.6, g: 0.6, b: 0.6 } }];
  passwordPlaceholder.x = 12; passwordPlaceholder.y = 10;
  passwordInput.appendChild(passwordPlaceholder);

  // ========== 登录按钮 ==========
  const loginBtn = figma.createFrame();
  loginBtn.name = "Login Button";
  loginBtn.resize(356, 40);
  loginBtn.cornerRadius = 4;
  loginBtn.fills = [{ type: "SOLID", color: { r: 0.78, g: 0.082, b: 0.149 } }];
  loginBtn.layoutMode = "HORIZONTAL";
  loginBtn.primaryAxisAlignItems = "CENTER";
  loginBtn.counterAxisAlignItems = "CENTER";
  card.appendChild(loginBtn);

  const loginBtnText = figma.createText();
  loginBtnText.characters = "登 录";
  loginBtnText.fontSize = 16;
  loginBtnText.fills = [{ type: "SOLID", color: { r: 1, g: 1, b: 1 } }];
  loginBtnText.letterSpacing = { value: 4, unit: "PIXELS" };
  loginBtn.appendChild(loginBtnText);

  // 选中根节点
  figma.currentPage.selection = [root];
  figma.viewport.scrollAndZoomIntoView([root]);

  figma.notify("✅ 登录页设计稿已生成！");
}

createLoginPage();
