package br.edu.unisatc.gearlog.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance

val PremiumBackground = Color(0xFF121212)
val PremiumCard = Color(0xFF1E1E22)
val PremiumPrimary = Color(0xFFB23B3B)
val PremiumOnPrimary = Color(0xFFFFFFFF)
val PremiumOnBackground = Color(0xFFE6E6E6)
val PremiumOnSurface = Color(0xFFE6E6E6)
val PremiumMuted = Color(0xFF9A9A9A)
val PremiumPlateBlue = Color(0xFF1E3A5F)
val JdmDark = PremiumBackground
val JdmRed = PremiumPrimary

val ColorScheme.premiumCard: Color
	@Composable get() = if (background.luminance() < 0.5f) PremiumCard else Color(0xFFF5F5F5)

val ColorScheme.premiumMuted: Color
	@Composable get() = if (background.luminance() < 0.5f) PremiumMuted else Color(0xFF666666)
