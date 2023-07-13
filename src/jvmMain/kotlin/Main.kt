import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.difflib.text.DiffRowGenerator
import com.mikepenz.markdown.Markdown
import com.mikepenz.markdown.MarkdownDefaults
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import org.jetbrains.skiko.Cursor


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSplitPaneApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme(darkColorScheme()) {
        var left by remember { mutableStateOf("") }
        var right by remember { mutableStateOf("") }
        val sb = "(?:~|\\*\\*)(.*?)(?:~|\\*\\*)".toRegex()
        val diffGenerator = remember {
            DiffRowGenerator.create()
                .showInlineDiffs(true)
                .mergeOriginalRevised(true)
                .inlineDiffByWord(true)
                .ignoreWhiteSpaces(true)
                .oldTag { _: Boolean -> "~" } //introduce markdown style for strikethrough
                .newTag { _: Boolean -> "**" } //introduce markdown style for bold
                .build()
        }

        val d by remember { derivedStateOf { diffGenerator.generateDiffRows(left.lines(), right.lines()) } }

        Scaffold(
            topBar = { TopAppBar(title = { Text("String Compare") }) }
        ) { padding ->
            VerticalSplitPane(
                splitPaneState = rememberSplitPaneState(.5f, false),
                modifier = Modifier.padding(padding)
            ) {
                first {
                    Row(
                        Modifier.fillMaxSize()
                    ) {
                        TextField(
                            value = left,
                            onValueChange = { left = it },
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        )
                        Box(
                            Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.background)
                        )
                        TextField(
                            value = right,
                            onValueChange = { right = it },
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        )
                    }
                }
                second {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        CustomText(
                            text = buildAnnotatedString {
                                d.forEach { r ->
                                    append(annotateStringWithRegex(r.oldLine, sb))
                                    appendLine()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.background)
                        )

                        CustomText(
                            text = buildAnnotatedString {
                                d.forEach { r ->
                                    append(annotateStringWithRegex(r.newLine, sb))
                                    appendLine()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                splitter {
                    handle {
                        Divider(
                            Modifier
                                .markAsHandle()
                                .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
                        )
                    }
                    visiblePart { Divider() }
                }
            }
        }
    }
}

@Composable
fun CustomText(
    text: AnnotatedString,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        modifier = modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.small
            )
            .padding(4.dp)
    )
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

fun main1() {
    val s = "~(.*?)~".toRegex()
    val s1 = "hi ~hello~ there ~world~ okay"
    val d = s.findAll(s1)
    d.forEach {
        it.groups.forEach { println(it) }
        println("---")
        println(it.value)
        println(it.range)
        println(it.groupValues)
        println(it.destructured.match)
    }
}

fun annotateStringWithRegex(text: String, regex: Regex): AnnotatedString {
    val annotatedString = buildAnnotatedString {
        val matches = regex.findAll(text)
        var currentPosition = 0

        for (match in matches) {
            // Append the text before the match
            append(text.substring(currentPosition, match.range.first))

            // Determine the style based on the matched delimiter
            val style = when {
                match.value.startsWith("~") -> SpanStyle(
                    textDecoration = TextDecoration.LineThrough,
                    background = Alizarin
                )
                match.value.startsWith("**") -> SpanStyle(
                    fontWeight = FontWeight.Bold,
                    background = Emerald
                )
                else -> null
            }

            // Append the matched text with the appropriate style
            style?.let { withStyle(it) { append(match.groupValues.last()) } } ?: append(match.groupValues.last())

            currentPosition = match.range.last + 1
        }

        // Append any remaining text after the last match
        append(text.substring(currentPosition))
    }

    return annotatedString
}

val Emerald = Color(0xFF2ecc71)
val Alizarin = Color(0xFFe74c3c)
