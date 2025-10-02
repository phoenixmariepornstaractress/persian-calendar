package com.byagowi.persiancalendar.ui.utils

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.byagowi.persiancalendar.entities.Jdn

/**
 * Lightweight UI utilities shared across the app. Focus is on safety, readability and
 * small helpers useful for theming and animation.
 */

/**
 * Decide whether a color should be considered 'light'. The threshold (0.5) follows
 * Material guidelines and the project's prior implementation.
 */
@Stable
val Color.isLight: Boolean
    get() = this.luminance() > 0.5

/** Return a contrasting text color for the provided background. */
fun Color.contrastingTextColor(): Color = if (isLight) Color.Black else Color.White

/**
 * Material corner helpers — a small collection of commonly used shapes derived from the
 * current Material theme shapes so they respond to theme changes.
 */
@Composable
@Stable
fun materialCornerExtraLargeTop(): CornerBasedShape = MaterialTheme.shapes.extraLarge.copy(
    bottomStart = ZeroCornerSize,
    bottomEnd = ZeroCornerSize,
)

@Composable
@Stable
fun materialCornerExtraLargeNoBottomEnd(): CornerBasedShape = MaterialTheme.shapes.extraLarge.copy(
    bottomEnd = ZeroCornerSize
)

@Composable
@Stable
fun materialCornerTopStartRounded(): CornerBasedShape = MaterialTheme.shapes.extraLarge.copy(
    topEnd = ZeroCornerSize,
    bottomStart = ZeroCornerSize,
    bottomEnd = ZeroCornerSize
)

@Composable
@Stable
fun materialCornerOnlyBottom(): CornerBasedShape = MaterialTheme.shapes.extraLarge.copy(
    topStart = ZeroCornerSize,
    topEnd = ZeroCornerSize
)

/**
 * A small shared bounds transform used with SharedElement / AnimatedContent where
 * a gentle spring animation is desired. Tuned to produce smooth motion on most devices.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
val appBoundsTransform: BoundsTransform = BoundsTransform { _, _ ->
    spring(
        stiffness = Spring.StiffnessMediumLow,
        dampingRatio = Spring.DampingRatioLowBouncy,
        visibilityThreshold = Rect.VisibilityThreshold
    )
}

/**
 * Savers for small stateful types used in Compose screens.
 */
val JdnSaver: Saver<MutableState<Jdn>, Long> = Saver(
    save = { it.value.value },
    restore = { mutableStateOf(Jdn(it)) }
)

val AnimatableFloatSaver: Saver<Animatable<Float, AnimationVector1D>, Float> = Saver(
    save = { it.value },
    restore = { Animatable(it) }
)

// --- Common numeric constants (kept as floats/ints to remain compatible with call sites) ---

// When something needs to match Material default theme corner sizes
const val ExtraLargeShapeCornerSize: Float = 28f
const val LargeShapeCornerSize: Float = 16f
const val SmallShapeCornerSize: Float = 8f

// Plain items in settings should have this horizontal padding (pixels-independent number)
const val SettingsHorizontalPaddingItem: Int = 24

// Clickable items in settings should have this height
const val SettingsItemHeight: Int = SettingsHorizontalPaddingItem * 2

// Vertical padding for settings items (useful for composing layouts)
val SettingsItemVerticalPadding: Dp = 12.dp

// Common alpha value to blend a component with its background
const val AppBlendAlpha: Float = 0.75f

// analogous to compat's listPreferredItemPaddingLeft/Right, represented in dp when useful
val ItemWidthDp: Dp = 100.dp
const val ItemWidth: Float = 100f

// Common elevation values for cards or dialogs
const val DefaultCardElevation: Float = 6f
const val ElevatedCardElevation: Float = 12f

// Standardized corner sizes returned as Dp for layout calculations
val ExtraLargeShapeCornerSizeDp: Dp get() = ExtraLargeShapeCornerSize.dp
val LargeShapeCornerSizeDp: Dp get() = LargeShapeCornerSize.dp
val SmallShapeCornerSizeDp: Dp get() = SmallShapeCornerSize.dp

// --- Animation helpers ---

/** Convenience extension to animate an [Animatable] Float to the target value using a spring. */
suspend fun Animatable<Float, AnimationVector1D>.animateToValue(
    targetValue: Float,
    stiffness: Float = Spring.StiffnessMedium,
    dampingRatio: Float = Spring.DampingRatioMediumBouncy
) {
    this.animateTo(targetValue, spring(stiffness = stiffness, dampingRatio = dampingRatio))
}

// --- Color helpers related to Material color scheme ---

/**
 * Pick a content color (onBackground / onSurface) depending on a background color and the
 * provided [ColorScheme] so components can choose a sensible default.
 */
fun Color.pickContentColor(colorScheme: ColorScheme): Color = if (isLight) colorScheme.onBackground else colorScheme.onSurface

/** Blend two colors with the given alpha (source over destination). */
fun blendColors(src: Color, dst: Color, alpha: Float): Color {
    val a = alpha.coerceIn(0f, 1f)
    val r = (src.red * a) + (dst.red * (1 - a))
    val g = (src.green * a) + (dst.green * (1 - a))
    val b = (src.blue * a) + (dst.blue * (1 - a))
    val opa = (src.alpha * a) + (dst.alpha * (1 - a))
    return Color(r, g, b, opa)
}

/** Return a surface color that is slightly elevated from the provided [base] using [elevation].
 * This mimics small Material3 elevation tinting for simple cases.
 */
fun elevatedSurfaceColor(base: Color, elevation: Float, colorScheme: ColorScheme): Color {
    // A very small climb in alpha/elevation effect; keep conservative to avoid strong tints.
    val overlayAlpha = (elevation / 100f).coerceIn(0f, 0.12f)
    return blendColors(colorScheme.surface, base, overlayAlpha)
}

/** Simple utility to return whether a layout should use a light or dark content color
 * given the current [ColorScheme] and background color. */
fun shouldUseLightContent(background: Color, colorScheme: ColorScheme): Boolean =
    !background.isLight

 
