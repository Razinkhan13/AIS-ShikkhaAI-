package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioPodcastInfo
import com.example.data.model.TranscriptLine
import com.example.ui.theme.TacticalAmberSecondary
import com.example.ui.theme.TacticalCyanGlow

@Composable
fun AudioPodcastCard(
    podcast: AudioPodcastInfo,
    isPlaying: Boolean,
    progressSeconds: Int,
    playbackSpeed: Float,
    onTogglePlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_podcast_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with hosts & duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Podcast",
                            tint = TacticalCyanGlow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = podcast.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = podcast.hostsDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "০.৩ সে. সাইলেন্স রুল",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Player Bar
            val totalSeconds = podcast.episodeDurationSeconds
            val progressFraction = if (totalSeconds > 0) progressSeconds.toFloat() / totalSeconds.toFloat() else 0f
            val isFinished = progressSeconds >= totalSeconds

            Slider(
                value = progressFraction.coerceIn(0f, 1f),
                onValueChange = { frac ->
                    onSeek((frac * totalSeconds).toInt())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audio_seek_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = TacticalCyanGlow,
                    activeTrackColor = TacticalCyanGlow,
                    inactiveTrackColor = TacticalCyanGlow.copy(alpha = 0.2f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(progressSeconds),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTime(totalSeconds),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Play/Pause & Speed Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause / Replay Button
                Button(
                    onClick = onTogglePlayPause,
                    modifier = Modifier.testTag("audio_play_pause_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalCyanGlow),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = when {
                            isPlaying -> Icons.Default.Pause
                            isFinished -> Icons.Default.Replay
                            else -> Icons.Default.PlayArrow
                        },
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            isPlaying -> "বিরতি"
                            isFinished -> "পুনরায় শুনুন"
                            else -> "অডিও শুনুন"
                        },
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Speed Selector Pills
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                        val isSelected = playbackSpeed == speed
                        val chipBg by animateColorAsState(
                            if (isSelected) TacticalAmberSecondary else MaterialTheme.colorScheme.surfaceVariant
                        )
                        val chipTextColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSpeedChange(speed) },
                            color = chipBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${speed}x",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = chipTextColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Synced Transcript Preview
            Text(
                text = "লাইভ পডকাস্ট ট্রান্সক্রিপ্ট (কথোপকথন):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                podcast.transcript.forEachIndexed { index, line ->
                    val isCurrent = progressSeconds >= line.timeSecond &&
                            (index == podcast.transcript.lastIndex || progressSeconds < podcast.transcript[index + 1].timeSecond)

                    TranscriptItem(line = line, isActive = isCurrent, onSeek = { onSeek(line.timeSecond) })
                }
            }
        }
    }
}

@Composable
private fun TranscriptItem(
    line: TranscriptLine,
    isActive: Boolean,
    onSeek: () -> Unit
) {
    val bgColor by animateColorAsState(
        if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSeek() },
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (line.speaker.contains("সাকিব")) TacticalCyanGlow else TacticalAmberSecondary
            ) {
                Text(
                    text = line.speaker,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = line.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = line.timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
