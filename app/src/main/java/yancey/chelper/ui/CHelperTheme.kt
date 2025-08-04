/**
 * It is part of CHelper. CHelper is a command helper for Minecraft Bedrock Edition.
 * Copyright (C) 2025  Yancey
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package yancey.chelper.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberPlatformOverscrollFactory
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import yancey.chelper.R
import yancey.chelper.android.about.activity.ShowTextActivity

private val LightColorPalette = CHelperColors(
    mainColor = Color(0xFFEA5947),
    mainColorSecondary = Color(0x6AEA5947),
    background = Color(0xFFF3F3F3),
    backgroundComponent = Color(0xAAFFFFFF),
    backgroundComponentNoTranslate = Color(0xFFFDFDFD),
    textMain = Color(0xFF000000),
    textBond = Color(0xFF000000),
    textSecondary = Color(0xFF444444),
    textHint = Color(0xFF444444),
    textErrorReason = Color(0xFFCC4444),
    underlineErrorReason = Color(0xFFCC4444),
    line = Color(0xFFF3F3F3),
    iconMain = Color(0xFF444444),
    scrollBar = Color(0x99444444),
    overscrollGlowColor = Color(0xFFFFFFFF),
    syntaxHighlightBoolean = Color(0xFF4FAD63),
    syntaxHighlightFloat = Color(0xFF4FAD63),
    syntaxHighlightInteger = Color(0xFF4FAD63),
    syntaxHighlightSymbol = Color(0xFF4FAD63),
    syntaxHighlightId = Color(0xFFD4AC0D),
    syntaxHighlightTargetSelector = Color(0xFF07C160),
    syntaxHighlightCommand = Color(0xFF9F20A7),
    syntaxHighlightBrackets1 = Color(0xFF836C0A),
    syntaxHighlightBrackets2 = Color(0xFF9F20A7),
    syntaxHighlightBrackets3 = Color(0xFF4571E1),
    syntaxHighlightString = Color(0xFFD95A53),
    syntaxHighlightNull = Color(0xFF0FA0C8),
    syntaxHighlightRange = Color(0xFF0FA0C8),
    syntaxHighlightLiteral = Color(0xFF0FA0C8),
)

private val DarkColorPalette = CHelperColors(
    mainColor = Color(0xFFEA5947),
    mainColorSecondary = Color(0x6AEA5947),
    background = Color(0xFF1E1F22),
    backgroundComponent = Color(0xAA2b2d30),
    backgroundComponentNoTranslate = Color(0xFF282A2D),
    textMain = Color(0xFFDDDDDD),
    textBond = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFAAAAAA),
    textHint = Color(0xFFAAAAAA),
    textErrorReason = Color(0xFFCC4444),
    underlineErrorReason = Color(0xFFCC4444),
    line = Color(0xFF222222),
    iconMain = Color(0xFFAAAAAA),
    scrollBar = Color(0x99969696),
    overscrollGlowColor = Color(0xFF000000),
    syntaxHighlightBoolean = Color(0xFFB5CEA8),
    syntaxHighlightFloat = Color(0xFFB5CEA8),
    syntaxHighlightInteger = Color(0xFFB5CEA8),
    syntaxHighlightSymbol = Color(0xFFB5CEA8),
    syntaxHighlightId = Color(0xFFDCDCAA),
    syntaxHighlightTargetSelector = Color(0xFF4EC9B0),
    syntaxHighlightCommand = Color(0xFFC586C0),
    syntaxHighlightBrackets1 = Color(0xFFFFD700),
    syntaxHighlightBrackets2 = Color(0xFFC586C0),
    syntaxHighlightBrackets3 = Color(0xFF179FFF),
    syntaxHighlightString = Color(0xFFCE9178),
    syntaxHighlightNull = Color(0xFF9CDCFE),
    syntaxHighlightRange = Color(0xFF9CDCFE),
    syntaxHighlightLiteral = Color(0xFF9CDCFE),
)

private val LocalCHelperColors = compositionLocalOf {
    LightColorPalette
}

private val LocalBackground = compositionLocalOf<ImageBitmap?> {
    null
}

object CHelperTheme {
    val colors: CHelperColors
        @Composable
        get() = LocalCHelperColors.current
    val backgroundBitmap: ImageBitmap?
        @Composable
        get() = LocalBackground.current

    enum class Theme {
        Light, Dark
    }
}

class CHelperColors(
    mainColor: Color,
    mainColorSecondary: Color,
    background: Color,
    backgroundComponent: Color,
    backgroundComponentNoTranslate: Color,
    textMain: Color,
    textBond: Color,
    textSecondary: Color,
    textHint: Color,
    textErrorReason: Color,
    underlineErrorReason: Color,
    line: Color,
    iconMain: Color,
    scrollBar: Color,
    overscrollGlowColor: Color,
    syntaxHighlightBoolean: Color,
    syntaxHighlightFloat: Color,
    syntaxHighlightInteger: Color,
    syntaxHighlightSymbol: Color,
    syntaxHighlightId: Color,
    syntaxHighlightTargetSelector: Color,
    syntaxHighlightCommand: Color,
    syntaxHighlightBrackets1: Color,
    syntaxHighlightBrackets2: Color,
    syntaxHighlightBrackets3: Color,
    syntaxHighlightString: Color,
    syntaxHighlightNull: Color,
    syntaxHighlightRange: Color,
    syntaxHighlightLiteral: Color,
) {
    var mainColor: Color by mutableStateOf(mainColor)
        private set
    var mainColorSecondary: Color by mutableStateOf(mainColorSecondary)
        private set
    var background: Color by mutableStateOf(background)
        private set
    var backgroundComponent: Color by mutableStateOf(backgroundComponent)
        private set
    var backgroundComponentNoTranslate: Color by mutableStateOf(backgroundComponentNoTranslate)
        private set
    var textMain: Color by mutableStateOf(textMain)
        private set
    var textBond: Color by mutableStateOf(textBond)
        private set
    var textSecondary: Color by mutableStateOf(textSecondary)
        private set
    var textHint: Color by mutableStateOf(textHint)
        private set
    var textErrorReason: Color by mutableStateOf(textErrorReason)
        private set
    var underlineErrorReason: Color by mutableStateOf(underlineErrorReason)
        private set
    var line: Color by mutableStateOf(line)
        private set
    var iconMain: Color by mutableStateOf(iconMain)
        private set
    var scrollBar: Color by mutableStateOf(scrollBar)
        private set
    var overscrollGlowColor: Color by mutableStateOf(overscrollGlowColor)
        private set
    var syntaxHighlightBoolean: Color by mutableStateOf(syntaxHighlightBoolean)
        private set
    var syntaxHighlightFloat: Color by mutableStateOf(syntaxHighlightFloat)
        private set
    var syntaxHighlightInteger: Color by mutableStateOf(syntaxHighlightInteger)
        private set
    var syntaxHighlightSymbol: Color by mutableStateOf(syntaxHighlightSymbol)
        private set
    var syntaxHighlightId: Color by mutableStateOf(syntaxHighlightId)
        private set
    var syntaxHighlightTargetSelector: Color by mutableStateOf(syntaxHighlightTargetSelector)
        private set
    var syntaxHighlightCommand: Color by mutableStateOf(syntaxHighlightCommand)
        private set
    var syntaxHighlightBrackets1: Color by mutableStateOf(syntaxHighlightBrackets1)
        private set
    var syntaxHighlightBrackets2: Color by mutableStateOf(syntaxHighlightBrackets2)
        private set
    var syntaxHighlightBrackets3: Color by mutableStateOf(syntaxHighlightBrackets3)
        private set
    var syntaxHighlightString: Color by mutableStateOf(syntaxHighlightString)
        private set
    var syntaxHighlightNull: Color by mutableStateOf(syntaxHighlightNull)
        private set
    var syntaxHighlightRange: Color by mutableStateOf(syntaxHighlightRange)
        private set
    var syntaxHighlightLiteral: Color by mutableStateOf(syntaxHighlightLiteral)
        private set
}

private object NoIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource) =
        NoIndicationInstance()

    override fun equals(other: Any?) = other === this

    override fun hashCode() = -1

    class NoIndicationInstance : Modifier.Node()
}

@Composable
fun CHelperTheme(
    theme: CHelperTheme.Theme,
    backgroundBitmap: ImageBitmap?,
    content: @Composable () -> Unit
) {
    val targetColor = when (theme) {
        CHelperTheme.Theme.Light -> LightColorPalette
        CHelperTheme.Theme.Dark -> DarkColorPalette
    }

    val animationSpec = TweenSpec<Color>(600)
    val mainColor = animateColorAsState(targetColor.mainColor, animationSpec)
    val mainColorSecondary =
        animateColorAsState(targetColor.mainColorSecondary, animationSpec)
    val background = animateColorAsState(targetColor.background, animationSpec)
    val backgroundComponent = animateColorAsState(targetColor.backgroundComponent, animationSpec)
    val backgroundComponentNoTranslate =
        animateColorAsState(targetColor.backgroundComponentNoTranslate, animationSpec)
    val textMain = animateColorAsState(targetColor.textMain, animationSpec)
    val textBond = animateColorAsState(targetColor.textBond, animationSpec)
    val textSecondary = animateColorAsState(targetColor.textSecondary, animationSpec)
    val textHint = animateColorAsState(targetColor.textHint, animationSpec)
    val textErrorReason = animateColorAsState(targetColor.textErrorReason, animationSpec)
    val underlineErrorReason = animateColorAsState(targetColor.underlineErrorReason, animationSpec)
    val line = animateColorAsState(targetColor.line, animationSpec)
    val iconMain = animateColorAsState(targetColor.iconMain, animationSpec)
    val scrollBar = animateColorAsState(targetColor.scrollBar, animationSpec)
    val overscrollGlowColor = animateColorAsState(targetColor.overscrollGlowColor, animationSpec)
    val syntaxHighlightBoolean =
        animateColorAsState(targetColor.syntaxHighlightBoolean, animationSpec)
    val syntaxHighlightFloat = animateColorAsState(targetColor.syntaxHighlightFloat, animationSpec)
    val syntaxHighlightInteger =
        animateColorAsState(targetColor.syntaxHighlightInteger, animationSpec)
    val syntaxHighlightSymbol =
        animateColorAsState(targetColor.syntaxHighlightSymbol, animationSpec)
    val syntaxHighlightId = animateColorAsState(targetColor.syntaxHighlightId, animationSpec)
    val syntaxHighlightTargetSelector =
        animateColorAsState(targetColor.syntaxHighlightTargetSelector, animationSpec)
    val syntaxHighlightCommand =
        animateColorAsState(targetColor.syntaxHighlightCommand, animationSpec)
    val syntaxHighlightBrackets1 =
        animateColorAsState(targetColor.syntaxHighlightBrackets1, animationSpec)
    val syntaxHighlightBrackets2 =
        animateColorAsState(targetColor.syntaxHighlightBrackets2, animationSpec)
    val syntaxHighlightBrackets3 =
        animateColorAsState(targetColor.syntaxHighlightBrackets3, animationSpec)
    val syntaxHighlightString =
        animateColorAsState(targetColor.syntaxHighlightString, animationSpec)
    val syntaxHighlightNull = animateColorAsState(targetColor.syntaxHighlightNull, animationSpec)
    val syntaxHighlightRange = animateColorAsState(targetColor.syntaxHighlightRange, animationSpec)
    val syntaxHighlightLiteral =
        animateColorAsState(targetColor.syntaxHighlightLiteral, animationSpec)

    val colors = CHelperColors(
        mainColor = mainColor.value,
        mainColorSecondary = mainColorSecondary.value,
        background = background.value,
        backgroundComponent = backgroundComponent.value,
        backgroundComponentNoTranslate = backgroundComponentNoTranslate.value,
        textMain = textMain.value,
        textBond = textBond.value,
        textSecondary = textSecondary.value,
        textHint = textHint.value,
        textErrorReason = textErrorReason.value,
        underlineErrorReason = underlineErrorReason.value,
        line = line.value,
        iconMain = iconMain.value,
        scrollBar = scrollBar.value,
        overscrollGlowColor = overscrollGlowColor.value,
        syntaxHighlightBoolean = syntaxHighlightBoolean.value,
        syntaxHighlightFloat = syntaxHighlightFloat.value,
        syntaxHighlightInteger = syntaxHighlightInteger.value,
        syntaxHighlightSymbol = syntaxHighlightSymbol.value,
        syntaxHighlightId = syntaxHighlightId.value,
        syntaxHighlightTargetSelector = syntaxHighlightTargetSelector.value,
        syntaxHighlightCommand = syntaxHighlightCommand.value,
        syntaxHighlightBrackets1 = syntaxHighlightBrackets1.value,
        syntaxHighlightBrackets2 = syntaxHighlightBrackets2.value,
        syntaxHighlightBrackets3 = syntaxHighlightBrackets3.value,
        syntaxHighlightString = syntaxHighlightString.value,
        syntaxHighlightNull = syntaxHighlightNull.value,
        syntaxHighlightRange = syntaxHighlightRange.value,
        syntaxHighlightLiteral = syntaxHighlightLiteral.value,
    )
    val textSelectionColors = TextSelectionColors(
        handleColor = mainColor.value,
        backgroundColor = mainColorSecondary.value
    )
    val overscrollFactory = rememberPlatformOverscrollFactory(overscrollGlowColor.value)
    CompositionLocalProvider(
        LocalCHelperColors provides colors,
        LocalBackground provides backgroundBitmap,
        LocalTextSelectionColors provides textSelectionColors,
        LocalIndication provides NoIndication,
        LocalOverscrollFactory provides overscrollFactory,
    ) {
        content()
    }
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(),
    maxLines: Int = Int.MAX_VALUE,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(
            color = if (style.color == Color.Unspecified) CHelperTheme.colors.textMain else style.color,
            fontSize = if (style.fontSize == TextUnit.Unspecified) 16.sp else style.fontSize
        ),
        maxLines = maxLines
    )
}

@Composable
fun RootView(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CHelperTheme.colors.background)
    ) {
        CHelperTheme.backgroundBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(insets = WindowInsets.systemBars.union(WindowInsets.ime))
        ) {
            content()
        }
    }
}

@Composable
fun Icon(@DrawableRes id: Int, modifier: Modifier = Modifier, contentDescription: String? = null) {
    Image(
        painter = painterResource(id),
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = ColorFilter.tint(CHelperTheme.colors.iconMain)
    )
}

@Composable
fun Header(title: String) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(CHelperTheme.colors.backgroundComponent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            id = R.drawable.chevron_left,
            modifier = Modifier
                .clickable(onClick = {
                    backDispatcher?.onBackPressed()
                })
                .padding(5.dp)
                .size(25.dp),
            contentDescription = stringResource(R.string.back)
        )
        Text(
            text = title,
            style = TextStyle(
                fontSize = 18.sp,
            ),
            maxLines = 1
        )
    }
}

@Composable
fun Copyright(modifier: Modifier = Modifier, copyright: String = "Copyright \u00a9 2025 Yancey") {
    Text(
        text = copyright,
        modifier = modifier.padding(5.dp),
        style = TextStyle(
            fontSize = 14.sp,
        ),
    )
}

@Composable
fun RootViewWithHeaderAndCopyright(
    title: String,
    copyright: String = "Copyright \u00a9 2025 Yancey",
    content: @Composable () -> Unit
) {
    RootView {
        Column(modifier = Modifier.fillMaxSize()) {
            Header(title)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                content()
            }
            Copyright(Modifier.align(Alignment.CenterHorizontally), copyright)
        }
    }
}

@Composable
fun Collection(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CHelperTheme.colors.backgroundComponent)
    ) {
        content()
    }
}

@Composable
fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 5.dp)
            .background(CHelperTheme.colors.line)
    )
}

@Composable
fun NameAndContent(
    name: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name)
            if (description != null) {
                Text(
                    text = description,
                    style = TextStyle(
                        color = LocalCHelperColors.current.textSecondary,
                        fontSize = 14.sp,
                    )
                )
            }
        }
        content()
    }
}

@Composable
fun NameAndAction(name: String, description: String? = null, onClick: () -> Unit) {
    NameAndContent(
        name = name,
        description = description,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(R.drawable.chevron_right, Modifier.size(25.dp), name)
    }
}

@Composable
fun NameAndValue(name: String, value: String) {
    NameAndContent(name) {
        SelectionContainer {
            Text(
                text = value,
                style = TextStyle(
                    color = CHelperTheme.colors.textSecondary,
                    fontSize = 16.sp
                ),
            )
        }
    }
}

@Composable
fun NameAndLink(name: String, link: Uri) {
    val context = LocalContext.current
    NameAndAction(name) {
        context.startActivity(Intent(Intent.ACTION_VIEW, link))
    }
}

@Composable
fun NameAndAsset(name: String, assetsPath: String) {
    val context = LocalContext.current
    NameAndAction(name) {
        val content = context.assets.open(assetsPath).bufferedReader().use { it.readText() }
        context.startActivity(
            Intent(context, ShowTextActivity::class.java).apply {
                putExtra(ShowTextActivity.TITLE, name)
                putExtra(ShowTextActivity.CONTENT, content)
            }
        )
    }
}

@Composable
fun NameAndStartActivity(name: String, activityClass: Class<*>) {
    val context = LocalContext.current
    NameAndAction(
        name = name,
        onClick = {
            context.startActivity(Intent(context, activityClass))
        }
    )
}

@Composable
fun CollectionName(name: String) {
    Text(
        text = name,
        modifier = Modifier.padding(horizontal = 25.dp, vertical = 15.dp)
    )
}

@Composable
fun SettingsItem(
    name: String,
    description: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    NameAndContent(name = name, description = description) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun Button(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .height(45.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(CHelperTheme.colors.mainColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            ),
        )
    }
}

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    isNarrow: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = CHelperTheme.colors.backgroundComponent)
            .padding(horizontal = 10.dp, vertical = if (isNarrow) 0.dp else 10.dp),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

@Composable
fun TextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    hint: String? = null,
    isNarrow: Boolean = false,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default
) {
    BasicTextField(
        state = state,
        modifier = modifier,
        textStyle = TextStyle(
            color = CHelperTheme.colors.textMain,
            fontSize = 16.sp,
            textAlign =
                if (contentAlignment is BiasAlignment && contentAlignment.horizontalBias == 0f)
                    TextAlign.Center
                else
                    TextAlign.Start,
        ),
        lineLimits = lineLimits,
        cursorBrush = SolidColor(CHelperTheme.colors.mainColor),
        decorator = { innerTextField ->
            Surface(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = contentAlignment,
                isNarrow = isNarrow
            ) {
                innerTextField()
                if (state.text.isEmpty() && hint != null) {
                    Text(
                        text = hint,
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            color = CHelperTheme.colors.textHint,
                        ),
                    )
                }
            }
        }
    )
}

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 40.dp,
    height: Dp = 24.dp,
    thumbRadius: Dp = 10.dp
) {
    val thumbPosition by animateFloatAsState(targetValue = if (checked) 1f else 0f)
    val trackColor by animateColorAsState(
        if (checked)
            CHelperTheme.colors.mainColorSecondary
        else
            Color(0xFFAFAFAF)
    )
    val thumbColor by animateColorAsState(
        if (checked)
            CHelperTheme.colors.mainColor
        else
            Color(0xFFECECEC)
    )
    Box(
        modifier = modifier
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .size(width, height)
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .padding(4.dp)
        ) {
            drawRoundRect(
                color = trackColor,
                topLeft = Offset.Zero,
                size = size,
                cornerRadius = CornerRadius(size.height / 2),
            )
        }
        Canvas(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (width - thumbRadius * 2) * thumbPosition)
                .size(thumbRadius * 2)
                .shadow(
                    elevation = 5.dp,
                    shape = CircleShape,
                    clip = false
                )
        ) {
            drawCircle(
                color = thumbColor,
                radius = thumbRadius.toPx(),
                center = center
            )
        }
    }
}