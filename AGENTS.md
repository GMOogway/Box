# 项目概述

Box (TVBox) 是一个基于 Android 的电视媒体播放器应用，支持多种视频源播放、直播、历史记录、收藏等功能。该项目采用 Kotlin + Java 混合开发，集成了多种播放器（系统播放器、IJKPlayer、ExoPlayer），并支持 Python 和 JavaScript 插件扩展。

## 技术栈

- **语言**: Kotlin 1.9.25, Java 8
- **构建工具**: Gradle 7.4.2
- **目标 SDK**: 28
- **编译 SDK**: 34
- **最低 SDK**: 21
- **架构组件**: Room (数据库), DataBinding, ViewModel
- **网络**: OkHttp 3.12.11
- **图片加载**: Glide 4.16.0
- **播放器**: 
  - Media3 (ExoPlayer) 1.3.1
  - IJKPlayer
  - 系统播放器
  - 阿里云播放器 5.5.2.0
- **其他关键依赖**:
  - Kotlin 标准库
  - AndroidX 组件 (AppCompat, ConstraintLayout, RecyclerView, Material)
  - EventBus 3.3.1
  - Hawk 2.0.1 (轻量级存储)
  - DanmakuFlameMaster 0.9.25 (弹幕)
  - Jsoup 1.16.1 (HTML 解析)
  - AndServer 2.1.12 (Web 服务器)
  - Chaquo Python 12.0.1 (Python 支持)

## 项目结构

```
Box/
├── app/                          # 主应用模块
│   ├── src/main/
│   │   ├── java/com/github/tvbox/osc/
│   │   │   ├── api/             # API 接口
│   │   │   ├── base/            # 基础类 (App, BaseActivity, BaseLazyFragment)
│   │   │   ├── bean/            # 数据模型
│   │   │   ├── cache/           # 缓存管理
│   │   │   ├── callback/        # 回调接口
│   │   │   ├── data/            # 数据管理 (Room 数据库)
│   │   │   ├── event/           # 事件总线
│   │   │   ├── player/          # 播放器封装
│   │   │   ├── receiver/        # 广播接收器
│   │   │   ├── server/          # Web 服务器
│   │   │   ├── subtitle/        # 字幕处理
│   │   │   ├── ui/              # UI 界面
│   │   │   │   └── activity/    # Activity (HomeActivity, PlayActivity 等)
│   │   │   ├── util/            # 工具类
│   │   │   ├── viewmodel/       # ViewModel
│   │   │   └── widget/          # 自定义控件
│   │   ├── jniLibs/             # 原生库 (IJKPlayer, P2P, Thunder SDK)
│   │   └── res/                 # 资源文件
│   │       ├── drawable/        # 图片资源
│   │       ├── layout/          # 布局文件
│   │       └── values/          # 值资源
│   └── libs/                    # JAR 库文件
├── pyramid/                     # Python 扩展模块
│   └── src/python/
│       ├── app.py               # Python 主入口
│       ├── runner.py            # Python 运行器
│       ├── trigger.py           # 触发器
│       └── base/                # Python 基础库
│           ├── htmlParser.py    # HTML 解析
│           ├── localProxy.py    # 本地代理
│           └── spider.py        # 爬虫基础
├── quickjs/                     # QuickJS 模块 (JavaScript 支持)
└── xwalk/                       # Crosswalk 相关文件
```

## 构建配置

项目支持多维度构建配置：

### ABI 架构
- `armeabi`: ARMv7 (32位)
- `arm64`: ARMv8 (64位)

### 品牌变体
- `generic`: 通用版本
- `hisense`: 海信定制版本 (applicationId: com.github.hisense.osc.tk)

### 模式变体
- `normal`: Java 版本
- `python`: Python 支持版本

### 构建命令

```bash
# 清理项目
./gradlew clean

# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本
./gradlew assembleRelease

# 构建特定变体 (例如: arm64 + generic + normal 的 release 版本)
./gradlew assembleArm64GenericNormalRelease

# 构建所有变体
./gradlew assembleRelease --build-cache --parallel --daemon
```

## 默认配置

应用默认配置可在 `/src/main/java/com/github/tvbox/osc/base/App.java` 中的 `initParams()` 方法修改：

```java
putDefault(HawkConfig.HOME_REC, 2);       // 首页推荐: 0=豆瓣, 1=推荐, 2=历史
putDefault(HawkConfig.PLAY_TYPE, 1);      // 播放器: 0=系统, 1=IJK, 2=Exo
putDefault(HawkConfig.IJK_CODEC, "硬解码"); // IJK 解码方式: 软解码/硬解码
putDefault(HawkConfig.HOME_SHOW_SOURCE, true); // 是否显示源
putDefault(HawkConfig.HOME_NUM, 2);       // 历史记录数量
putDefault(HawkConfig.DOH_URL, 2);        // DNS 配置
putDefault(HawkConfig.SEARCH_VIEW, 2);    // 搜索视图: 文本/图片
```

## 主要功能

1. **多播放器支持**: 系统播放器、IJKPlayer、ExoPlayer、阿里云播放器
2. **视频源管理**: 支持多种视频源配置和导入
3. **直播播放**: 支持直播流播放
4. **点播功能**: 支持点播视频、剧集选择
5. **历史记录**: 自动记录观看历史
6. **收藏功能**: 支持收藏视频
7. **搜索功能**: 支持视频搜索
8. **弹幕支持**: 集成弹幕显示功能
9. **WebDAV 支持**: 支持从 WebDAV 服务器播放视频
10. **本地代理**: 支持本地代理播放
11. **Python 扩展**: 支持 Python 脚本扩展功能
12. **JavaScript 支持**: 支持 JS 脚本扩展
13. **字幕支持**: 支持外挂字幕
14. **EPG 支持**: 电子节目单支持
15. **远程控制**: 支持 Web 远程控制

## 权限要求

应用需要以下权限：
- INTERNET: 网络访问
- ACCESS_NETWORK_STATE / ACCESS_WIFI_STATE: 网络状态
- READ_EXTERNAL_STORAGE / WRITE_EXTERNAL_STORAGE / MANAGE_EXTERNAL_STORAGE: 存储访问
- REQUEST_INSTALL_PACKAGES / REQUEST_DELETE_PACKAGES: 应用安装/卸载
- QUERY_ALL_PACKAGES: 查询所有应用
- FOREGROUND_SERVICE: 前台服务 (播放服务)

## CI/CD

项目使用 GitHub Actions 进行自动化构建，配置文件位于 `.github/workflows/test.yml`。

构建流程：
1. 检出代码
2. 执行 `./gradlew assemblerelease` 构建所有 release 版本
3. 收集生成的 APK 文件
4. 上传到 GitHub Artifacts

## 开发注意事项

1. **代码混淆**: Release 版本启用了 ProGuard 混淆，配置文件为 `proguard-rules.pro` 和 `proguard-python.pro`
2. **多 dex 支持**: 启用了 `multiDexEnabled true` 以支持方法数超过 64K 的应用
3. **屏幕适配**: 使用 AutoSize 库进行屏幕适配，设计尺寸为 1280x720
4. **字体支持**: 使用 Calligraphy 库支持自定义字体
5. **数据绑定**: 启用了 DataBinding 以简化 UI 代码
6. **Python 路径**: Python 构建需要配置 Python 路径，当前配置为 Windows 路径 `D:/Programs/Python/Python38/python.exe`，在不同环境需要调整
7. **原生库**: 包含多种原生库 (IJKPlayer, P2P, Thunder SDK)，支持 ARMv7 和 ARMv8 架构

## 数据库

使用 Room 数据库，Schema 文件位于 `app/schemas/com.github.tvbox.osc.data.AppDataBase/`：
- `1.json`: 初始数据库版本
- `3.json`: 当前数据库版本

## 常见问题

1. **Python 构建失败**: 检查 `pyramid/build.gradle` 中的 Python 路径配置是否正确
2. **播放器选择**: 可在设置中切换不同的播放器
3. **源配置**: 支持通过多种方式导入视频源配置
4. **字幕显示**: 需要字幕文件与视频文件同名或在相同目录

## 测试

当前项目配置了 GitHub Actions 测试构建，可通过手动触发 (`workflow_dispatch`) 来执行构建测试。