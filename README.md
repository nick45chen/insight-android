> 本專案參考自 [Insight](https://github.com/Ping-Pang-Qiu/Insight)，並基於其概念進行 Android 平台的實作。

# Insight-android

Android 應用程式資料檢視工具，用於檢視目標 App 的 SharedPreferences、檔案系統、資料庫等私有資料。
適用於 MAS 資安認證檢測，驗證敏感性資料是否以明文儲存。

## 運作原理

透過 Android 的 `sharedUserId` 機制，讓本 App 與目標 App 共享相同的 Linux UID，
從而能直接存取目標 App 的私有資料目錄（`/data/data/<package>/`）。

**前提條件：**
- 兩個 App 必須使用**相同的 `sharedUserId`**
- 兩個 App 必須使用**相同的憑證簽署**
- 目標 App 需要**先解除安裝再重新安裝**（因為新增 `sharedUserId` 會改變 UID）

## 設定方式

### 1. 建立 `signing.properties`

複製範本檔案並填入實際值（此檔案已加入 `.gitignore`，不會被提交）：

```bash
cp signing.properties.example signing.properties
```

```properties
# 目標 App 的 package name
targetPackageName=com.example.target

# Keystore 檔案路徑（相對於 app/ 目錄）
storeFile=release.keystore

# 簽署憑證設定（必須與目標 App 使用相同的 keystore）
storePassword=YOUR_STORE_PASSWORD
keyAlias=YOUR_KEY_ALIAS
keyPassword=YOUR_KEY_PASSWORD
```

| 參數 | 說明 |
|------|------|
| `targetPackageName` | 要檢視的目標 App package name |
| `storeFile` | Keystore 檔案名稱（放在 `app/` 目錄下） |
| `storePassword` | Keystore 密碼 |
| `keyAlias` | Key 別名 |
| `keyPassword` | Key 密碼 |

### 2. 放置 Keystore 檔案

將目標 App 的 keystore 檔案放到 `app/` 目錄下，檔名須與 `signing.properties` 中的 `storeFile` 一致。

### 3. 目標 App 新增 sharedUserId

在目標 App 的 `AndroidManifest.xml` 的 `<manifest>` 標籤中加入：

```xml
android:sharedUserId="com.example.target"
```

其中 `com.example.target` 替換為目標 App 的 package name（需與 `targetPackageName` 一致）。

> **注意：** 新增 `sharedUserId` 後，裝置上已安裝的目標 App 必須先解除安裝，再重新安裝。

### 4. 查詢 Keystore 資訊

如果不確定 keystore 的 alias，可用以下指令查詢：

```bash
keytool -list -keystore app/YOUR_KEYSTORE.keystore
```

## 建置與安裝

```bash
# 建置 debug APK
./gradlew assembleDebug

# 直接安裝到連接的裝置
./gradlew installDebug
```

## 使用步驟

1. 先在裝置上解除安裝原本的目標 App
2. 安裝加入 `sharedUserId` 的目標 App
3. 安裝本 Insight App
4. 開啟 Insight App，即可瀏覽目標 App 的：
   - **Process Info** — 執行緒、FD、記憶體使用量
   - **Internal Storage** — 內部儲存檔案瀏覽
   - **External Storage** — 外部儲存檔案瀏覽
   - **SharedPreferences** — 所有 SP 檔案及 key-value 內容
   - **Database** — 資料庫檔案列表

## 功能說明

| 功能 | 說明 |
|------|------|
| SharedPreferences 瀏覽 | 列出所有 SP 檔案，可查看每個 key-value 的型別、長度與完整內容 |
| 檔案瀏覽器 | 瀏覽目標 App 的內部/外部儲存，可查看文字檔內容（< 200KB） |
| 資料庫列表 | 列出所有 `.db` 檔案與大小 |
| Process 資訊 | 顯示執行緒數、FD 數、記憶體佔用 |
| 複製功能 | SP 值與檔案內容可複製到剪貼簿 |

## 切換檢測目標

若要檢測其他 App，只需修改 `signing.properties` 中的 `targetPackageName` 與 `storeFile`，
並確保使用該 App 的 keystore 簽署。

## 專案結構

```
app/src/main/java/com/example/insight_android/
├── MainActivity.kt          # 進入點，Compose Navigation
├── TargetApp.kt             # 目標 App Context 管理
├── model/
│   ├── SpModel.kt           # SharedPreferences 資料模型
│   ├── FileModel.kt         # 檔案系統資料模型
│   ├── DbModel.kt           # 資料庫資料模型
│   └── ProcessModel.kt      # Process 資訊模型
├── screen/
│   ├── HomeScreen.kt        # 首頁總覽
│   ├── SpListScreen.kt      # SP 檔案列表
│   ├── SpContentScreen.kt   # SP Key-Value 列表
│   ├── SpDetailScreen.kt    # SP 值詳情
│   ├── FileListScreen.kt    # 檔案瀏覽器
│   ├── FileTextScreen.kt    # 文字檔檢視
│   └── DbListScreen.kt      # 資料庫列表
├── navigation/
│   └── Navigation.kt        # Route 定義
├── util/
│   └── Utilities.kt         # 格式化工具
└── ui/theme/                 # Material 3 主題
```
