package com.jbastudio.gofish.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GoFishColors = lightColorScheme(
    primary           = SeafoamDeep,
    onPrimary         = Foam,
    primaryContainer  = SeafoamGreen,
    onPrimaryContainer = DeepSea,

    secondary         = CoralDeep,
    onSecondary       = Foam,
    secondaryContainer = CoralPink,
    onSecondaryContainer = DeepSea,

    tertiary          = LavenderDeep,
    onTertiary        = Foam,
    tertiaryContainer = Lavender,
    onTertiaryContainer = DeepSea,

    background        = OceanTop,
    onBackground      = DeepSea,
    surface           = Foam,
    onSurface         = DeepSea,
    surfaceVariant    = OceanMid,
    onSurfaceVariant  = DeepSea,

    error             = CoralDeep,
    onError           = Foam
)

@Composable
fun GoFishTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GoFishColors,
        typography  = Typography,
        content     = content
    )
}
