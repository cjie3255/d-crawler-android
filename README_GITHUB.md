# 京东搜索爬虫 Android 应用

[![Android Build](https://github.com/YOUR_USERNAME/jd-crawler-android/actions/workflows/build.yml/badge.svg)](https://github.com/YOUR_USERNAME/jd-crawler-android/actions/workflows/build.yml)

一个用于批量爬取京东搜索结果产品件数的 Android 应用。

## 🚀 自动构建

本项目配置了 GitHub Actions 自动构建，每次推送代码都会自动生成 APK。

### 下载最新 APK
1. 进入 [Actions 页面](https://github.com/YOUR_USERNAME/jd-crawler-android/actions)
2. 点击最新的工作流运行
3. 在 "Artifacts" 部分下载 `jd-crawler-apk`
4. 解压得到 `app-debug.apk`

### 手动触发构建
1. 进入 Actions → Android Build
2. 点击 "Run workflow"
3. 选择分支，点击运行

## 📱 功能特性

- ✅ 批量搜索关键词（支持 1 万个以上）
- ✅ 自动提取产品件数
- ✅ 支持多账号切换
- ✅ 数据本地存储
- ✅ 导出 CSV/Excel 格式
- ✅ 可配置请求间隔

## 🔧 本地构建

### 环境要求
- Android Studio Arctic Fox 或更高版本
- JDK 11 或更高版本
- Android SDK API 34

### 构建步骤
```bash
./gradlew assembleDebug
```
APK 生成在：`app/build/outputs/apk/debug/app-debug.apk`

## 📲 安装使用

### 安装到手机
```bash
adb install app-debug.apk
```

### 使用步骤
1. 添加京东账号 Cookie
2. 输入关键词（每行一个）
3. 开始爬取
4. 导出数据

## ⚠️ 注意事项

- 遵守法律法规，仅用于合法用途
- 尊重网站规则，控制请求频率
- 避免大量请求，防止 IP 被封

## 📄 许可证

仅供学习研究使用。

## 📞 联系方式

如有问题，请提交 Issue 或联系开发者。