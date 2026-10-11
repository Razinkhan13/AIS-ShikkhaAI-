package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AcademicLevel
import com.example.data.model.Chapter
import com.example.data.model.FunnelStats
import com.example.data.model.Subject
import com.example.ui.components.FunnelProgressCard
import com.example.ui.theme.TacticalCyanGlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedLevel: AcademicLevel,
    selectedSubject: Subject?,
    chapters: List<Chapter>,
    funnelStats: FunnelStats,
    unresolvedMistakeCount: Int,
    onSelectLevel: (AcademicLevel) -> Unit,
    onSelectSubject: (Subject?) -> Unit,
    onOpenChapter: (Chapter) -> Unit,
    onOpenMistakeVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D1525)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_ais_logo),
                                contentDescription = "AIS Logo",
                                modifier = Modifier.size(38.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AIS ShikkhaAI",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TacticalCyanGlow
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = TacticalCyanGlow
                                ) {
                                    Text(
                                        text = "FREE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "ARMY IBA SPECIALIST • এনসিটিবি রূপান্তর",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenMistakeVault,
                        modifier = Modifier.testTag("home_vault_icon_button")
                    ) {
                        BadgedBox(badge = {
                            if (unresolvedMistakeCount > 0) {
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Text("$unresolvedMistakeCount")
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Mistake Vault",
                                tint = TacticalCyanGlow
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenMistakeVault,
                icon = { Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color.Black) },
                text = { Text("মিস্টেক ভল্ট ($unresolvedMistakeCount)", color = Color.Black, fontWeight = FontWeight.Bold) },
                containerColor = TacticalCyanGlow,
                modifier = Modifier.testTag("fab_mistake_vault")
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. 100:6:1 Mastery Funnel Card
            item {
                FunnelProgressCard(
                    stats = funnelStats,
                    onOpenMistakeVault = onOpenMistakeVault,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 2. Class Level Selector (SSC vs HSC)
            item {
                Column {
                    Text(
                        text = "শ্রেণি ও পরীক্ষা স্তর নির্বাচন:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AcademicLevel.values().forEach { level ->
                            val isSelected = selectedLevel == level
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onSelectLevel(level) }
                                    .testTag("level_selector_${level.name.lowercase()}"),
                                color = if (isSelected) TacticalCyanGlow else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = level.labelBn,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = level.classRange,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) Color.Black.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Subject Pills
            item {
                Column {
                    Text(
                        text = "পাঠ্যবই ও বিষয়:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedSubject == null,
                                onClick = { onSelectSubject(null) },
                                label = { Text("সব বিষয়") }
                            )
                        }
                        items(Subject.values()) { subj ->
                            FilterChip(
                                selected = selectedSubject == subj,
                                onClick = { onSelectSubject(subj) },
                                label = { Text(subj.labelBn) }
                            )
                        }
                    }
                }
            }

            // 4. Chapter List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "এনসিটিবি কগনিটিভ চ্যাপ্টারসমূহ (${chapters.size}টি অধ্যায়)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "৪-ধাপের ফানেল",
                        style = MaterialTheme.typography.labelSmall,
                        color = TacticalCyanGlow,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 5. Chapters
            items(chapters, key = { it.id }) { chapter ->
                ChapterCard(
                    chapter = chapter,
                    onClick = { onOpenChapter(chapter) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun ChapterCard(
    chapter: Chapter,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("chapter_card_${chapter.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${chapter.subject.labelBn} • অধ্যায় ${chapter.chapterNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TacticalCyanGlow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = chapter.level.labelBn,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = chapter.titleBn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = chapter.subtitleBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4-step funnel indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FunnelStepBadge(label = "১. অডিও", icon = Icons.Default.GraphicEq)
                    FunnelStepBadge(label = "২. মাইন্ডম্যাপ", icon = Icons.Default.AccountTree)
                    FunnelStepBadge(label = "৩. ডাউট", icon = Icons.Default.Psychology)
                    FunnelStepBadge(label = "৪. পরীক্ষা", icon = Icons.Default.Assessment)
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open",
                    tint = TacticalCyanGlow
                )
            }
        }
    }
}

@Composable
private fun FunnelStepBadge(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(11.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
