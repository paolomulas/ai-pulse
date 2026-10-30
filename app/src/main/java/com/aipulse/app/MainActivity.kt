package com.aipulse.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private val Paper = Color(0xFFFAF9F5)
private val Warm = Color(0xFFF0EEE6)
private val Ink = Color(0xFF25231F)
private val Muted = Color(0xFF777166)
private val Terra = Color(0xFFC6613F)
private val Sage = Color(0xFF788C5D)
private const val FEED_URL = "https://raw.githubusercontent.com/paolomulas/ai-pulse/main/public/feed.json"

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
    val podcastCategory: String = "frontier",
    val editorial: JSONObject? = null,
    val editorialLevel: String = "raw_feed",
    val editorPickReason: String = ""
)

private val demoStories = listOf(
    Story("demo-1", "MODELLI", "L'intelligenza artificiale entra in una nuova fase", "Aggiornamenti e analisi sulle nuove generazioni di modelli. I contenuti live saranno collegati alle fonti verificate.", "AI Pulse Editorial", "https://openai.com/news/", "news"),
    Story("demo-2", "OPEN SOURCE", "I repository AI da tenere d'occhio", "Progetti emergenti ordinati considerando attività recente, crescita e interesse della community.", "GitHub", "https://github.com/trending", "github"),
    Story("demo-3", "RICERCA", "Ricerca e applicazioni: cosa cambia", "Una selezione di aggiornamenti da laboratori e gruppi di ricerca, con collegamento alle fonti originali.", "Google DeepMind", "https://deepmind.google/discover/blog/", "news"),
    Story("demo-4", "SPEAK UP · INTERVISTA", "Le idee che orientano il dibattito sull'AI", "La sezione raccoglierà podcast, interviste e conferenze recenti. Citazioni dirette solo con trascrizioni verificabili.", "Latent Space", "https://www.latent.space/", "speak_up")
)

private suspend fun loadFeed(): List<Story> = withContext(Dispatchers.IO) {
    val connection = (URL(FEED_URL).openConnection() as HttpURLConnection).apply {
        connectTimeout = 10000
        readTimeout = 15000
        requestMethod = "GET"
        setRequestProperty("Accept", "application/json")
        setRequestProperty("User-Agent", "AIPulse-Android")
    }
    try {
        val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val items = JSONObject(body).optJSONArray("items") ?: return@withContext emptyList()
        buildList {
            for (index in 0 until items.length()) {
                val item = items.optJSONObject(index) ?: continue
                val url = item.optString("sourceUrl")
                val title = item.optString("title")
                if (!url.startsWith("https://") || title.isBlank()) continue
                val type = item.optString("type", "news")
                val section = when (type) {
                    "github" -> "OPEN SOURCE"
                    "speak_up" -> "SPEAK UP"
                    else -> item.optString("sourceKind", "NEWS").uppercase()
                }
                add(
                    Story(
                        id = item.optString("id", "$index-$title"),
                        section = section,
                        title = title,
                        summary = item.optString("summary"),
                        source = item.optString("sourceName", "Fonte"),
                        url = url,
                        type = type,
                        duration = item.optString("duration"),
                        language = item.optString("language", "en"),
                        podcastCategory = item.optString("podcastCategory", "frontier"),
                        editorial = item.optJSONObject("editorial"),
                        editorialLevel = item.optString("editorial_level", "raw_feed"),
                        editorPickReason = item.optString("editor_pick_reason")
                    )
                )
            }
        }
    } finally {
        connection.disconnect()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PulseScreen() }
    }
}

@Composable
private fun PulseScreen() {
    var filter by remember { mutableStateOf("Tutto") }
    var language by remember { mutableStateOf("it") }
    var levelFilter by remember { mutableStateOf("all") }
    var liveStories by remember { mutableStateOf<List<Story>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun refreshFeed() {
        scope.launch {
            isLoading = true
            try {
                liveStories = loadFeed()
            } catch (_: Exception) {
                liveStories = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { refreshFeed() }

    val live = liveStories.isNotEmpty()
    val sourceStories = if (live) liveStories else demoStories
    val filters = listOf("Tutto", "News", "GitHub", "Speak Up")
    val visible = sourceStories.filter {
        (filter == "Tutto" ||
            (filter == "News" && it.type == "news") ||
            (filter == "GitHub" && it.type == "github") ||
            (filter == "Speak Up" && it.type == "speak_up")) &&
            (levelFilter == "all" || it.editorialLevel == levelFilter)
    }

    MaterialTheme(colorScheme = lightColorScheme(
        background = Paper, surface = Paper, onBackground = Ink, onSurface = Ink,
        primary = Terra, secondary = Sage
    )) {
        Scaffold(containerColor = Paper) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, null, tint = Terra, modifier = Modifier.size(30.dp))
                            Spacer(Modifier.width(9.dp))
                            Column {
                                Text("AI PULSE", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, color = Ink, letterSpacing = 1.sp)
                                Text("THE DAILY AI BRIEFING", fontSize = 9.sp, color = Muted, letterSpacing = 1.4.sp)
                            }
                        }
                        IconButton(onClick = { filter = "Tutto"; refreshFeed() }) { Icon(Icons.Default.Refresh, "Aggiorna notizie", tint = Terra) }
                    }
                }
                item {
                    Column(Modifier.fillMaxWidth().background(Warm, RoundedCornerShape(22.dp)).padding(20.dp)) {
                        Text("DAILY BRIEFING", color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Capire l'AI.\nUn giorno alla volta.", color = Ink, fontSize = 29.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Text("Notizie, idee e progetti che stanno muovendo il mondo dell'intelligenza artificiale.", color = Muted, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        filters.forEach { f ->
                            FilterChip(selected = filter == f, onClick = { filter = f }, label = { Text(f, fontSize = 11.sp) })
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("LINGUA", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("it" to "🇮🇹 IT", "en" to "🇬🇧 EN", "es" to "🇪🇸 ES", "zh-CN" to "🇨🇳 中文").forEach { (code, label) ->
                                FilterChip(selected = language == code, onClick = { language = code }, label = { Text(label, fontSize = 10.sp) })
                            }
                        }
                        Text("LIVELLO EDITORIALE", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("all" to "Tutti", "raw_feed" to "Raw", "ai_curated" to "Curated", "editors_pick" to "Pick").forEach { (code, label) ->
                                FilterChip(selected = levelFilter == code, onClick = { levelFilter = code }, label = { Text(label, fontSize = 10.sp) })
                            }
                        }
                    }
                }
                if (filter == "Speak Up") {
                    item {
                        Column(Modifier.fillMaxWidth().background(Warm, RoundedCornerShape(22.dp)).padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.GraphicEq, null, tint = Terra, modifier = Modifier.size(27.dp))
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("SPEAK UP", color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                                    Text("Le voci dell'AI", color = Ink, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Podcast e conversazioni selezionati per seguire ricerca, strumenti e idee che stanno cambiando l'intelligenza artificiale.", color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                listOf("🧠 Research", "🛠 Builders", "🚀 Frontier").forEach { label ->
                                    Surface(color = Color.White, shape = RoundedCornerShape(30.dp)) {
                                        Text(label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = Muted, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(7.dp).background(if (live) Sage else Terra, RoundedCornerShape(50)))
                        Spacer(Modifier.width(7.dp))
                        Text(
                            when {
                                live -> "FEED LIVE · GITHUB"
                                isLoading -> "CARICAMENTO DEL FEED"
                                else -> "ANTEPRIMA · CONTENUTI DIMOSTRATIVI"
                            },
                            color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp
                        )
                    }
                }
                items(visible, key = { it.id }) { story ->
                    Column(
                        Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(18.dp))
                            .clickable { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(story.url))) } }
                            .padding(17.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (story.type) {
                                "github" -> Icons.Default.Code
                                "speak_up" -> Icons.Default.GraphicEq
                                else -> Icons.Default.AutoAwesome
                            }
                            Icon(icon, null, tint = Terra, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(story.section, color = Terra, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        if (story.type == "speak_up") {
                            Text(story.source.uppercase() + if (story.duration.isNotBlank()) " · ${story.duration}" else "", color = Sage, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                            Spacer(Modifier.height(5.dp))
                        }
                        val localized = story.editorial?.optJSONObject(language) ?: story.editorial?.optJSONObject("it")
                        val curated = localized != null && story.editorialLevel != "raw_feed"
                        val displayTitle = if (curated) localized?.optString("title")?.takeIf { it.isNotBlank() } ?: story.title else story.title
                        val displaySummary = if (curated) localized?.optString("summary")?.takeIf { it.isNotBlank() } ?: story.summary else story.summary
                        val displayWhy = if (curated) localized?.optString("why_it_matters")?.takeIf { it.isNotBlank() } else null
                        Surface(color = if (story.editorialLevel == "editors_pick") Ink else Warm, shape = RoundedCornerShape(30.dp)) {
                            Row(Modifier.padding(horizontal = 9.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (story.editorialLevel == "editors_pick") Icons.Default.Star else Icons.Default.AutoAwesome, null, tint = if (story.editorialLevel == "editors_pick") Color(0xFFF2D69A) else Terra, modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(5.dp))
                                Text(when (story.editorialLevel) { "editors_pick" -> "EDITOR'S PICK"; "ai_curated" -> "AI CURATED"; else -> "RAW FEED" }, color = if (story.editorialLevel == "editors_pick") Color(0xFFF2D69A) else Terra, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                            }
                        }
                        Spacer(Modifier.height(9.dp))
                        Text(displayTitle, color = Ink, fontSize = 18.sp, lineHeight = 23.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(7.dp))
                        Text(displaySummary, color = Muted, fontSize = 13.sp, lineHeight = 19.sp, maxLines = 5, overflow = TextOverflow.Ellipsis)
                        if (!displayWhy.isNullOrBlank()) {
                            Spacer(Modifier.height(9.dp))
                            Text(if (language == "it") "PERCHÉ È INTERESSANTE" else "WHY IT MATTERS", color = Terra, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                            Text(displayWhy, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Fonte: ${story.source}  ·  Apri ↗", color = Terra, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
                if (visible.isEmpty()) {
                    item {
                        Text(
                            if (filter == "Speak Up") "Nessun episodio disponibile al momento. Il feed podcast viene aggiornato automaticamente." else "Nessun contenuto in questa sezione. Riprova più tardi.",
                            color = Muted, fontSize = 13.sp, lineHeight = 19.sp
                        )
                    }
                }
                if (!live && !isLoading) {
                    item {
                        Text("Feed temporaneamente non disponibile: mostriamo un'anteprima dimostrativa.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                    }
                }
            }
        }
    }
}
