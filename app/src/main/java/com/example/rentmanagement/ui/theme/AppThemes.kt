package com.example.rentmanagement.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.rentmanagement.domain.model.AppColorTheme

/**
 * One palette definition per ready-made theme (Settings > App theme).
 * Both a light and dark variant are provided for every palette so the
 * Appearance switch (Light/Dark/System) stays independent of palette choice.
 */
data class ThemePalette(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val secondary: Color,
    val text: Color,
    val border: Color,
    val onPrimary: Color = Color.White
)

object AppThemePalettes {

    private val monoLight = ThemePalette(
        background = Color(0xFFF7F7F7),
        surface = Color(0xFFFFFFFF),
        primary = Color(0xFF111111),
        secondary = Color(0xFF444444),
        text = Color(0xFF111111),
        border = Color(0xFFD0D0D0)
    )
    private val monoDark = ThemePalette(
        background = Color(0xFF121212),
        surface = Color(0xFF1C1C1C),
        primary = Color(0xFFECECEC),
        secondary = Color(0xFFB0B0B0),
        text = Color(0xFFF2F2F2),
        border = Color(0xFF3A3A3A),
        onPrimary = Color(0xFF111111)
    )

    private val midnight = ThemePalette(
        background = Color(0xFF0B0F14),
        surface = Color(0xFF151B23),
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFF6EA8FE),
        text = Color(0xFFF5F7FA),
        border = Color(0xFF283140),
        onPrimary = Color(0xFF0B0F14)
    )

    private val oceanLight = ThemePalette(
        background = Color(0xFFF5F9FC),
        surface = Color(0xFFFFFFFF),
        primary = Color(0xFF1565C0),
        secondary = Color(0xFF42A5F5),
        text = Color(0xFF17202A),
        border = Color(0xFFD5E4F0)
    )
    private val oceanDark = ThemePalette(
        background = Color(0xFF0E1620),
        surface = Color(0xFF16212E),
        primary = Color(0xFF42A5F5),
        secondary = Color(0xFF1565C0),
        text = Color(0xFFE8F1FA),
        border = Color(0xFF2A3A4C)
    )

    private val emeraldLight = ThemePalette(
        background = Color(0xFFF5F9F6),
        surface = Color(0xFFFFFFFF),
        primary = Color(0xFF087F5B),
        secondary = Color(0xFF2F9E78),
        text = Color(0xFF14201B),
        border = Color(0xFFD3E9DF)
    )
    private val emeraldDark = ThemePalette(
        background = Color(0xFF0E1A15),
        surface = Color(0xFF16261F),
        primary = Color(0xFF2F9E78),
        secondary = Color(0xFF087F5B),
        text = Color(0xFFE6F3EE),
        border = Color(0xFF2A4338)
    )

    private val royalLight = ThemePalette(
        background = Color(0xFFF8F7FC),
        surface = Color(0xFFFFFFFF),
        primary = Color(0xFF5B3CC4),
        secondary = Color(0xFF8067D9),
        text = Color(0xFF18151F),
        border = Color(0xFFE0DBF2)
    )
    private val royalDark = ThemePalette(
        background = Color(0xFF130F1E),
        surface = Color(0xFF1D1830),
        primary = Color(0xFF8067D9),
        secondary = Color(0xFF5B3CC4),
        text = Color(0xFFEEEBFB),
        border = Color(0xFF352B4F)
    )

    private val sunsetLight = ThemePalette(
        background = Color(0xFFFFF8F3),
        surface = Color(0xFFFFFFFF),
        primary = Color(0xFFC2410C),
        secondary = Color(0xFFEA580C),
        text = Color(0xFF24150F),
        border = Color(0xFFF3DCC9)
    )
    private val sunsetDark = ThemePalette(
        background = Color(0xFF1E140D),
        surface = Color(0xFF2B1D13),
        primary = Color(0xFFEA580C),
        secondary = Color(0xFFC2410C),
        text = Color(0xFFFBEBE0),
        border = Color(0xFF473224)
    )

    private val roseLight = ThemePalette(
        background = Color(0xFFFFF7F9),
        surface = Color(0xFFFFFFFF),
        primary = Color(0xFFBE185D),
        secondary = Color(0xFFDB2777),
        text = Color(0xFF24131B),
        border = Color(0xFFF3D6E3)
    )
    private val roseDark = ThemePalette(
        background = Color(0xFF1E1015),
        surface = Color(0xFF2B1820),
        primary = Color(0xFFDB2777),
        secondary = Color(0xFFBE185D),
        text = Color(0xFFFBE4ED),
        border = Color(0xFF472A36)
    )

    private val graphiteLight = ThemePalette(
        background = Color(0xFFECECEC),
        surface = Color(0xFFFAFAFA),
        primary = Color(0xFF292929),
        secondary = Color(0xFF555555),
        text = Color(0xFF171717),
        border = Color(0xFFC7C7C7)
    )
    private val graphiteDark = ThemePalette(
        background = Color(0xFF161616),
        surface = Color(0xFF202020),
        primary = Color(0xFFDADADA),
        secondary = Color(0xFF9A9A9A),
        text = Color(0xFFF0F0F0),
        border = Color(0xFF3B3B3B),
        onPrimary = Color(0xFF161616)
    )

    /** Preset accent choices for the Custom theme (kept simple: pick one, we derive the rest). */
    val customAccentOptions: Map<String, Color> = linkedMapOf(
        "Black" to Color(0xFF111111),
        "Blue" to Color(0xFF1565C0),
        "Green" to Color(0xFF087F5B),
        "Purple" to Color(0xFF5B3CC4),
        "Orange" to Color(0xFFC2410C),
        "Red" to Color(0xFFB91C1C),
        "Pink" to Color(0xFFBE185D)
    )

    fun customPalette(accent: Color, isDark: Boolean): ThemePalette = if (!isDark) {
        ThemePalette(
            background = Color(0xFFF7F7F7),
            surface = Color(0xFFFFFFFF),
            primary = accent,
            secondary = accent.copy(alpha = 0.75f),
            text = Color(0xFF111111),
            border = Color(0xFFD0D0D0)
        )
    } else {
        ThemePalette(
            background = Color(0xFF121212),
            surface = Color(0xFF1C1C1C),
            primary = accent,
            secondary = accent.copy(alpha = 0.75f),
            text = Color(0xFFF2F2F2),
            border = Color(0xFF3A3A3A)
        )
    }

    fun paletteFor(theme: AppColorTheme, isDark: Boolean, customAccent: Color? = null): ThemePalette =
        when (theme) {
            AppColorTheme.MONO -> if (isDark) monoDark else monoLight
            AppColorTheme.MIDNIGHT -> midnight // premium/security-focused: same dark palette regardless of mode
            AppColorTheme.OCEAN -> if (isDark) oceanDark else oceanLight
            AppColorTheme.EMERALD -> if (isDark) emeraldDark else emeraldLight
            AppColorTheme.ROYAL -> if (isDark) royalDark else royalLight
            AppColorTheme.SUNSET -> if (isDark) sunsetDark else sunsetLight
            AppColorTheme.ROSE -> if (isDark) roseDark else roseLight
            AppColorTheme.GRAPHITE -> if (isDark) graphiteDark else graphiteLight
            AppColorTheme.CUSTOM -> customPalette(customAccent ?: customAccentOptions.getValue("Black"), isDark)
        }
}
