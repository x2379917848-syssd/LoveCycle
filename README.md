# 她的小日历 (LoveCycle)

一款温柔、私密、实用的 Android App，用于记录经期、预测下次经期、记录心情和症状，并支持定时喝水提醒。

🌸 **用心记录，温柔陪伴** 🌸

## 功能列表

### 核心功能
- **首页** - 显示经期预测、喝水进度、每日关心语
- **经期记录** - 添加、编辑、删除经期记录（日期、流量、疼痛程度、心情、症状、备注）
- **经期预测** - 基于历史数据预测下次经期、排卵期、易孕期
- **喝水记录** - 记录每日喝水量，支持自定义和撤销
- **喝水提醒** - 定时提醒喝水，支持自定义间隔
- **心情日记** - 记录每日心情和标签
- **统计页面** - 查看经期统计、喝水趋势、心情分布

### 隐私保护
- 数据默认只保存在本机
- 不上传任何隐私数据
- 不接入广告
- 不接入第三方统计

### 技术特点
- Kotlin + Jetpack Compose
- Material 3 设计语言
- Room 数据库本地存储
- DataStore 设置持久化
- WorkManager 后台提醒
- MVVM 架构

## 隐私说明

她的小日历是一款私人记录工具：
- 所有数据（经期、喝水、日记等）默认只保存在您的手机本地
- 不上传任何隐私数据到服务器
- 不接入任何广告 SDK
- 不接入任何第三方统计 SDK
- 经期预测仅供参考，不作为医疗建议

## 医疗免责声明

经期和排卵期预测功能仅供参考，不作为医疗建议。如果身体明显不适，请及时咨询医生。

## 本地构建

### 环境要求
- JDK 17+
- Android SDK (API 34)
- Gradle 8.4

### 构建步骤

```bash
# 1. 克隆项目
git clone <repository-url>
cd LoveCycle

# 2. 设置执行权限（Linux/Mac）
chmod +x ./gradlew

# 3. 清理并构建 Debug APK
./gradlew clean assembleDebug

# 4. APK 输出位置
# app/build/outputs/apk/debug/app-debug.apk
```

### Windows 构建

```powershell
# 在项目目录下执行
.\gradlew.bat clean assembleDebug
```

## GitHub Actions 构建

项目已配置 GitHub Actions 自动化构建。

### 自动构建触发条件
- 推送代码到 main 分支
- 提交 Pull Request 到 main 分支
- 手动触发 workflow_dispatch

### 下载 APK

1. 进入项目的 **Actions** 页面
2. 选择最新的 workflow run
3. 在 Artifacts 部分下载 **app-debug-apk**
4. 解压后将 APK 安装到手机

## 安装到手机

### 方法一：直接安装
1. 将手机通过数据线连接到电脑
2. 将 `app-debug.apk` 复制到手机存储
3. 在手机上打开 APK 文件进行安装
4. 如提示"安装来源未知"，请在设置中允许

### 方法二：adb 安装
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 首次运行
1. 首次打开 App 时，会请求通知权限（用于喝水提醒）
2. 建议允许通知以确保收到提醒

## 已知限制

1. **提醒时间**：提醒间隔最低为 15 分钟（Android 系统限制）
2. **跨天提醒**：暂不支持跨天提醒（如 22:00 到次日 09:00）
3. **精确闹钟**：Android 14+ 涉及精确闹钟时需额外权限
4. **PIN 锁**：基础 PIN 保护已实现，安全性有限

## 后续优化计划

- [ ] 数据导出/导入功能（JSON）
- [ ] PIN 锁增强
- [ ] 纪念日功能
- [ ] 深色模式细节优化
- [ ] 经期日历视图
- [ ] 多语言支持

## 项目结构

```
LoveCycle/
├── app/
│   └── src/main/
│       ├── java/com/lovecare/cycle/
│       │   ├── data/
│       │   │   ├── entity/      # Room 实体
│       │   │   ├── dao/         # Room DAO
│       │   │   ├── database/    # 数据库
│       │   │   └── repository/  # 仓库
│       │   ├── ui/
│       │   │   ├── screens/     # 页面
│       │   │   ├── components/ # 组件
│       │   │   ├── theme/      # 主题
│       │   │   └── navigation/  # 导航
│       │   ├── viewmodel/      # ViewModel
│       │   ├── reminder/       # 提醒
│       │   └── util/           # 工具
│       └── res/                # 资源
├── .github/workflows/          # GitHub Actions
├── build.gradle.kts            # 根构建配置
├── settings.gradle.kts         # 项目设置
└── gradle/                    # Gradle Wrapper
```

## 版本信息

- **版本号**：1.0.0
- **最低 SDK**：Android 6.0 (API 23)
- **目标 SDK**：Android 14 (API 34)

---

💕 Made with love for her
