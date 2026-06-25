# android-xr-genie-effect-sample

A sample Android XR application demonstrating a smooth, Dock-like Genie Effect animation and seamless space transitions using the Jetpack XR SDK (Compose for XR).

このリポジトリは、Jetpack XR SDK（Compose for XR）を利用して、MacのDockのような「ジーニーエフェクト（吸い込み・飛び出し）」アニメーションと、ホームスペース/フルスペースの双方向遷移を実装したサンプルプロジェクトです。

---

## Features (特徴)

- **Home $\leftrightarrow$ Full Space Transition (空間遷移)**
  - 2Dのホームスペースと、3Dのフルスペースをシームレスに行き来できます。
- **Genie Effect Animation (ジーニーエフェクト)**
  - 空間パネルが縮小しながら、下部オービター内にある「パネルを非表示/表示」のトグルボタンへ正確に吸い込まれるようにスライド・フェード消去します。
- **Dynamic Parameter Calculation (パラメータの動的計算)**
  - パネルとオービターの距離（`visualGap`）をどれだけ大きく離しても、クリッピング（枠外での途切れ）を起こさず、かつ自動的にボタンの座標を追従して吸い込まれるように、アニメーションパラメータを動的計算しています。
- **Translucent UI (透過UI)**
  - 空間UIに馴染むよう、メインパネルには透過率60%（アルファ値 0.4f）のモダンな半透明サーフェスを適用しています。

---

## Technical Highlights (技術的なポイント)

### 1. クリッピング問題の回避 (Avoiding Clipping)
通常、`SpatialPanel` の外側にコンテンツがはみ出すとシステムによってクリップ（切り取り）されてしまいます。
このプロジェクトでは、`SpatialPanel` 本体の高さを「コンテンツの高さ（`panelHeight`） ＋ 余白（`visualGap`）」で大きく確保し、その内部でスライドさせることで、離れた距離にあるオービターまで途切れずにアニメーションできるようにしています。

### 2. ボタン位置への正確な収束 (Targeting the Toggle Button)
アニメーションのスケール基準点（`TransformOrigin`）のY座標比率（`pivotY`）および垂直方向のスライド移動ピクセル数（`slideOffsetY`）を、以下の数式で動的に算出しています。

```kotlin
// スライド移動量の合計（ピクセル単位）
val slideOffsetY = with(density) {
    (visualGap + orbiterOffset + (buttonHeight / 2)).toPx().toInt()
}

// スケール縮小時の中央点Y座標の比率
val pivotY = (panelHeight + visualGap + orbiterOffset + (buttonHeight / 2)) / panelHeight

// スケール縮小時の中央点X座標の比率（オービター内トグルボタンの推定中心座標）
val pivotX = 0.375f
```

これによって、`visualGap` を `150.dp` から `300.dp` などへ広げても、追加の調整なしでボタンに吸い込まれる挙動が維持されます。

---

## Tech Stack (技術スタック)

- **Language:** Kotlin
- **SDK:** Jetpack XR SDK (`1.0.0-alpha17`)
- **Framework:** Compose for XR
- **Build System:** Gradle (AGP `8.9.1`, Gradle `8.11.1`)
- **OS Target:** Android SDK 36 (Vanilla Ice Cream / Android XR)
