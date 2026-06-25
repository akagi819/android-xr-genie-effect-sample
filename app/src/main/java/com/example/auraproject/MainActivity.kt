package com.example.auraproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.spatial.ContentEdge
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.width
import androidx.xr.runtime.Session
import androidx.xr.scenecore.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val session: Session? = LocalSession.current
                    val spatialCapabilities = LocalSpatialCapabilities.current
                    
                    // パネルの表示状態を管理するState
                    var isPanelVisible by remember { mutableStateOf(true) }

                    if (spatialCapabilities.isSpatialUiEnabled) {
                        // フルスペース（空間UIが有効な状態）
                        Subspace {
                            // パネルとオービターの見た目の距離（余白）を設定
                            val panelWidth = 800.dp
                            val panelHeight = 600.dp
                            val visualGap = 300.dp // 距離をさらに大きく離す (300dp)
                            val orbiterOffset = 16.dp
                            val buttonHeight = 48.dp // 一般的なマテリアル3ボタンの高さ

                            val density = LocalDensity.current
                            // スライド移動量の合計（ピクセル単位）
                            // 600dp の下端（アニメーション要素の下端）から、ボタンの中心までの距離。
                            // アニメーション要素は Box(Modifier.fillMaxSize()) の中で height(600.dp) で Align(TopCenter) されている。
                            // そこからさらに visualGap + orbiterOffset + buttonHeight/2 だけ離れた位置にボタンの中心がある。
                            val slideOffsetY = with(density) {
                                (visualGap + orbiterOffset + (buttonHeight / 2)).toPx().toInt()
                            }

                            // スケール時のピボットY（TransformOrigin.pivotY）
                            // パネルの高さ (600dp) に対する、吸い込み先であるボタンの中心のY座標の比率。
                            val pivotY = (panelHeight + visualGap + orbiterOffset + (buttonHeight / 2)) / panelHeight

                            // スケール時のピボットX（TransformOrigin.pivotX）
                            // パネル幅 (800dp) に対する、「非表示/表示」ボタンの中心位置の比率。
                            // Row の中心がパネルの中心 (0.5f = 400dp) に位置し、そこから左にボタン中心までの距離（約100dp）ずれた位置。
                            // 400dp - 100dp = 300dp。よって 300dp / 800dp = 0.375f。
                            val pivotX = 0.375f

                            // SpatialPanelの描画領域を高さを増やして下まで確保します。
                            // これにより、どれだけ距離を離してもクリッピング（枠外での消失）されずにオービターまでアニメーションできます。
                            SpatialPanel(
                                modifier = SubspaceModifier
                                    .width(panelWidth)
                                    .height(panelHeight + visualGap)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    val animationDuration = 800
                                    val easingSpec = EaseInOutCubic

                                    // メインの空間パネル（上部に600dpの高さで配置）
                                    AnimatedVisibility(
                                        visible = isPanelVisible,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(panelHeight)
                                            .align(Alignment.TopCenter),
                                        // 垂直スライドの移動量に「吸い込み先までの距離（slideOffsetY）」を適用
                                        enter = slideInVertically(
                                                    initialOffsetY = { slideOffsetY }, 
                                                    animationSpec = tween(durationMillis = animationDuration, easing = easingSpec)
                                                ) + 
                                                fadeIn(animationSpec = tween(durationMillis = animationDuration, easing = easingSpec)) + 
                                                scaleIn(
                                                    initialScale = 0.02f, 
                                                    // 動的に計算した pivotX, pivotY を指定し、正確にボタンの位置をターゲットします
                                                    transformOrigin = TransformOrigin(pivotX, pivotY), 
                                                    animationSpec = tween(durationMillis = animationDuration, easing = easingSpec)
                                                ),
                                        exit = slideOutVertically(
                                                   targetOffsetY = { slideOffsetY }, 
                                                   animationSpec = tween(durationMillis = animationDuration, easing = easingSpec)
                                               ) + 
                                               fadeOut(animationSpec = tween(durationMillis = animationDuration, easing = easingSpec)) + 
                                               scaleOut(
                                                   targetScale = 0.02f, 
                                                   transformOrigin = TransformOrigin(pivotX, pivotY), // ボタンに向けて縮小
                                                   animationSpec = tween(durationMillis = animationDuration, easing = easingSpec)
                                               )
                                    ) {
                                        Surface(
                                            modifier = Modifier.fillMaxSize(),
                                            color = Color(0xFFB3E5FC).copy(alpha = 0.6f),
                                            shape = MaterialTheme.shapes.large
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "Full Space Mode",
                                                    style = MaterialTheme.typography.headlineMedium
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // メインパネルの下（Bottom）に配置されるオービター（offsetを設定します）
                            Orbiter(
                                position = ContentEdge.Bottom,
                                offset = orbiterOffset
                            ) {
                                Row {
                                    // パネルの表示・非表示を切り替えるボタン
                                    Button(onClick = { isPanelVisible = !isPanelVisible }) {
                                        Text(if (isPanelVisible) "Hide Panel" else "Show Panel")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(onClick = { session?.scene?.requestHomeSpaceMode() }) {
                                        Text("Back to Home Space")
                                    }
                                }
                            }
                        }
                    } else {
                        // ホームスペース（通常の2Dパネルモード）
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Home Space Mode (2D Panel)",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { session?.scene?.requestFullSpaceMode() }) {
                                    Text("Go to Full Space")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
