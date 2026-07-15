# Sea of Voice 🌊🎵

**Sea of Voice** 是一款基于 **Radio Browser API** 的 Android 网络广播播放器，采用现代 Android 开发技术栈（Jetpack Compose + Material 3）。它帮助你发现全球超过 50,000 个广播电台，跨越国家、语言和风格，让世界的声音触手可及。

**Sea of Voice (海之声)** is an Android internet radio player powered by the [Radio Browser API](https://de1.api.radio-browser.info). Built with Jetpack Compose and Material 3, it helps you discover 50,000+ radio stations across countries, languages, and genres — bringing the world's voices to your fingertips.

---

## ✨ Features / 功能

- 🌍 **全球电台发现 (Global Station Discovery)** — 按国家、语言、标签浏览 50k+ 电台
- 🔥 **热门推荐 (Trending & Featured)** — 查看投票最高、点击最多的热门电台
- ❤️ **收藏管理 (Favorites / Library)** — 收藏你喜爱的电台，快速访问
- 🔎 **全局搜索 (Global Search)** — 统一胶囊搜索框，快速找到你想听的电台
- 🎨 **沉浸播放体验 (Immersive Player)** — 全屏模态播放页，带动态毛玻璃效果
- 🎚️ **悬浮 Mini 播放条 (Floating Mini Player)** — 悬浮在导航栏上方，随时控制播放
- 🌓 **动态主题 (Dynamic Theme)** — 自动适配系统深色/浅色模式
- 🏁 **国旗图标 (Flag Icons)** — 近 200 个国家/地区矢量国旗，浏览体验更直观
- 🔄 **分页浏览 (Paging)** — 流畅加载海量电台数据
- ⏰ **睡眠定时器 (Sleep Timer)** — 设定时间自动停止播放
- 📊 **音频可视化 (Audio Visualizer)** — 播放时的实时频谱动画
- 🖼️ **Lottie 动画 (Lottie Animations)** — 空状态等场景的生动动画反馈

## 📸 Screens / 页面

| 页面 | 说明 |
|------|------|
| **发现 (Discover)** | 热门推荐、最近常听、本地热门、投票最高 |
| **分类 (Browse)** | 按照国家、语言、标签多维度浏览 |
| **分类详情 (Category Detail)** | 某个分类下的电台列表 |
| **心愿 (Library)** | 收藏电台、收听历史 |
| **播放器 (Player)** | 全屏沉浸式播放界面 |
| **搜索 (Search)** | 全局覆盖层搜索 |
| **设置 (Settings)** | 个性化配置与偏好 |

## 🛠️ Tech Stack / 技术栈

| Layer | Technology |
|-------|-----------|
| 🎨 UI | **Jetpack Compose** + **Material 3** (Adaptive) |
| 🧭 Navigation | **Compose Navigation** + **Navigation 3 Suite** |
| 🎵 Playback | **Media3 ExoPlayer** (HLS, DASH) + MediaSession |
| 🌐 Networking | **Ktor** (OkHttp engine) + **kotlinx.serialization** |
| 🗄️ Local DB | **Room** (SQLite) |
| ⚙️ DI | **Hilt** |
| 📄 Preferences | **Proto DataStore** |
| 📃 Paging | **Paging 3** (Compose) |
| 🖼️ Images | **Coil** (Compose) |
| 🎬 Animations | **Lottie Compose** |
| 🔧 Build | **Gradle Kotlin DSL** + KSP + Protobuf |

## 📋 Requirements / 系统要求

- **Android 8.0+** (API 26+)
- **Jetpack Compose** enabled
- **Kotlin** 2.0+

## 🚀 Getting Started / 快速开始

```bash
# Clone the repository
git clone https://github.com/wceng/SeaofVoice.git

# Open with Android Studio
# Wait for Gradle sync, then run on device/emulator
```

> The app requires no API key — it uses the public [Radio Browser API](https://de1.api.radio-browser.info).

## 📁 Project Structure / 项目结构

```
app/
├── src/main/java/dev/wceng/seaofvoice/
│   ├── data/
│   │   ├── api/          # Ktor API client & DTOs
│   │   ├── db/           # Room database, DAOs, entities
│   │   ├── datastore/    # Proto DataStore preferences
│   │   ├── model/        # Domain models
│   │   └── repository/   # Repository + PagingSource
│   ├── di/               # Hilt modules
│   ├── domain/usecase/   # Use cases
│   ├── player/           # MediaService, PlaybackManager, Visualizer
│   └── ui/
│       ├── components/   # Reusable composables
│       ├── navigation/   # Destinations & Navigator
│       ├── screens/      # Screen composables + ViewModels
│       └── theme/        # Color, Type, Theme
└── src/main/proto/       # Protobuf definitions
```

## 📄 License

```
Copyright © 2025 wceng

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

**Sea of Voice** — Let the world speak to you. 🌊
