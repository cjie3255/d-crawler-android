# 京东搜索爬虫 Android 应用

一个用于批量爬取京东搜索结果产品件数的 Android 应用。

## 功能特性

- ✅ 批量搜索关键词（支持 1 万个以上）
- ✅ 自动提取产品件数
- ✅ 支持多账号切换
- ✅ 数据本地存储
- ✅ 导出 CSV/Excel 格式
- ✅ 可配置请求间隔

## 编译安装

### 环境要求

- Android Studio Arctic Fox 或更高版本
- JDK 11 或更高版本
- Android SDK API 34

### 编译步骤

1. 用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 点击 Build → Build Bundle(s) / APK(s) → Build APK(s)
4. APK 生成在 `app/build/outputs/apk/debug/` 目录

### 安装到手机

1. 手机开启 USB 调试模式
2. 连接电脑
3. 使用 ADB 安装：
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```
4. 或直接传输 APK 到手机手动安装

## 使用说明

### 1. 添加京东账号

1. 打开应用，点击"账号管理"
2. 输入京东账号的 Cookie
3. 保存账号

**如何获取 Cookie：**
1. 在手机浏览器登录京东
2. 打开开发者工具
3. 找到请求头中的 Cookie
4. 复制粘贴到应用

### 2. 输入关键词

在文本框中输入关键词，每行一个：
```
男士内裤
女士 T 恤
运动鞋
手机
电脑
```

### 3. 开始爬取

1. 点击"开始爬取"
2. 应用会逐个搜索关键词
3. 实时显示进度和结果

### 4. 导出数据

1. 点击"导出"按钮
2. 文件保存在 `下载/JDCrawler/` 目录
3. 可用 Excel 打开查看

## 注意事项

⚠️ **重要提示：**

1. **遵守法律法规** - 仅用于合法用途
2. **遵守 robots.txt** - 尊重网站规则
3. **控制请求频率** - 建议间隔≥2 秒
4. **避免大量请求** - 可能导致 IP 被封
5. **个人使用** - 不要用于商业竞争

## 项目结构

```
jd-crawler-android/
├── app/
│   ├── src/main/
│   │   ├── java/com/jdcrawler/
│   │   │   ├── ui/           # 界面 Activity
│   │   │   ├── model/        # 数据模型
│   │   │   ├── db/           # 数据库操作
│   │   │   └── util/         # 工具类
│   │   ├── res/              # 资源文件
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── README.md
```

## 技术栈

- **语言**: Java
- **网络**: OkHttp3
- **HTML 解析**: Jsoup
- **数据库**: SQLite
- **导出**: CSV

## 开发计划

- [ ] 完善账号管理界面
- [ ] 支持 Excel 导出
- [ ] 添加筛选条件爬取
- [ ] 支持定时任务
- [ ] 数据统计图表

## 许可证

仅供学习研究使用。

## 联系方式

如有问题，请联系开发者。
