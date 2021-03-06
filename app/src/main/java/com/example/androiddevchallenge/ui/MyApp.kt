package com.example.androiddevchallenge.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.MainViewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.purple500
import kotlinx.coroutines.launch

@Composable
fun MyApp() {
    val sbHostState = SnackbarHostState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Countdown Timer")
                }
            )
        },
        snackbarHost = {
            SnackbarHost(sbHostState)
        }
    ) {
        val viewModel: MainViewModel = viewModel()
        val isCalculating = remember { viewModel.isCalculating }
        Surface(color = Color.White, modifier = Modifier.fillMaxSize(1f)) {
            Column(
                Modifier.fillMaxSize(1f),
                verticalArrangement = Arrangement.Top) {

                Text(
                    text = "Tips: click text below to choose to countdown",
                    modifier = Modifier.padding(16.dp)
                )

                TimeList(times = viewModel.hourTimes) {
                    viewModel.selectTime(it)
                }

                TimeList(times = viewModel.minTimes) {
                    viewModel.selectTime(it)
                }

                TimeList(times = viewModel.secondTimes) {
                    viewModel.selectTime(it)
                }

                Row(
                    Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        ProgressCircle(
                            modifier = Modifier
                                .size(280.dp),
                            color = Color(0x666200EE),
                            strokeWidth = 32.dp,
                            progress = 10f
                        )
                        if (viewModel.totalSecond > 0) {
                            ProgressCircle(
                                modifier = Modifier
                                    .size(280.dp),
                                color = purple500,
                                strokeWidth = 32.dp,
                                progress = viewModel.calcSecond / viewModel.totalSecond.toFloat()
                            )
                        }
                        if (viewModel.selectTime.value.isNotEmpty()) {
                            Column(
                                Modifier.fillMaxWidth(1f)
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(1f),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "selected time: ${viewModel.selectTime.value}",
                                        style = TextStyle(color = Color.Black)
                                    )
                                }
                                Row(
                                    Modifier.fillMaxWidth(1f),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (viewModel.realHour.value.isNotEmpty()) {
                                        Text(
                                            text = "${viewModel.realHour.value}:",
                                            style = TextStyle(color = Color.Black)
                                        )
                                    }
                                    if (viewModel.realMinute.value.isNotEmpty()) {
                                        Text(
                                            text = "${viewModel.realMinute.value}:",
                                            style = TextStyle(color = Color.Black)
                                        )
                                    }
                                    if (viewModel.realSecond.value.isNotEmpty()) {
                                        Text(
                                            text = viewModel.realSecond.value,
                                            style = TextStyle(color = Color.Black)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(0.dp, 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (viewModel.selectTime.value.isEmpty()) {
                                coroutineScope.launch {
                                    sbHostState.showSnackbar("You may select a time to countdown~")
                                }
                                return@Button
                            }
                            if (viewModel.isCalculating.value) {
                                viewModel.pauseCountdown()
                            } else {
                                viewModel.startCountdown()
                            }
                        }
                    ) {
                        Crossfade(targetState = isCalculating) { isCalculating ->
                            if (isCalculating.value) {
                                Text(
                                    text = "PAUSE",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                )
                            } else {
                                Text(
                                    text = "START",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.resetCountdown()
                        }
                    ) {
                        Text(
                            text = "RESET",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeList(times: List<String>, onItemClick: (String) -> Unit = {}) {
    LazyRow(modifier = Modifier
        .padding(0.dp, 16.dp)
    ) {
        items(times) { time ->
            TimeItem(
                time = time,
                modifier = Modifier
                    .clickable {
                        onItemClick(time)
                    }
            )
        }
    }
}

@Composable
fun TimeItem(time: String, modifier: Modifier) {
    Card(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .padding(12.dp, 0.dp),
        backgroundColor = purple500
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
        ) {
            Text(
                text = time,
                modifier = Modifier.padding(24.dp, 12.dp),
                style = TextStyle(fontSize = 16.sp, color = Color.White)
            )
        }
    }
}

@Composable
fun ProgressCircle(
    /*@FloatRange(from = 0.0, to = 1.0)*/
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = purple500,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
    }
    Canvas(
        modifier
            .progressSemantics(progress)
            .size(40.dp)
            .focusable()
    ) {
        // Start at 12 O'clock
        val startAngle = 270f
        val sweep = (1 - progress) * 360f
        drawCircularIndicator(startAngle, sweep, color, stroke)
    }
}

private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}

@Preview
@Composable
fun TimeListPreview() {
    val times = listOf("1", "2", "3", "4", "5")
    MyTheme {
        TimeList(times = times)
    }
}

@Preview
@Composable
fun ProgressPreview() {
    MyTheme {
        ProgressCircle(
            modifier = Modifier
                .size(280.dp),
            color = Color.Blue,
            strokeWidth = 32.dp,
            progress = 0.5f,
        )
    }
}