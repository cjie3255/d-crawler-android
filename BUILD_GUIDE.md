# 京东爬虫Android应用 - 构建指南

## 项目状态
✅ 代码完整，可以编译
⚠️ 需要Android SDK环境

## 构建方法（选择一种）

### 方法1：使用Android Studio（推荐）
1. 下载安装 [Android Studio](https://developer.android.com/studio)
2. 打开项目文件夹 `/home/caijie/jd-crawler-android`
3. 等待Gradle同步完成
4. 点击 Build → Build Bundle(s) / APK(s) → Build APK(s)
5. APK生成在：`app/build/outputs/apk/debug/app-debug.apk`

### 方法2：命令行构建（需要完整Android SDK）
```bash
cd /home/caijie/jd-crawler-android

# 设置Android SDK路径（根据你的安装位置）
export ANDROID_HOME=/path/to/android-sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# 构建APK
./gradlew assembleDebug
```

### 方法3：使用在线CI/CD服务
#### GitHub Actions配置（.github/workflows/build.yml）：
```yaml
name: Android Build

on: [push]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build with Gradle
      run: ./gradlew assembleDebug
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

## 安装到手机
```bash
# 方法1：ADB安装
adb install app/build/outputs/apk/debug/app-debug.apk

# 方法2：手动安装
# 将APK文件复制到手机，点击安装
```

## 项目依赖
- Android SDK API 34
- JDK 11+
- Gradle 8.5

## 已知问题解决
1. **Gradle同步慢** - 使用国内镜像
2. **缺少SDK** - 安装Android SDK Command Line Tools
3. **构建失败** - 检查网络和代理设置

## 快速测试
核心爬虫逻辑已通过单元测试，代码功能正常。

## 联系方式
如有构建问题，请联系开发者。