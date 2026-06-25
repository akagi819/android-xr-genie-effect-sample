# android-xr-genie-effect-sample

![Genie Effect Demo](images/demo.gif)

A sample Android XR application demonstrating a smooth, Dock-like Genie Effect animation and seamless space transitions using the Jetpack XR SDK (Compose for XR).

---

## Features

- **Home $\leftrightarrow$ Full Space Transition**: Seamlessly transition between the 2D Home Space and the immersive 3D Full Space.
- **Dock-like Genie Effect Animation**: The spatial panel animates smoothly, shrinking and sliding down directly into the toggle button on the Orbiter, mimicking the classic macOS Dock genie effect.
- **Dynamic Parameter Calculation**: Animation offsets and scale pivot points (`TransformOrigin`) are dynamically calculated based on the `visualGap`, ensuring the animation remains perfectly aligned even if the distance between the panel and the orbiter is modified.
- **Translucent UI**: The spatial panel features a custom translucent surface (60% opacity) for a sleek, modern XR appearance.

## Technical Highlights

### 1. Avoiding Clipping
By default, the system clips any content that draws outside the boundaries of a `SpatialPanel`. To allow the panel to animate all the way down to the orbiter without getting clipped, this project extends the actual `SpatialPanel` height by adding the `visualGap` distance (`panelHeight + visualGap` = 600dp + 300dp = 900dp) and aligns the internal content to the top.

### 2. Precise Button Targeting
The vertical slide offset and the scale pivot Y (`TransformOrigin.pivotY`) are dynamically calculated using the following formulas:

```kotlin
// Total slide offset in pixels from the bottom of the active panel to the center of the button
val slideOffsetY = with(density) {
    (visualGap + orbiterOffset + (buttonHeight / 2)).toPx().toInt()
}

// Scale pivot Y ratio relative to the panel height
val pivotY = (panelHeight + visualGap + orbiterOffset + (buttonHeight / 2)) / panelHeight

// Scale pivot X ratio targeting the toggle button center (approx. 300dp from the left of the 800dp width panel)
val pivotX = 0.375f
```

## Tech Stack

- **Language:** Kotlin
- **SDK:** Jetpack XR SDK (`1.0.0-alpha17`)
- **Framework:** Compose for XR
- **Build System:** Gradle (AGP `8.9.1`, Gradle `8.11.1`)
- **OS Target:** Android SDK 36 (Android XR)

---

# android-xr-genie-effect-sample (日本語)

![Genie Effect Demo](images/demo.gif)

Jetpack XR SDK（Compose for XR）を利用して、MacのDockのような「ジーニーエフェクト（吸い込み・飛び出し）」アニメーションと、ホームスペース/フルスペースの双方向遷移を実装したサンプルプロジェクトです。

## 特徴

- **ホームスペース $\leftrightarrow$ フルスペースの空間遷移**
  - 2Dのホームスペースと、3Dのフルスペースをシームレスに行き来できます。
- **Mac of Dock風ジーニーエフェクト**
  - 空間パネルが縮小しながら、下部オービター内にある「パネルを非表示/表示」のトグルボタンへ正確に吸い込まれるようにスライド・フェード消去します。
- **パラメータの動的計算**
  - パネルとオービターの距離（`visualGap`）をどれだけ大きく離しても、クリッピング（枠外での途切れ）を起こさず、かつ自動的にボタンの座標を追従して吸い込まれるように、アニメーションパラメータを動的計算しています。
- **半透明UIの適用**
  - 空間UIに馴染むよう、メインパネルには透過率60%（アルファ値 0.4f）のモダンな半透明サーフェスを適用しています。

## 技術的なポイント

### 1. クリッピング問題の回避
通常、`SpatialPanel` の外側にコンテンツがはみ出すとシステムによってクリップ（切り取り）されてしまいます。
このプロジェクトでは、`SpatialPanel` 本体の高さを「コンテンツの高さ（`panelHeight`） ＋ 余白（`visualGap`）」で大きく確保し、その内部でスライドさせることで、離れた距離にあるオービターまで途切れずにアニメーションできるようにしています。

### 2. ボタン位置への正確な収束
アニメーションのスケール基準点（`TransformOrigin`）のY座標比率（`pivotY`）および垂直方向のスライド移動ピクセル数（`slideOffsetY`）を、以下の数式で動的に算出しています。

```kotlin
// スライド移動量の合計（ピクセル単位）
val slideOffsetY = with(density) {
    (visualGap + orbiterOffset + (buttonHeight / 2)).toPx().toInt()
}

// スケール縮小時の中央点Y座標の比率
val pivotY = (panelHeight + visualGap + orbiterOffset + (buttonHeight / 2)) / panelHeight

// スケール縮小時の中央点X座標 of 比率（オービター内トグルボタンの推定中心座標）
val pivotX = 0.375f
```

これによって、`visualGap` を `150.dp` から `300.dp` などへ広げても、追加の調整なしでボタンに吸い込まれる挙動が維持されます。

## 技術スタック

- **開発言語:** Kotlin
- **使用SDK:** Jetpack XR SDK (`1.0.0-alpha17`)
- **UIフレームワーク:** Compose for XR
- **ビルドシステム:** Gradle (AGP `8.9.1`, Gradle `8.11.1`)
- **対象OS:** Android SDK 36 (Android XR)
