# Extensions — Cẩm nang tra cứu theo Keyword

> Thư mục: `com.el.mybasekotlin.utils.extension`
> **Cách dùng:** `Ctrl+F` trong file này gõ keyword tiếng Việt / tiếng Anh / tên gần đúng → tìm ra hàm cần.
> Mỗi dòng trong các bảng có dạng: **keyword-gợi-nhớ → `HàmCần()` — mô tả — [file](file.kt)**.

---

## MỤC LỤC NHANH

| # | Nhóm | File | Keyword đặc trưng |
|---|------|------|-------------------|
| 1 | [Date & Time](#1-date--time) | [DateExt.kt](DateExt.kt) | date, time, timestamp, format, duration, ago, calendar |
| 2 | [String](#2-string) | [StringExt.kt](StringExt.kt), [MathExt.kt](MathExt.kt) | string, slug, hex, clipboard, encrypt, regex, numeric |
| 3 | [Text / TextView](#3-text--textview) | [TextExt.kt](TextExt.kt) | textview, span, gradient, font, html, typeface |
| 4 | [EditText](#4-edittext) | [EdittextExt.kt](EdittextExt.kt) | edittext, password, phone, watcher, textChanged |
| 5 | [View / UI](#5-view--ui) | [ViewExt.kt](ViewExt.kt), [Ui.kt](Ui.kt) | view, visible, gone, keyboard, snackbar, click, dp, px, fade |
| 6 | [Image / Photo](#6-image--photo) | [PhotoExt.kt](PhotoExt.kt) | image, glide, load, circle, avatar, radius |
| 7 | [Screen / Display](#7-screen--display) | [ScreenExt.kt](ScreenExt.kt) | screen, size, density, dpi |
| 8 | [Intent / Bundle / Navigation](#8-intent--bundle) | [IntentExt.kt](IntentExt.kt) | intent, bundle, extras, parcelable, openActivity |
| 9 | [Permission](#9-permission) | [PermissionExt.kt](PermissionExt.kt) | permission, request, grant, notification, rationale |
| 10 | [Flow / Coroutine](#10-flow--coroutine) | [FlowExt.kt](FlowExt.kt), [FlowExtension.kt](FlowExtension.kt) | flow, collect, combine, throttle, debounce, click |
| 11 | [Fragment / Lifecycle](#11-fragment--lifecycle) | [FragmentLifecycleExt.kt](FragmentLifecycleExt.kt) | fragment, lifecycle, launchAndRepeat, collectIn |
| 12 | [Internet / Network](#12-internet--network) | [InternetExt.kt](InternetExt.kt) | internet, connection, wifi, mobile |
| 13 | [URL](#13-url) | [UrlExt.kt](UrlExt.kt) | url, youtube, uri, param |
| 14 | [File / Asset](#14-file--asset) | [FileExtension.kt](FileExtension.kt) | file, asset, json, loadJson |
| 15 | [Data / JSON / Gson](#15-data--json--gson) | [DataExtension.kt](DataExtension.kt) | json, gson, parse, mapTo, requestBody |
| 16 | [Log / Debug / Type](#16-log--debug) | [LogExt.kt](LogExt.kt) | log, measureTime, isType |

---

## BẢNG TRA KEYWORD NHANH (A→Z)

> Bảng này là "cổng vào". Team gõ từ khóa gần đúng sẽ trúng hàm. Ví dụ cần `findFile` → gõ `file`, `asset`, `load` đều ra.

### A

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| ago, "trước", "thời gian trước", timeAgo | `String.toTimeAgo()`, `Long.convertToNotificationTimeStamp()` | 1 |
| alpha, opacity, mờ, "độ mờ" | `ImageView.changeOpacityTo(alpha)`, `TextView.changeOpacityTo(alpha)` | 6, 3 |
| ampm, "sáng chiều" | `String.convertHourToAmPm()`, `String.convertTimestampSecondsToAMPM()` | 1 |
| animation, fade, "hiệu ứng mờ" | `View.fadeIn(duration)`, `View.downToTopVisible()` | 5 |
| asset, "tài nguyên", loadFromAsset | `loadJSONFromAsset(fileName, context)` | 14 |
| awaitLayout, "đợi layout" | `View.awaitNextLayout()`, `View.doOnNextLayout{}` | 5 |

### B

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| base64, encode/decode byte | `convertByteToString(array)`, `convertStringToByte(str)`, `bytesToHex(bytes)` | 2 |
| bold, "in đậm" | `TextView.changeTextToVnBold()` | 3 |
| browser, "mở web" | `Context.openBrowser(url)`, `Context.openWebUrl(url)` | 5 |
| bundle, putExtras, parcelable | `Bundle.putExtrasBundle(...)`, `Bundle.parcelable<T>(key)`, `Bundle.parcelableArrayList<T>(key)`, `Bundle.get<T>(key)` | 8 |

### C

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| calendar, "lịch" | `calendarFormatFromString(time)` | 1 |
| checkInternet, hasInternet, wifi, "mạng" | `hasConnection(context)` | 12 |
| checkPermission, "kiểm tra quyền" | `checkPerNotice(app)`, `Fragment.requestPermission(...)`, `Activity.requestPermission(...)` | 9 |
| circle, circleCrop, avatar | `ImageView.loadCircleImageFromUrl()`, `ImageView.loadCircleImageFromUri()`, `ImageView.loadCircleImageFromDrawable()`, `ImageView.loadImageAvatarNotice()` | 6 |
| click, onClick, safeClick | `View.onClick{}`, `View.onLongClick{}`, `View.onClicked(): Flow<Unit>` | 5, 10 |
| clipboard, copy | `String.copyToClipboard(context)` | 2 |
| collect, collectIn, lifecycle | `Flow.collectIn(fragment)`, `Flow.collectInOwner(owner)`, `Flow.launchAndCollectIn(owner)`, `LifecycleOwner.execute(flow)` | 10, 11 |
| color, setColor, spanColor | `TextView.changeColorTo(color)`, `TextView.setSpannableColor(...)`, `Context.getSpannableColor(...)` | 3 |
| combine, "gộp flow", flow 6/7/8 | `combine6Flow(...)`, `combine7Flow(...)`, `combine8Flow(...)` | 10 |
| connection, "kết nối mạng" | `hasConnection(context)` | 12 |
| convertJson, parseJson, toObject | `parseJson<T>(json)`, `parseJsonList<T>(json)`, `Any.mapTo<T>()`, `objectToJson(any)` | 15 |
| countdown, duration, "đếm giây" | `Int.formatDuration()` | 1 |

### D

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| date, "ngày", format, toDate | `String.toDate(format)`, `Date.toString(format)`, `String.getStringDate(...)`, `String.formatDate(...)`, `convertTimeStampToDate(time)`, `convertTimestampSecondsToAMPMDate(input)` | 1 |
| dataclass, "sinh class từ json" | `String.generateDataClass(className)` | 15 |
| delay, throttle, "chống spam" | `Flow.throttle{}`, `Flow.throttleFlow(waitMs)`, `throttleFirst(...)` | 10 |
| density, dpi, screen | `Activity.getScreenDensity()` | 7 |
| dpToPx, pxToDp, "đổi dp sang px" | `Int.dpToPx()`, `Int.pxToDp()`, `String.dpToPx()`, `String.pxToDp()` | 5 |
| drawable, loadDrawable | `ImageView.loadImageFromDrawable(res)`, `ImageView.loadImageAvatarNotice(res)` | 6 |

### E

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| edittext, textChanged | `EditText.afterTextChanged{}`, `EditText.onTextChanged{}`, `PasswordTextWatcher` | 4 |
| encrypt, key | `generateEncryptionKey()` | 2 |
| extract, "tách chuỗi" | `extractSubstring(input)`, `extractAndRemoveSubstring(input)` | 2 |

### F

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| fade, "hiệu ứng mờ dần" | `View.fadeIn(duration)` | 5 |
| file, asset, loadJson, "đọc file" | `loadJSONFromAsset(fileName, context)` | 14 |
| flow, collect | `Flow.collectIn(fragment)`, `Flow.launchAndCollectIn(owner)`, `View.onClicked(): Flow<Unit>` | 10, 11 |
| focus, keyboard | `View.focusAndShowKeyboard()` | 5 |
| font, typeface, vietnam | `TextView.changeTextToVnBold()`, `TextView.changeTextToVNRegular()`, `CustomTypefaceSpan` | 3 |
| format, duration, pattern | `Int.formatDuration()`, `Long.toTimeStamp(format)`, `String.convertTimeToFormat(format)` | 1 |
| fragment, viewLifecycle | `Fragment.launchAndRepeatStarted{}`, `Flow.collectIn(fragment)`, `Fragment.requestPermission(...)` | 11, 9 |

### G

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| get, orBlank, "lấy mặc định" | `String?.getOrBlank()`, `String?.get()` | 2 |
| getScreen, screenSize, "kích thước màn hình" | `getScreenSize(context)` | 7 |
| getSetting | `Context.getSetting(id)` | 8 |
| glide, loadImage | `ImageView.loadImageFromUrl(url, err)`, `ImageView.loadImageFromDrawable(res)`, `ImageView.loadImageWithRadius(url, r)` | 6 |
| gone, hide | `View.gone()`, `View.invisible()`, `View.visibleIf(condition)`, `View.goneDelay(handler, delay)` | 5 |
| gradient, text gradient | `TextView.setTextGradient(start, end, angle)`, `AppCompatTextView.setGradientText(...)` | 3, 5 |
| gson, json | `parseJson<T>(json)`, `objectToJson(any)`, `Any.mapTo<T>()`, `String.mapErrorMessage()` | 15 |

### H

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| hex, byte to hex | `bytesToHex(bytes)` | 2 |
| html, fromHtml | `TextView.htmlLoadImg(htmlText)` (đang comment) | 3 |
| hour, "giờ", AM/PM | `String.formatDateToHours()`, `String.convertHourToAmPm()`, `convertTimeStampToHour(time)`, `convertTimestampSecondsToAMPMOnlyTime(input)` | 1 |

### I

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| image, glide, load | (xem Nhóm 6) | 6 |
| intent, openActivity, putExtra | `Context.openActivity<T>(vararg pairs)`, `Intent.putExtras(...)`, `Intent.set(key, value)`, `Intent.get<T>(key)`, `Intent.parcelable<T>(key)`, `Intent.parcelableArrayList<T>(key)` | 8 |
| invisible, "ẩn view" | `View.invisible()`, `View.gone()` | 5 |
| isNumeric, "kiểm tra số" | `String.isNumericInt()`, `String.isNumericLong()`, `String.areAllNumbers(size)`, `isStringAnInt(str)`, `canConvertToLong(input)` | 2, 1 |
| isType, isInstance | `Any.isType<T>()` | 16 |

### J

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| json, parse, gson | `parseJson<T>(json)`, `parseJsonList<T>(json)`, `objectToJson(t)`, `Any.mapTo<T>()`, `loadJSONFromAsset(name, ctx)`, `String.generateDataClass(className)` | 15, 14 |

### K

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| keyboard, "bàn phím" | `View.showKeyboard()`, `View.hideKeyboard()`, `View.focusAndShowKeyboard()`, `View.hideKeyboardClickOutSide()`, `View.isKeyboardShown`, `addKeyboardVisibilityListener(root): Flow<Boolean>` | 5 |

### L

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| last, "ký tự cuối", "từ cuối" | `lastChar(input)`, `getLastWordWithoutPunctuation(input)` | 2 |
| launch, lifecycle | `Fragment.launchAndRepeatStarted{...}`, `FragmentActivity.launchAndRepeatStarted{...}`, `Flow.launchAndCollectIn(owner)`, `Flow.launchAndCollectInActivity(...)`, `Flow.launchAndCollectEachIn(owner)` | 11, 10 |
| load, image, url | (xem Nhóm 6) | 6 |
| longClick | `View.onLongClick{}` | 5 |

### M

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| mapTo, object, convert | `Any.mapTo<T>()`, `Flow.mapTo(value)` | 15, 10 |
| measureTime, "đo thời gian" | `measureTimeMillis{...}` | 16 |
| mobile, wifi | `hasConnection(context)` | 12 |

### N

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| navigate, openActivity | `Context.openActivity<T>(pairs)` | 8 |
| notification, notificationAgo | `Long.convertToNotificationTimeStamp()`, `checkPerNotice(app)` | 1, 9 |
| number, phone, valid | `isValidNumber(data)`, `String.isNumericInt()`, `String.isNumericLong()` | 4, 2 |

### O

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| onClick, safeClick | `View.onClick{}`, `View.onLongClick{}`, `View.onClicked(): Flow<Unit>` | 5, 10 |
| opacity, alpha | `ImageView.changeOpacityTo(a)`, `TextView.changeOpacityTo(a)` | 6, 3 |
| openActivity, openBrowser, openUrl | `Context.openActivity<T>(pairs)`, `Context.openBrowser(url)`, `Context.openWebUrl(url)` | 8, 5 |

### P

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| parcelable, bundle, intent | `Intent.parcelable<T>(key)`, `Bundle.parcelable<T>(key)`, `Intent.parcelableArrayList<T>(key)`, `Bundle.parcelableArrayList<T>(key)` | 8 |
| parse, json | `parseJson<T>(json)`, `parseJsonList<T>(json)` | 15 |
| password, hide/show | `EditText.showPassWord()`, `EditText.hidePassWord()`, `PasswordTextWatcher` | 4 |
| permission, request | `Fragment.requestPermission(...)`, `ComponentActivity.requestPermission(...)`, `Fragment.requestPermissionsList(...)`, `ComponentActivity.requestPermissionsList(...)`, `requestPermission(ctx, perm, cb)`, `checkPerNotice(app)` | 9 |
| phone, validateNumber | `isValidNumber(data)` | 4 |
| px, dp | `Int.dpToPx()`, `Int.pxToDp()`, `String.dpToPx()`, `String.pxToDp()` | 5 |

### Q

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| queryParam, url param | `String.addParam(params: Map)` | 13 |

### R

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| radioGroup, position | `RadioGroup.getCheckedRadioButtonPosition()` | 5 |
| radius, rounded image | `ImageView.loadImageWithRadius(url, r)` | 6 |
| recyclerView, lastVisible | `RecyclerView.getLastVisibleItemPosition{...}`, `RecyclerView.hasItems` | 5 |
| regex, removeSpecial | `String.removeAllSpace()`, `String.removeSpecialCharacters()`, `String.removeMatchingSubstring(regex)`, `processStringX(input)` | 2 |
| requestBody, multipart | `String.toRequestBodyPart()` | 15 |
| requestPermission | (xem Nhóm 9) | 9 |

### S

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| safeClick | `View.onClick{}` (param `safe = true`) | 5 |
| screen, size, density | `getScreenSize(context)`, `Activity.getScreenDensity()` | 7 |
| scroll, recyclerView | `RecyclerView.getLastVisibleItemPosition{...}` | 5 |
| set, MutableStateFlow | `MutableStateFlow.set(value, default)` | 10 |
| setting, notificationSetting | `Context.getSetting(id)` | 8 |
| slug | `String.toSlug()` | 2 |
| snackbar | `View.showSnackBar(msg)`, `Context.showSnackBar(view, msg)`, `Context.showSnackBarCustom(layoutId, root)` | 5 |
| span, color, margin | `TextView.setSpannableColor(...)`, `TextView.setSpannableHTMLStyle(...)`, `TextView.setSpannableColorAndMargin(...)`, `Context.getSpannableColor(...)`, `CustomTypefaceSpan` | 3 |
| string, slug, clipboard | (xem Nhóm 2) | 2 |

### T

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| text, textView, color | (xem Nhóm 3) | 3 |
| textChanged, watcher | `EditText.afterTextChanged{}`, `EditText.onTextChanged{}` | 4 |
| throttle, debounce | `Flow.throttle{}`, `Flow.throttleFlow(ms)`, `throttleFirst(skipMs, scope, fn)` | 10 |
| timeAgo, "n giờ trước" | `String.toTimeAgo()`, `Long.convertToNotificationTimeStamp()` | 1 |
| timestamp, long to date | `Long.toTimeStamp(format)`, `convertTimeStampToDate(time)`, `convertTimeStampToHour(time)`, `String.convertTimestampSecondsToAMPM()`, `convertTimestampSecondsToAMPMOnlyTime(input)`, `convertTimestampSecondsToAMPMDate(input)`, `String.convertTimeToFormat(format)` | 1 |
| typeface, font, Vietnamese | `TextView.changeTextToVnBold()`, `TextView.changeTextToVNRegular()`, `CustomTypefaceSpan` | 3 |

### U

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| uri, youtube, vcc | `String.isYouTubeUrl()`, `String.isVccUrl()`, `String.addParam(params)` | 13 |

### V

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| valid, phone, password | `isValidNumber(data)` | 4 |
| view, visible, gone | `View.visible()`, `View.gone()`, `View.invisible()`, `View.visibleIf(cond)` | 5 |

### W / Y / Z

| Keyword | Hàm | Nhóm |
|---------|-----|------|
| watcher, textChanged | `PasswordTextWatcher`, `EditText.afterTextChanged{}`, `EditText.onTextChanged{}` | 4 |
| youtube url | `String.isYouTubeUrl()` | 13 |
| zonedDateTime, schedule | `String.convertScheduleDateTime()` | 1 |

---

## 1. Date & Time
> File: [DateExt.kt](DateExt.kt)
> Keywords: `date`, `time`, `hour`, `timestamp`, `format`, `ago`, `amPm`, `calendar`, `duration`

| Hàm | Mô tả | Keyword |
|-----|-------|---------|
| `String.getStringDate(initialFormat, requiredFormat, locale)` | Parse ngày theo format A rồi format lại sang format B. | parse date, format |
| `String.toDate(format, locale)` | Chuỗi → `Date` (UTC). | parse, toDate |
| `String.convertTimeTest(locale): Triple<day, month, dayOfWeek>` | Từ epoch seconds → `(dd, THÁNG, T2/T3/…)`. Hỗ trợ VN. | weekday, day of week |
| `String.formatDate(initFormat, endFormat)` | Convert text date format X → Y. | format |
| `Date.toString(format, locale)` | Date → chuỗi theo pattern. | format |
| `Long.toTimeStamp(format, locale)` | Epoch seconds → chuỗi formatted. | timestamp |
| `String.formatDateToHours()` | `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'` → `HH:mm`. | hour |
| `calendarFormatFromString(time)` | ISO string → `Calendar` (GMT+7). | calendar |
| `convertTimeStampToDate(timeMs, outPattern)` | ms → ngày. | timestamp, date |
| `convertTimeStampToHour(timeMs)` | ms → `h:mm a`. | timestamp, hour |
| `String.convertHourToAmPm(fromPattern, toPattern, isLowercase)` | `HH:mm:ss` → `hh:mm a`. | am pm |
| `String.convertTimestampSecondsToAMPM()` | Epoch seconds → `SA/CH - dd/MM/yyyy`. | am pm, Vietnamese |
| `convertTimestampSecondsToAMPMOnlyTime(input)` | Giống trên nhưng chỉ giờ. | am pm |
| `convertTimestampSecondsToAMPMDate(input)` | Giống trên nhưng chỉ ngày. | am pm, date |
| `canConvertToLong(input)` | Check chuỗi có thể thành `Long`. | isNumeric |
| `String.convertTimeToFormat(format)` | Epoch seconds → bất kỳ format nào. | timestamp |
| `Long.convertToNotificationTimeStamp()` | Hiển thị "n giây/phút/giờ/ngày/tuần/tháng/năm trước". | notification, ago |
| `Int.formatDuration()` | Giây → `HH:MM:SS` / `MM:SS`. | duration, countdown |
| `String.toTimeAgo()` | ISO → "n giờ trước". | ago |
| `String.convertScheduleDateTime()` | `yyyy-MM-dd` → `ZonedDateTime`. | schedule, zoned |

---

## 2. String
> Files: [StringExt.kt](StringExt.kt), [MathExt.kt](MathExt.kt)
> Keywords: `string`, `slug`, `hex`, `byte`, `clipboard`, `encrypt`, `regex`, `last char`, `numeric`

| Hàm | Mô tả | Keyword |
|-----|-------|---------|
| `String.toSlug()` | "Hello world!" → "hello-world". | slug, seo |
| `generateEncryptionKey(): ByteArray` | Random 16 bytes. | key, encrypt |
| `String?.getOrBlank()` | Null-safe default rỗng. | default, null |
| `String?.get()` | Helper trả về rỗng khi có data (đảo ngược — coi chừng). | default |
| `bytesToHex(bytes)` | ByteArray → hex string. | hex |
| `convertByteToString(array)` | ByteArray → Base64 string. | base64 |
| `convertStringToByte(string)` | Base64 string → ByteArray. | base64 |
| `String.copyToClipboard(context)` | Copy + show toast "Đã sao chép". | clipboard |
| `String.removeAllSpace()` | Xóa hết khoảng trắng. | regex |
| `String.removeSpecialCharacters()` | Xóa `x` và `*`. | regex |
| `String.removeMatchingSubstring(regex)` | Remove theo regex. | regex |
| `extractSubstring(input)` | Lấy substring trước delimiter đầu tiên (space / comma). | split |
| `extractAndRemoveSubstring(input): ExtractionResult` | Trả về `(extracted, remaining)`. | split |
| `lastChar(input)` | Ký tự cuối. | last |
| `getLastWordWithoutPunctuation(input)` | Từ cuối, bỏ `. , `. | last word |
| `processStringX(input)` | Chuẩn hóa "x" trong chuỗi số (e.g. "2x" → "2 x"). | regex |
| `isStringAnInt(str)` | Check có phải Int. | numeric |
| `String.isNumericInt()` / `isNumericLong()` | Check Int / Long. | numeric |
| `String.areAllNumbers(sizeLimit)` | Tất cả phần split by `,` có phải số & trong độ dài cho phép. | validate, csv |

---

## 3. Text / TextView
> File: [TextExt.kt](TextExt.kt)
> Keywords: `textview`, `font`, `span`, `color`, `gradient`, `typeface`, `html`

| Hàm | Mô tả | Keyword |
|-----|-------|---------|
| `TextView.changeTextToVnBold()` | Đổi font sang `be_vietnam_pro_bold`. | font, bold |
| `TextView.changeTextToVNRegular()` | Đổi font sang regular. | font |
| `TextView.changeColorTo(colorRes)` | Đổi màu text theo resource id. | color |
| `TextView.changeOpacityTo(alpha)` | Đổi `alpha`. | opacity |
| `TextView.setSpannableColor(text, color, start, end)` | Span màu cho đoạn text. | span, color |
| `TextView.setSpannableHTMLStyle(spanned, color, s, e)` | Span trên đối tượng `Spanned`. | span, html |
| `TextView.setSpannableColorAndMargin(...)` | Span màu + margin dòng đầu. | span, margin |
| `Context.getSpannableColor(...)` | Trả về `SpannableStringBuilder` màu. | span |
| `TextView.setTextGradient(start, end, angle)` | Gradient text. | gradient |
| `TextView.htmlLoadImg(html, w, h, ...)` | (đang comment) — load HTML có ảnh qua ImageLoader. | html, image |
| `class CustomTypefaceSpan(family, typeface)` | Span font tùy chỉnh dùng trong `SpannableString`. | typeface, font, span |

---

## 4. EditText
> File: [EdittextExt.kt](EdittextExt.kt)
> Keywords: `edittext`, `password`, `phone`, `watcher`

| Hàm | Mô tả | Keyword |
|-----|-------|---------|
| `EditText.showPassWord()` / `hidePassWord()` | Toggle kiểu password. | password |
| `EditText.afterTextChanged{...}` | Shortcut `afterTextChanged` của `TextWatcher`. | watcher |
| `EditText.onTextChanged{...}` | Shortcut `onTextChanged`. | watcher |
| `PasswordTextWatcher(editText, cb)` | TextWatcher trim password realtime, tránh leak bằng `WeakReference`. | password, watcher |
| `isValidNumber(data)` | Check số điện thoại VN theo prefix trong `headerPhoneNumber`. | phone, validate |
| `data class ValidData(isValid, code, msg)` | Model kết quả validate. | validate |

---

## 5. View / UI (dp, px, animation, keyboard, snackbar)
> Files: [ViewExt.kt](ViewExt.kt), [Ui.kt](Ui.kt)
> Keywords: `view`, `visible`, `gone`, `keyboard`, `snackbar`, `click`, `dp`, `px`, `animation`, `gradient`, `recyclerView`

### 5.1. Visibility & animation

| Hàm | Mô tả |
|-----|------|
| `View.visible()` / `gone()` / `invisible()` | Đổi visibility nếu khác trạng thái hiện tại. |
| `View.visibleIf(condition, gone = true)` | True → visible; false → gone/invisible. |
| `View.goneDelay(handler, delay)` | Gone sau delay ms. |
| `View.fadeIn(duration)` | AlphaAnimation 0→1. |
| `View.downToTopVisible(duration)` | Trượt từ dưới lên visible. |

### 5.2. Keyboard

| Hàm | Mô tả |
|-----|------|
| `View.showKeyboard()` / `hideKeyboard()` | Hiện/ẩn bàn phím. |
| `View.focusAndShowKeyboard()` | Request focus + show keyboard (kể cả chưa có window focus). |
| `View.hideKeyboardClickOutSide()` | Click ngoài EditText → ẩn keyboard (áp cho children của ViewGroup). |
| `View.isKeyboardShown: Boolean` | Prop check bàn phím đang hiện. |
| `addKeyboardVisibilityListener(rootLayout): Flow<Boolean>` | Flow emit true/false theo keyboard visibility. |

### 5.3. Click / Snackbar / Browser

| Hàm | Mô tả |
|-----|------|
| `View.onClick(safe = true){...}` | SafeClick (chống double-click). |
| `View.onLongClick{...}` | Long click callback. |
| `View.showSnackBar(msg, duration, anchor)` | Snackbar 3 overload (String / @StringRes / @IdRes anchor). |
| `Context.showSnackBar(view, msg, duration)` | Từ Context. |
| `Context.showSnackBarCustom(layoutId, root)` | Snackbar custom layout XML. |
| `Context.openBrowser(url)` / `openWebUrl(url)` | Mở URL qua Intent (web view chỉ khác ở try-catch). |

### 5.4. Layout / RecyclerView / Inflate

| Hàm | Mô tả |
|-----|------|
| `ViewGroup.inflate<T>(resId)` | Inflate with generic View type. |
| `ViewGroup.inflater: LayoutInflater` | Property lấy inflater. |
| `View.doOnNextLayout{...}` | One-shot layout callback. |
| `View.awaitNextLayout()` | `suspend` — đợi layout xong. |
| `RecyclerView.hasItems: Boolean` | Check có item. |
| `RecyclerView.getLastVisibleItemPosition{pos -> }` | Listen scroll, trả về last visible item. |
| `RadioGroup.getCheckedRadioButtonPosition()` | Trả về index radio đang chọn. |
| `AppCompatTextView.setGradientText(color1, color2, value)` | Gradient text (2 màu hex). |

### 5.5. dp / px ([Ui.kt](Ui.kt))

| Hàm | Mô tả |
|-----|------|
| `Int.dpToPx()` / `Int.pxToDp()` | Int → Int. |
| `String.dpToPx()` / `String.pxToDp()` | String → Int. |

---

## 6. Image / Photo (Glide)
> File: [PhotoExt.kt](PhotoExt.kt)
> Keywords: `image`, `glide`, `load`, `avatar`, `circle`, `radius`

| Hàm | Mô tả |
|-----|------|
| `ImageView.loadImageFromUrl(url, errorRes?)` | Load URL. |
| `ImageView.loadImageFromDrawable(res)` | Load local drawable. |
| `ImageView.loadCircleImageFromUrl(url, placeholder)` | Hình tròn. |
| `ImageView.loadCircleImageFromUri(uri, placeholder)` | Tròn từ `Uri`. |
| `ImageView.loadCircleImageFromDrawable(res)` | Tròn từ drawable. |
| `ImageView.loadImageAvatarNotice(res)` | Tròn drawable (alias avatar). |
| `ImageView.loadImageWithRadius(url, radiusPx)` | CenterCrop + rounded corners. |
| `ImageView.changeOpacityTo(alpha)` | Đổi opacity. |

---

## 7. Screen / Display
> File: [ScreenExt.kt](ScreenExt.kt)
> Keywords: `screen`, `size`, `density`, `dpi`

| Hàm | Mô tả |
|-----|------|
| `Activity.getScreenDensity()` | Log density type (ldpi/mdpi/…/xxxhdpi) + sw dp. |
| `getScreenSize(context): Pair<width, height>` | Trả về kích thước màn hình (trừ 60dp cho nav bar). |

---

## 8. Intent / Bundle
> File: [IntentExt.kt](IntentExt.kt)
> Keywords: `intent`, `bundle`, `putExtras`, `parcelable`, `openActivity`, `setting`

| Hàm | Mô tả |
|-----|------|
| `Context.openActivity<T>(vararg Pair)` | Mở Activity + putExtras dưới dạng Pair. |
| `Intent.putExtras(vararg Pair)` | Put nhiều extra bằng Pair (auto switch type). |
| `Intent.set(key, value)` | Operator overload: `intent["key"] = value`. |
| `Intent.get<T>(key, default)` | Operator `intent.get<String>("key")`. |
| `Intent.parcelable<T>(key)` | Lấy parcelable (API 33 compat). |
| `Intent.parcelableArrayList<T>(key)` | Lấy list parcelable. |
| `Bundle.get<T>(key, default)` | Giống Intent. |
| `Bundle.parcelable<T>(key)` / `parcelableArrayList<T>(key)` | Bundle parcelable compat. |
| `Bundle.putExtrasBundle(vararg Pair)` | Put nhiều vào Bundle. |
| `Context.getSetting(id)` | Map id → Settings action (e.g. `NOTIFICATION_SETTING`). |

---

## 9. Permission
> File: [PermissionExt.kt](PermissionExt.kt)
> Keywords: `permission`, `request`, `grant`, `rationale`, `notification`

| Hàm | Mô tả |
|-----|------|
| `checkPerNotice(app): Boolean` | Có đang bị tắt notification không. |
| `requestPermission(activity, permission, callback)` | Flow low-level cho 1 quyền (mở setting nếu denied permanently). |
| `Fragment.requestPermission(permission, rationaleMsg, onGranted, onDenied, onNeverAskAgain)` | High-level cho Fragment: hiện dialog rationale → request. |
| `ComponentActivity.requestPermission(...)` | Tương tự cho Activity. |
| `Fragment.requestPermissionsList(permissions, msg, onAllGranted, onPartialGranted, onNeverAskAgain)` | Request nhiều quyền 1 lần. |
| `ComponentActivity.requestPermissionsList(...)` | Tương tự Activity. |

---

## 10. Flow / Coroutine
> Files: [FlowExt.kt](FlowExt.kt), [FlowExtension.kt](FlowExtension.kt)
> Keywords: `flow`, `collect`, `combine`, `throttle`, `click`, `mainThread`

### 10.1. Operator

| Hàm | Mô tả |
|-----|------|
| `Flow.flatMapFirst{...}` | Map drop-while-busy (bỏ emission khi đang xử lý). |
| `Flow.flattenFirst()` | Flatten higher-order flow kiểu first. |
| `Flow.mapTo(value)` | Map mọi emission thành constant. |
| `Flow.asInitialValueFlow{ ... }` | Gắn initial value emit ngay khi collect. |
| `class InitialValueFlow<T>` | Có `.skipInitialValue()`. |
| `Flow.throttle{ timeoutMillis }` | Throttle theo hàm tính delay. |
| `Flow.throttleFlow(waitMillis)` | Throttle fixed window. |
| `throttleFirst(skipMs, scope, fn)` | Gọi `fn` lần đầu, bỏ trong `skipMs` tiếp theo. |
| `MutableStateFlow.set(value, default)` | Emit value rồi reset về default (hữu dụng cho one-shot event). |

### 10.2. Combine nhiều flow

| Hàm | Mô tả |
|-----|------|
| `combine6Flow(f1..f6, transform)` | Combine 6 flow. |
| `combine7Flow(f1..f7, transform)` | Combine 7. |
| `combine8Flow(f1..f8, transform)` | Combine 8. |

### 10.3. Click / Main thread

| Hàm | Mô tả |
|-----|------|
| `checkMainThread()` | Assert đang ở main thread. |
| `View.onClicked(): Flow<Unit>` | CallbackFlow từ click event. |

### 10.4. Collect theo lifecycle ([FlowExtension.kt](FlowExtension.kt))

| Hàm | Mô tả |
|-----|------|
| `Flow.launchAndCollectIn(owner, minState, action)` | Collect `repeatOnLifecycle`. |
| `Flow.launchAndCollectInActivity(scope, minState, action)` | Dành cho Activity scope. |
| `Flow.launchAndCollectEachIn(owner, minState, action)` | Dùng `onEach`. |
| `LifecycleOwner.execute(flow, minState, action)` | Helper ngắn gọn. |

---

## 11. Fragment / Lifecycle
> File: [FragmentLifecycleExt.kt](FragmentLifecycleExt.kt)
> Keywords: `fragment`, `lifecycle`, `launch`, `collect`, `repeat`

| Hàm | Mô tả |
|-----|------|
| `Fragment.launchAndRepeatStarted(vararg block, doAfterLaunch)` | Launch nhiều suspend block trong `repeatOnLifecycle(STARTED)` của `viewLifecycleOwner`. |
| `FragmentActivity.launchAndRepeatStarted(vararg block, minState)` | Tương tự cho Activity. |
| `Flow.collectInOwner(owner, minState, action)` | Collect bằng `LifecycleOwner`. |
| `Flow.collectIn(fragment, minState, action)` | Collect trong Fragment (dùng `viewLifecycleOwner`). **Hàm phổ biến nhất — dùng xuyên suốt codebase.** |

---

## 12. Internet / Network
> File: [InternetExt.kt](InternetExt.kt)
> Keywords: `internet`, `connection`, `wifi`, `mobile`

| Hàm | Mô tả |
|-----|------|
| `hasConnection(context?): Boolean` | Check có wifi / mobile / bất kỳ kết nối nào. |

---

## 13. URL
> File: [UrlExt.kt](UrlExt.kt)
> Keywords: `url`, `youtube`, `uri`, `param`

| Hàm | Mô tả |
|-----|------|
| `String.isYouTubeUrl()` | Regex check YouTube URL. |
| `String.isVccUrl()` | Regex check VCC internal URL. |
| `String.addParam(params: Map)` | Append query params vào URL. |

---

## 14. File / Asset
> File: [FileExtension.kt](FileExtension.kt)
> Keywords: `file`, `asset`, `json`, `loadJson`, `findFile`, `getFile`

| Hàm | Mô tả |
|-----|------|
| `loadJSONFromAsset(fileName, context): String?` | Đọc file trong `assets/` ra chuỗi UTF‑8. |

> 📌 Đây là file **giúp tìm khi bạn search**: `file`, `asset`, `json`, `findFile`, `getFile`, `readFile`. Mục đích chính hiện tại: load JSON từ `assets/`.

---

## 15. Data / JSON / Gson
> File: [DataExtension.kt](DataExtension.kt)
> Keywords: `json`, `gson`, `parse`, `mapTo`, `requestBody`, `generateClass`

| Hàm | Mô tả |
|-----|------|
| `String.toRequestBodyPart(): RequestBody` | Tạo `RequestBody` multipart từ String. |
| `Any.mapTo<T>(): T` | Object A → Object B qua JSON (Gson). |
| `String.mapErrorMessage(): BaseError?` | Parse chuỗi lỗi backend → `BaseError`. |
| `objectToJson(t): String` | Any → JSON string. |
| `parseJson<T>(json): T` | JSON string → T (reified). |
| `parseJsonList<T>(json): List<T>` | JSON → List<T>. |
| `String.generateDataClass(className)` | Sinh code `data class` kèm `@SerializedName` từ JSON mẫu → print ra Logcat. Tool dev-only. |

---

## 16. Log / Debug
> File: [LogExt.kt](LogExt.kt)
> Keywords: `log`, `time`, `measure`, `isType`

| Hàm | Mô tả |
|-----|------|
| `measureTimeMillis{...}` | Đo thời gian block chạy (ms). |
| `Any.isType<T>()` | Reified `is T`. |

---

## 17. Quy ước dùng extension

1. **Import theo package**: hầu hết package là `com.el.mybasekotlin.utils.extension` — **ngoại lệ**:
   - `DateExt.kt` → `com.vcc.ticket.utils.extension` (legacy, không đổi).
   - `InternetExt.kt` → `vn.chayluoi.stream.app.utils.extension` (legacy).
   - `UrlExt.kt` → `com.vcc.ticket.utils.extension` (legacy).
2. **Trước khi viết hàm mới**: search trong file này bằng keyword. Nếu đã có — dùng lại.
3. **Khi thêm hàm mới**: thêm vào file có nhóm tương ứng, rồi **cập nhật bảng A→Z** trong file này (ít nhất 1 dòng).
4. **Nhóm hàm mới hoàn toàn**: tạo file `XxxExt.kt` riêng, thêm section mới & bổ sung mục `MỤC LỤC NHANH`.
5. Hàm đã bị comment-out trong code (ví dụ `htmlLoadImg`, `isValidPassword`) thì để nguyên, không xóa — là "code dự phòng" team đang cân nhắc.

---

## 18. FAQ nhanh

**Q: Tôi muốn collect Flow trong Fragment, dùng hàm nào?**
→ `flow.collectIn(this) { ... }` — đây là hàm được dùng xuyên suốt codebase (xem nhóm 11).

**Q: Muốn parse ngày `2025-01-15T08:30:00.000Z` thành "30 phút trước"?**
→ `"2025-01-15T08:30:00.000Z".toTimeAgo()`.

**Q: Truyền Parcelable qua Intent / Bundle nhưng cảnh báo deprecation (API 33+)?**
→ Dùng `intent.parcelable<MyModel>("KEY")` hoặc `bundle.parcelable<MyModel>("KEY")` — đã xử lý SDK_INT trong extension.

**Q: Check mạng offline/online?**
→ `hasConnection(context)`.

**Q: Load ảnh tròn có placeholder?**
→ `imgView.loadCircleImageFromUrl(url, R.drawable.placeholder)`.

**Q: Chặn user spam click button?**
→ `btn.onClick(safe = true) { ... }` (default `safe = true`). Hoặc với Flow: `btn.onClicked().throttleFlow(500)`.

**Q: Đổi dp sang px?**
→ `16.dpToPx()`.
