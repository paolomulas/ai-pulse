package com.aipulse.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private val Paper = Color(0xFFFAF9F5)
private val Warm = Color(0xFFF0EEE6)
private val Ink = Color(0xFF25231F)
private val Muted = Color(0xFF777166)
private val Terra = Color(0xFFC6613F)
private val Sage = Color(0xFF788C5D)

private object AppConfig {
    private const val SITE_BASE_URL = "https://paolomulas.github.io/ai-pulse"
    const val FEED_URL = "https://raw.githubusercontent.com/paolomulas/ai-pulse/main/public/feed.json"
    const val PRIVACY_URL = "$SITE_BASE_URL/privacy.html"
    const val CONTACT_URL = "$SITE_BASE_URL/contact.html"
    const val GUIDELINES_URL = "$SITE_BASE_URL/editorial-guidelines.html"
    const val CONTACT_EMAIL = "mailto:paolomulas@gmail.com"
}

private data class Story(
    val id: String,
    val section: String,
    val title: String,
    val summary: String,
    val source: String,
    val url: String,
    val type: String,
    val duration: String = "",
    val language: String = "en",
    val audioUrl: String = "",
    val ttsAudioUrl: String = "",
    val ttsAudioByLanguage: JSONObject? = null,
    val editorial: JSONObject? = null,
    val editorialLevel: String = "raw_feed",
    val editorPickReason: String = ""
)

private data class PulsePreferences(
    val topics: Set<String> = setOf("Agenti Autonomi", "LLM & Modelli"),
    val language: String = "it",
    val curatedOnly: Boolean = true,
    val synced: Boolean = false
)

private val topicOptions = listOf(
    "Agenti Autonomi", "LLM & Modelli", "Open Source",
    "Ricerca & Sviluppo", "Chip & Robotica", "Business AI"
)

private fun loadPreferences(context: Context): PulsePreferences {
    val prefs = context.getSharedPreferences("pulse_preferences", Context.MODE_PRIVATE)
    return PulsePreferences(
        topics = prefs.getStringSet("topics", null)?.toSet() ?: PulsePreferences().topics,
        language = prefs.getString("language", "it") ?: "it",
        curatedOnly = prefs.getBoolean("curated_only", true),
        synced = prefs.getBoolean("synced", false)
    )
}

private fun savePreferences(context: Context, preferences: PulsePreferences) {
    context.getSharedPreferences("pulse_preferences", Context.MODE_PRIVATE).edit()
        .putStringSet("topics", preferences.topics)
        .putString("language", preferences.language)
        .putBoolean("curated_only", preferences.curatedOnly)
        .putBoolean("synced", preferences.synced)
        .apply()
}

private suspend fun loadFeed(): List<Story> = withContext(Dispatchers.IO) {
    val connection = (URL(AppConfig.FEED_URL).openConnection() as HttpURLConnection).apply {
        connectTimeout = 10000
        readTimeout = 15000
        requestMethod = "GET"
        setRequestProperty("Accept", "application/json")
        setRequestProperty("User-Agent", "AIPulse-Android")
    }
    try {
        val items = JSONObject(connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() })
            .optJSONArray("items") ?: return@withContext emptyList()
        buildList {
            for (index in 0 until items.length()) {
                val item = items.optJSONObject(index) ?: continue
                if (item.optString("sourceKind") == "creator_radar" || item.optBoolean("discoveryOnly")) continue
                val url = item.optString("sourceUrl")
                val title = item.optString("title")
                if (!url.startsWith("https://") || title.isBlank()) continue
                val type = item.optString("type", "news")
                val section = when (type) {
                    "github" -> "OPEN SOURCE"
                    "speak_up" -> "SPEAK UP"
                    else -> if (item.optString("sourceKind") == "primary_source") "NEWS · FONTE ORIGINALE"
                    else item.optString("sourceKind", "NEWS").uppercase()
                }
                add(Story(
                    id = item.optString("id", "$index-$title"),
                    section = section,
                    title = title,
                    summary = item.optString("summary"),
                    source = item.optString("sourceName", "Fonte"),
                    url = url,
                    type = type,
                    duration = item.optString("duration"),
                    language = item.optString("language", "en"),
                    audioUrl = item.optString("audioUrl"),
                    ttsAudioUrl = item.optString("ttsAudioUrl"),
                    ttsAudioByLanguage = item.optJSONObject("ttsAudioByLanguage"),
                    editorial = item.optJSONObject("editorial"),
                    editorialLevel = item.optString("editorial_level", "raw_feed"),
                    editorPickReason = item.optString("editor_pick_reason")
                ))
            }
        }
    } finally {
        connection.disconnect()
    }
}

private fun audioUrlFor(story: Story, language: String): String? =
    story.audioUrl.takeIf { it.isNotBlank() }
        ?: story.ttsAudioByLanguage?.optString(language)?.takeIf { it.isNotBlank() }
        ?: story.ttsAudioByLanguage?.optString("en")?.takeIf { it.isNotBlank() }
        ?: story.ttsAudioByLanguage?.optString("it")?.takeIf { it.isNotBlank() }
        ?: story.ttsAudioUrl.takeIf { it.isNotBlank() }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PulseScreen() }
    }
}

@Composable
private fun PulseScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) }
    var preferences by remember { mutableStateOf(loadPreferences(context)) }
    var stories by remember { mutableStateOf<List<Story>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    fun refresh() {
        scope.launch {
            loading = true
            stories = runCatching { loadFeed() }.getOrDefault(emptyList())
            loading = false
        }
    }
    LaunchedEffect(Unit) { refresh() }

    MaterialTheme(colorScheme = androidx.compose.material3.lightColorScheme(
        background = Paper, surface = Paper, onBackground = Ink, onSurface = Ink,
        primary = Terra, secondary = Sage
    )) {
        Scaffold(
            containerColor = Paper,
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    listOf(
                        "Briefing" to Icons.Default.AutoAwesome,
                        "Radar" to Icons.Default.Explore,
                        "Speak Up" to Icons.Default.VolumeUp,
                        "My Pulse" to Icons.Default.Person
                    ).forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = { Icon(item.second, contentDescription = item.first) },
                            label = { Text(item.first, fontSize = 10.sp) }
                        )
                    }
                }
            }
        ) { padding ->
            when (selectedTab) {
                0 -> BriefingScreen(stories, preferences, loading, ::refresh, padding)
                1 -> RadarScreen(padding)
                2 -> SpeakUpScreen(stories, preferences.language, padding)
                else -> MyPulseScreen(preferences, { newPreferences ->
                    preferences = newPreferences
                    savePreferences(context, newPreferences)
                    Toast.makeText(context, "Preferenze salvate", Toast.LENGTH_SHORT).show()
                }, padding)
            }
        }
    }
}

@Composable
private fun ScreenColumn(padding: PaddingValues, content: @Composable (Modifier) -> Unit) {
    content(Modifier.fillMaxSize().padding(padding))
}

@Composable
private fun Masthead(onRefresh: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("PULSE AI", fontFamily = FontFamily.Serif, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text("INTELLIGENCE FOR THE AUTONOMOUS ERA", color = Muted, fontSize = 9.sp, letterSpacing = 1.2.sp)
        }
        IconButton(onClick = onRefresh) { Icon(Icons.Default.Refresh, "Aggiorna", tint = Terra) }
    }
}

private fun matchesTopic(story: Story, topics: Set<String>): Boolean {
    if (topics.isEmpty()) return true
    val text = "${story.title} ${story.summary} ${story.section}".lowercase()
    return topics.any { topic ->
        when (topic) {
            "Agenti Autonomi" -> listOf("agent", "autonom", "agentic").any(text::contains)
            "LLM & Modelli" -> listOf("llm", "model", "gpt", "language").any(text::contains)
            "Open Source" -> listOf("open source", "github", "opensource").any(text::contains)
            "Ricerca & Sviluppo" -> listOf("research", "ricerca", "science", "paper").any(text::contains)
            "Chip & Robotica" -> listOf("chip", "robot", "hardware", "nvidia").any(text::contains)
            else -> listOf("business", "market", "funding", "company").any(text::contains)
        }
    }
}

@Composable
private fun BriefingScreen(
    stories: List<Story>,
    preferences: PulsePreferences,
    loading: Boolean,
    refresh: () -> Unit,
    padding: PaddingValues
) {
    val context = LocalContext.current
    val visible = stories.filter { matchesTopic(it, preferences.topics) && (!preferences.curatedOnly || it.editorialLevel != "raw_feed") }
    val hero = visible.firstOrNull()
    ScreenColumn(padding) { modifier ->
        LazyColumn(modifier, contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { Masthead(refresh) }
            item {
                Text("THE DAILY BRIEFING", color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
                Spacer(Modifier.height(7.dp))
                Text("Intelligenza, senza rumore.", fontFamily = FontFamily.Serif, fontSize = 32.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("Una selezione ragionata delle idee e dei sistemi che stanno definendo l'era autonoma.", color = Muted, fontSize = 14.sp, lineHeight = 20.sp)
            }
            if (hero != null) {
                item { StoryCard(hero, preferences.language, true) }
                items(visible.drop(1), key = { it.id }) { story -> StoryCard(story, preferences.language, false) }
            } else {
                item {
                    Text(
                        if (loading) "Caricamento delle fonti originali…" else "Nessuna storia corrisponde alle tue preferenze. Aggiorna My Pulse o riprova.",
                        color = Muted, fontSize = 14.sp, lineHeight = 20.sp
                    )
                }
            }
            item { Footer(context) }
        }
    }
}

@Composable
private fun StoryCard(story: Story, language: String, hero: Boolean) {
    val context = LocalContext.current
    val localized = story.editorial?.optJSONObject(language)
        ?: story.editorial?.optJSONObject("en")
        ?: story.editorial?.optJSONObject("it")
    val curated = localized != null && story.editorialLevel != "raw_feed"
    val title = if (curated) localized?.optString("title")?.takeIf { it.isNotBlank() } ?: story.title else story.title
    val summary = if (curated) localized?.optString("summary")?.takeIf { it.isNotBlank() } ?: story.summary else story.summary
    val why = if (curated) localized?.optString("why_it_matters")?.takeIf { it.isNotBlank() } else null
    Card(
        Modifier.fillMaxWidth().clickable { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(story.url))) } },
        colors = CardDefaults.cardColors(containerColor = if (hero) Warm else Color.White),
        shape = RoundedCornerShape(if (hero) 4.dp else 16.dp)
    ) {
        Column(Modifier.padding(if (hero) 22.dp else 17.dp)) {
            Text(
                if (hero) "EXCLUSIVE" else if (story.editorialLevel == "editors_pick") "EDITOR'S PICK" else "FEATURED ANALYSIS",
                color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp
            )
            Spacer(Modifier.height(9.dp))
            Text(title, fontFamily = FontFamily.Serif, fontSize = if (hero) 27.sp else 19.sp, lineHeight = if (hero) 31.sp else 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(summary, color = Muted, fontSize = 13.sp, lineHeight = 19.sp, maxLines = if (hero) 6 else 4, overflow = TextOverflow.Ellipsis)
            if (!why.isNullOrBlank()) {
                Spacer(Modifier.height(14.dp))
                Surface(color = Color.White.copy(alpha = .72f), shape = RoundedCornerShape(4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(if (language == "it") "PERCHÉ CONTA" else "WHY IT MATTERS", color = Terra, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(why, fontSize = 12.sp, lineHeight = 17.sp)
                    }
                }
            }
            Spacer(Modifier.height(13.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Fonte: ${story.source}", color = Terra, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                audioUrlFor(story, language)?.let { audioUrl ->
                    TextButton(onClick = {
                        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW).apply { setDataAndType(Uri.parse(audioUrl), "audio/*") }) }
                    }, contentPadding = PaddingValues(0.dp)) {
                        Icon(Icons.Default.GraphicEq, null, tint = Terra, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Ascolta", color = Terra, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RadarScreen(padding: PaddingValues) {
    ScreenColumn(padding) { modifier ->
        LazyColumn(modifier, contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text("RADAR", color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
                Text("Dove si muove l'AI", fontFamily = FontFamily.Serif, fontSize = 31.sp, fontWeight = FontWeight.Bold)
                Text("Temi, persone e segnali da tenere d'occhio.", color = Muted, fontSize = 14.sp)
            }
            items(listOf(
                "AGENTI AUTONOMI" to "I sistemi che imparano a pianificare, usare strumenti e agire nel mondo.",
                "FRONTIER MODELS" to "La corsa ai modelli più capaci, efficienti e aperti.",
                "SIMONE RIZZO · CREATOR RADAR" to "Voci indipendenti che spiegano cosa sta succedendo dietro il prossimo rilascio."
            )) { (kicker, body) ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(18.dp)) {
                        Text(kicker, color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
                        Spacer(Modifier.height(7.dp))
                        Text(body, fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeakUpScreen(stories: List<Story>, language: String, padding: PaddingValues) {
    val context = LocalContext.current
    val episodes = stories.filter { it.type == "speak_up" }
    ScreenColumn(padding) { modifier ->
        LazyColumn(modifier, contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Icon(Icons.Default.VolumeUp, null, tint = Terra, modifier = Modifier.size(28.dp))
                Text("SPEAK UP", color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
                Text("Le voci dell'AI", fontFamily = FontFamily.Serif, fontSize = 31.sp, fontWeight = FontWeight.Bold)
                Text("Briefing audio e conversazioni per ascoltare la frontiera.", color = Muted, fontSize = 14.sp)
            }
            items(episodes, key = { it.id }) { story ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(17.dp)) {
                        Text(story.source.uppercase() + if (story.duration.isNotBlank()) " · ${story.duration}" else "", color = Sage, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(7.dp))
                        Text(story.title, fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(5.dp))
                        Text(story.summary, color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
                        audioUrlFor(story, language)?.let { url ->
                            TextButton(onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW).apply { setDataAndType(Uri.parse(url), "audio/*") }) } }, contentPadding = PaddingValues(0.dp)) {
                                Icon(Icons.Default.GraphicEq, null, tint = Terra)
                                Spacer(Modifier.width(5.dp))
                                Text("Riproduci episodio", color = Terra, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MyPulseScreen(
    saved: PulsePreferences,
    onSave: (PulsePreferences) -> Unit,
    padding: PaddingValues
) {
    var topics by remember(saved) { mutableStateOf(saved.topics) }
    var language by remember(saved) { mutableStateOf(saved.language) }
    var curated by remember(saved) { mutableStateOf(saved.curatedOnly) }
    var synced by remember(saved) { mutableStateOf(saved.synced) }
    ScreenColumn(padding) { modifier ->
        LazyColumn(modifier, contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            item {
                Text("MY PULSE", color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
                Text("Personalizza il tuo Briefing", fontFamily = FontFamily.Serif, fontSize = 30.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold)
                Text("Scegli cosa vuoi capire ogni mattina.", color = Muted, fontSize = 14.sp)
            }
            item {
                SectionLabel("I TUOI ARGOMENTI")
                topicOptions.forEach { topic ->
                    Row(Modifier.fillMaxWidth().clickable {
                        topics = if (topic in topics) topics - topic else topics + topic
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = topic in topics, onCheckedChange = { checked -> topics = if (checked) topics + topic else topics - topic })
                        Text(topic, fontSize = 14.sp)
                    }
                }
            }
            item {
                SectionLabel("LINGUA DEL BRIEFING")
                listOf("it" to "Italiano", "en" to "English", "es" to "Español", "zh" to "中文", "de" to "Deutsch").forEach { (code, label) ->
                    Row(Modifier.fillMaxWidth().clickable { language = code }, verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = language == code, onClick = { language = code })
                        Text("${code.uppercase()}  $label", fontSize = 14.sp)
                    }
                }
            }
            item {
                SectionLabel("LIVELLO EDITORIALE")
                listOf(true to "Solo Sintesi Curate (Curated)", false to "Tutte le Notizie (All Feed)").forEach { (value, label) ->
                    Row(Modifier.fillMaxWidth().clickable { curated = value }, verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = curated == value, onClick = { curated = value })
                        Text(label, fontSize = 14.sp)
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Warm), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(17.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(32.dp).background(Color.White, RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                                Text("G", color = Color(0xFF4285F4), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Sincronizza il tuo Pulse", fontWeight = FontWeight.Bold)
                                Text("Accedi con Google per sincronizzare le tue preferenze e ricevere il Briefing mattutino", color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = { synced = true }, colors = ButtonDefaults.buttonColors(containerColor = if (synced) Sage else Ink)) {
                            if (synced) Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                            Text(if (synced) "Account sincronizzato" else "Accedi con Google")
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = { onSave(PulsePreferences(topics, language, curated, synced)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Terra),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Salva Preferenze", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
    HorizontalDivider(Modifier.padding(vertical = 6.dp), color = Warm)
}

@Composable
private fun Footer(context: Context) {
    Column(Modifier.padding(top = 18.dp, bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text("PULSE AI · INDEPENDENT AI JOURNAL", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text("Contenuti e collegamenti rimandano sempre alle fonti originali.", color = Muted, fontSize = 11.sp)
        Text("Privacy Policy", color = Terra, fontSize = 11.sp, modifier = Modifier.clickable { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.PRIVACY_URL))) } })
        Text("Contatti e informazioni editoriali", color = Terra, fontSize = 11.sp, modifier = Modifier.clickable { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.CONTACT_URL))) } })
    }
}
