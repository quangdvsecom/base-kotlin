# Game Module — Tài liệu kiến trúc & Hướng dẫn thêm Game mới

> Module: `com.el.mybasekotlin.ui.fragment.game`
> Mục đích: Cung cấp nền tảng (framework) thống nhất để các thành viên team có thể xây dựng một mini‑game mới (dưới dạng **Fragment** hoặc **Custom View**) và "cắm" vào màn `GamePlayScreenFragment` mà **không phải** đụng vào logic camera, record, điều khiển vòng đời game.

---

## 1. Tổng quan các file trong module

| File | Vai trò |
|------|---------|
| [GamePlayScreenFragment.kt](GamePlayScreenFragment.kt) | Màn hình host chứa game. Quản lý camera + record + render game content. |
| [GamePlayViewModel.kt](GamePlayViewModel.kt) | ViewModel dùng chung (shared) giữa `GamePlayScreenFragment` và các fragment game con. Giữ `currentGame`, `faceData`, `isRecording`, `gameState`, `setUpCamera`. |
| [GameFactory.kt](GameFactory.kt) | **Điểm cắm game mới**. Map `GameType` → `DetailsContent` (Fragment hoặc View). |
| [GameInterface.kt](GameInterface.kt) | Định nghĩa các interface: `GameController`, `DataController`, `GameViewListener`. |
| [BaseGameCustomView.kt](BaseGameCustomView.kt) | Base class cho game xây bằng **Custom View** (FrameLayout). |
| [BaseGameFragment.kt](BaseGameFragment.kt) | Base class cho game xây bằng **Fragment**. |
| [../../data/model/game/GameData.kt](../../../data/model/game/GameData.kt) | Model `GameData` + enum `GameType`, `GameDetailsContentType`. |
| [../camera/CameraConfig.kt](../camera/CameraConfig.kt) | `CameraConfig`, `DetectType`, `DetectionMode`. |
| [../gamedetails/MyGameCustomViewExample.kt](../gamedetails/MyGameCustomViewExample.kt) | Game mẫu dạng **Custom View**. |
| [../gamedetails/MyFragmentGameExample.kt](../gamedetails/MyFragmentGameExample.kt) | Game mẫu dạng **Fragment**. |
| [../gamedetails/EmptyGameCustom.kt](../gamedetails/EmptyGameCustom.kt) | Game fallback khi `GameType` không được hỗ trợ. |

---

## 2. Kiến trúc tổng quan

```
                    ┌──────────────────────────────────────┐
                    │       GamePlayScreenFragment         │
                    │  (HOST — camera, record, navigation) │
                    └──────────────────────────────────────┘
                                    │
                 ┌──────────────────┼──────────────────┐
                 ▼                  ▼                  ▼
        ┌──────────────┐   ┌──────────────┐   ┌──────────────────┐
        │ CameraView   │   │ GamePlayVM   │   │ contentContainer │
        │ (recorder +  │   │ (shared:     │   │ (FrameLayout)    │
        │ face detect) │   │  gameData,   │   │                  │
        └──────────────┘   │  faceData,   │   └────────┬─────────┘
                           │  isRecording)│            │
                           └──────────────┘            │
                                                       │
                               GameFactory.create(gameData)
                                                       │
                    ┌──────────────────────────────────┴───────────────────────┐
                    ▼                                                          ▼
          ┌──────────────────────┐                              ┌────────────────────────┐
          │ DetailsContent       │                              │ DetailsContent         │
          │    .ViewContent      │                              │    .FragmentContent    │
          │                      │                              │                        │
          │ BaseGameCustomView   │                              │ BaseGameFragment<VB>   │
          │  (FrameLayout)       │                              │  (Fragment + binding)  │
          └──────────┬───────────┘                              └───────────┬────────────┘
                     │                                                      │
                     └────────────┬─────────────────────────────────────────┘
                                  ▼
                     Implement: GameController + DataController
                     Callback:  GameViewListener  (score, state, finish, error)
```

### Luồng dữ liệu chính

1. **Người dùng chọn game** (ở màn list) → navigate sang `gamePlayFragment` kèm `Bundle{"KEY_GAME_MODEL" = GameData}`.
2. `GamePlayScreenFragment.initDataBeforeCreateView()` đọc `GameData` → `gamePlayViewModel.initGameData(...)`.
3. ViewModel emit `currentGame` + `setUpCamera` (`CameraConfig` được build từ `gameData.cameraDetectType`).
4. Fragment observe `currentGame` → gọi `renderContent(gameData)` → **GameFactory.create(gameData)**.
5. `GameFactory` trả về:
   - `DetailsContent.ViewContent` → inflate `BaseGameCustomView` vào `contentContainer`.
   - `DetailsContent.FragmentContent` → `childFragmentManager.replace(R.id.contentContainer, fragment)`.
6. `GamePlayScreenFragment` ép kiểu game về `GameController` / `DataController` và gán `listener = gameViewListener` để nhận callback.
7. Khi ấn nút record (`btnStart`) → `toggleRecording()` → `takeVideoSnapshot(file)`. `CameraListener.onVideoRecordingStart` → `gameController?.start()`.
8. Mỗi frame face detect → `FaceAnalyzerCameraView` → `gamePlayViewModel.updateFaceData(result)` **và** `dataController?.updateDataFaceDetect(result)` (Custom View dùng đường này, Fragment thường dùng shared ViewModel).

---

## 3. Các Interface cốt lõi

### 3.1. `GameController` — Host điều khiển game
```kotlin
interface GameController {
    val gameState: GameState
    fun setup(); fun start(); fun stop()
    fun pause(); fun resume(); fun reset(); fun destroy()
}
```
`GamePlayScreenFragment` chỉ nói chuyện với game qua interface này — **không care** game là Fragment hay View.

### 3.2. `DataController` — Đẩy data camera vào game
```kotlin
interface DataController {
    fun updateDataFaceDetect(faceData: FaceStateResult)
    // (mở rộng thêm hand/body khi cần)
}
```

### 3.3. `GameViewListener` — Game báo event ngược lên Host
```kotlin
interface GameViewListener {
    fun onGameStateChanged(state: GameState)
    fun onScoreUpdated(score: Int)
    fun onGameFinished(score: Int, extraData: Map<String, Any>)
    fun onGameError(error: String)
}
```

### 3.4. `GameState`
```
IDLE → PLAYING → PAUSED → PLAYING → STOPPED / FINISHED
```

---

## 4. State machine (vòng đời game)

```
       setup()                start()
 IDLE ─────────▶ IDLE ───────────────▶ PLAYING ◀──┐
                                        │         │ resume()
                                        │ pause() │
                                        ▼         │
                                      PAUSED ─────┘
                                        │
                                        │ stop() / reportFinished()
                                        ▼
                                   STOPPED / FINISHED
                                        │
                                        │ destroy()
                                        ▼
                                    (cleanup)
```
Các hook `onStartGame / onPauseGame / onResumeGame / onStopGame / onResetGame / onDestroyGame` được base class gọi tự động — class con **chỉ cần override** phần logic game của mình.

---

## 5. Mô hình thêm 1 GAME MỚI

```
┌──────────────────────────────────────────────────────────────────────┐
│  BƯỚC 1: Khai báo GameType mới trong GameData.kt                     │
│  ─────────────────────────────────────────────────────────────────   │
│  enum class GameType(val value: Int, val description: String) {      │
│      RANKING_FILTER_GAME(1, "..."),                                  │
│      MATH_RUN_GAME(2,     "..."),                                    │
│      MY_NEW_GAME(3,       "Game mới của tôi");   ◀── THÊM DÒNG NÀY   │
│  }                                                                   │
└──────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌──────────────────────────────────────────────────────────────────────┐
│  BƯỚC 2: Chọn hình thức game                                         │
│  ─────────────────────────────────────────────────────────────────   │
│                                                                      │
│    (A) CUSTOM VIEW            hoặc           (B) FRAGMENT            │
│    ──────────────                             ─────────────          │
│    + Nhẹ, render nhanh                       + Dùng được Hilt, VM    │
│    + Hợp với GLSurface,                      + Dùng được Navigation  │
│      canvas, game vẽ tay                     + Có binding riêng      │
│    + Không cần Fragment lifecycle            + Phù hợp game nhiều UI │
│                                                                      │
│    ==> Extend BaseGameCustomView             ==> Extend BaseGameFragment
└──────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌──────────────────────────────────────────────────────────────────────┐
│  BƯỚC 3: Tạo class game trong                                        │
│           ui/fragment/gamedetails/MyNewGame*.kt                      │
│  ─────────────────────────────────────────────────────────────────   │
│  - Override các hook bắt buộc:                                       │
│      onSetupGame, onStartGame, onStopGame, onPauseGame,              │
│      onResumeGame, onResetGame, onDestroyGame                        │
│  - (Optional) override onFaceDataUpdated(faceData)                   │
│  - Dùng reportScore / reportFinished / reportError khi cần.          │
└──────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌──────────────────────────────────────────────────────────────────────┐
│  BƯỚC 4: Đăng ký vào GameFactory.kt                                  │
│  ─────────────────────────────────────────────────────────────────   │
│  when (gameData.gameType) {                                          │
│      GameType.RANKING_FILTER_GAME.value -> { ... }                   │
│      GameType.MATH_RUN_GAME.value       -> { ... }                   │
│      GameType.MY_NEW_GAME.value -> {                      ◀── THÊM   │
│          // Nếu là Custom View:                                      │
│          DetailsContent.ViewContent { ctx ->                         │
│              MyNewGameView(ctx, gameData)                            │
│          }                                                           │
│          // Nếu là Fragment:                                         │
│          // DetailsContent.FragmentContent(                          │
│          //     MyNewGameFragment().apply {                          │
│          //         arguments = bundleOf("KEY_GAME_DATA" to gameData)│
│          //     })                                                   │
│      }                                                               │
│      else -> DetailsContent.ViewContent { EmptyGameCustom(it) }      │
│  }                                                                   │
└──────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌──────────────────────────────────────────────────────────────────────┐
│  BƯỚC 5: Cấu hình GameData (source JSON / DB / mock)                 │
│  ─────────────────────────────────────────────────────────────────   │
│  {                                                                   │
│    "id": 301,                                                        │
│    "categorizeID": 3,          // = GameType.MY_NEW_GAME.value       │
│    "detailsType": 2,           // 1=Fragment, 2=CustomView           │
│    "cameraDetectType": 1,      // DetectType.FACE_DETECT             │
│    "minTimeToPlay": 60000,                                           │
│    "gameResourcePath": 301,                                          │
│    "gameContentDataPath": "{...}",                                   │
│    ...                                                               │
│  }                                                                   │
└──────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
                             ✅ XONG
```

---

## 6. Template code — Copy & chỉnh

### 6.1. Game dạng **Custom View**

```kotlin
// ui/fragment/gamedetails/MyNewGameView.kt
package com.el.mybasekotlin.ui.fragment.gamedetails

import android.content.Context
import android.view.LayoutInflater
import com.el.mybasekotlin.data.model.game.GameData
import com.el.mybasekotlin.databinding.MyNewGameViewBinding
import com.el.mybasekotlin.ui.fragment.camera.FaceStateResult
import com.el.mybasekotlin.ui.fragment.game.BaseGameCustomView

class MyNewGameView(
    context: Context,
    private val gameData: GameData
) : BaseGameCustomView(context) {

    private val binding = MyNewGameViewBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    override fun onSetupGame() {
        // parse gameData, load tài nguyên
    }

    override fun onStartGame() { /* start animation, timer... */ }
    override fun onStopGame()  { /* dừng hoàn toàn */ }
    override fun onPauseGame() { /* freeze */ }
    override fun onResumeGame() { /* tiếp tục */ }
    override fun onResetGame()  { /* reset state */ }
    override fun onDestroyGame(){ /* giải phóng resource */ }

    override fun onFaceDataUpdated(faceData: FaceStateResult) {
        // logic game theo face data (chỉ gọi khi PLAYING)
        if (faceData.angleX > 20f) reportScore(+1)
    }
}
```

### 6.2. Game dạng **Fragment**

```kotlin
// ui/fragment/gamedetails/MyNewGameFragment.kt
package com.el.mybasekotlin.ui.fragment.gamedetails

import androidx.fragment.app.viewModels
import com.el.mybasekotlin.databinding.MyNewGameFragmentBinding
import com.el.mybasekotlin.ui.fragment.game.BaseGameFragment
import com.el.mybasekotlin.ui.fragment.game.GamePlayViewModel
import com.el.mybasekotlin.utils.extension.collectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyNewGameFragment :
    BaseGameFragment<MyNewGameFragmentBinding>(MyNewGameFragmentBinding::inflate) {

    // Share ViewModel với GamePlayScreenFragment để lấy faceData, gameData chung
    private val sharedVM: GamePlayViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onSetupGame() {}
    override fun onStartGame() { binding.tvStatus.text = "Start" }
    override fun onStopGame()  {}
    override fun onPauseGame() {}
    override fun onResumeGame(){}
    override fun onResetGame() {}
    override fun onDestroyGame(){}

    override fun initObserver() {
        sharedVM.faceData.collectIn(this) {
            // cập nhật UI theo face data
        }
    }
}
```

### 6.3. Đăng ký vào `GameFactory`

```kotlin
// GameFactory.kt
GameType.MY_NEW_GAME.value -> {
    // --- Lựa chọn 1: Custom View ---
    DetailsContent.ViewContent { ctx ->
        MyNewGameView(ctx, gameData)
    }

    // --- Lựa chọn 2: Fragment ---
    // val fragment = MyNewGameFragment().apply {
    //     arguments = Bundle().apply { putParcelable("KEY_GAME_DATA", gameData) }
    // }
    // DetailsContent.FragmentContent(fragment)
}
```

---

## 7. Quy tắc & Lưu ý khi code game

1. **KHÔNG tự ý `start()` trong constructor game**. Host (`GamePlayScreenFragment`) chịu trách nhiệm gọi `start()` sau khi record bắt đầu (`onVideoRecordingStart`). Nếu muốn game chạy ngay cả khi không record, gọi `view.start()` ngay sau khi add vào container — như code hiện tại đang làm với Custom View trong `renderContent()`.
2. **Luôn giải phóng resource trong `onDestroyGame()`** — bitmap, animator, MediaPlayer, coroutine… nếu không sẽ leak vì host có thể `replace` game khác.
3. **Fragment game nên dùng shared ViewModel** (`viewModels(ownerProducer = { requireParentFragment() })`) để tránh duplicate camera listener. Custom View được push data trực tiếp qua `updateDataFaceDetect()`.
4. **`reportScore / reportFinished / reportError`** là API duy nhất để báo event ngược lên host — KHÔNG truy cập trực tiếp `GamePlayScreenFragment` hay `GamePlayViewModel`.
5. **Camera config** được lấy từ `gameData.cameraDetectType`. Nếu game mới cần detect khác (hand/body) → thêm case mới trong `GamePlayScreenFragment.handleSetupConfigCamera()` **và** bổ sung `DataController` mở rộng.
6. **`detailsType` trong GameData** hiện chưa được `GameFactory` sử dụng để branch — nhánh được quyết định **hoàn toàn** bởi `GameFactory.create()`. Khi thêm game, đừng quên điền `detailsType` đúng với hình thức đã đăng ký (để list/UI ngoài dùng thông tin này).
7. **Fallback** — bất kỳ `gameType` chưa được đăng ký đều về `EmptyGameCustom`. Để debug nhanh khi add game mới mà thấy màn hình trống → nhiều khả năng quên nhánh `when` trong `GameFactory`.

---

## 8. Checklist khi thêm game mới

- [ ] Thêm entry vào `enum GameType` ở [GameData.kt](../../../data/model/game/GameData.kt).
- [ ] Tạo class game: `MyNewGameView` hoặc `MyNewGameFragment` trong `ui/fragment/gamedetails/`.
- [ ] Tạo layout XML + ViewBinding tương ứng nếu cần.
- [ ] Đăng ký nhánh `when` mới trong [GameFactory.kt](GameFactory.kt).
- [ ] Nếu là Fragment → thêm `@AndroidEntryPoint` (khi dùng Hilt) và khai báo shared ViewModel nếu cần.
- [ ] Cấu hình 1 bản ghi `GameData` (mock/JSON/DB) với `categorizeID = GameType.MY_NEW_GAME.value`.
- [ ] Nếu cần detect type mới → cập nhật `handleSetupConfigCamera()` ở [GamePlayScreenFragment.kt:201](GamePlayScreenFragment.kt).
- [ ] Test vòng đời: mở game → record → stop → back → mở lại (kiểm tra leak / state).

---

## 9. FAQ nhanh

**Q: Tôi muốn game chạy ngay khi mở màn, không cần bấm record?**
→ Hiện `renderContent()` đã tự `view.start()` với Custom View (xem [GamePlayScreenFragment.kt:302](GamePlayScreenFragment.kt)). Với Fragment, host đang gọi `gameController?.start()` ở `init()` — tùy business mà giữ/bỏ.

**Q: Hai game chạy song song được không?**
→ Không. `contentContainer` chỉ host 1 game tại một thời điểm; `clearCurrentContent()` dọn sạch trước khi render mới.

**Q: Làm sao chia sẻ data giữa Fragment game con và Host?**
→ Qua `GamePlayViewModel` (shared) — inject bằng `viewModels(ownerProducer = { requireParentFragment() })`. Xem ví dụ ở [MyFragmentGameExample.kt:15](../gamedetails/MyFragmentGameExample.kt).

**Q: Face data đi tới game thế nào?**
→ `FaceAnalyzerCameraView` (đăng ký trong `handleSetupConfigCamera`) nhận kết quả mỗi frame → gọi:
  - `gamePlayViewModel.updateFaceData(result)` — cho Fragment game dùng qua shared VM.
  - `dataController?.updateDataFaceDetect(result)` — cho Custom View.
