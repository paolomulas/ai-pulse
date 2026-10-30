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

private val Paper = Color(0xFFFAF9F5)
private val Warm = Color(0xFFF0EEE6)
private val Ink = Color(0xFF25231F)
private val Muted = Color(0xFF777166)
private val Terra = Color(0xFFC6613F)
private val Sage = Color(0xFF788C5D)

private data class Story(val section: String, val title: String, val summary: String, val source: String, val url: String, val type: String)

private val stories = listOf(
    Story("MODELLI", "L'intelligenza artificiale entra in una nuova fase", "Aggiornamenti e analisi sulle nuove generazioni di modelli. I contenuti live saranno collegati alle fonti verificate.", "AI Pulse Editorial", "https://openai.com/news/", "News"),
    Story("OPEN SOURCE", "I repository AI da tenere d'occhio", "Progetti emergenti ordinati considerando attività recente, crescita e interesse della community.", "GitHub", "https://github.com/trending", "GitHub"),
    Story("RICERCA", "Ricerca e applicazioni: cosa cambia", "Una selezione di aggiornamenti da laboratori e gruppi di ricerca, con collegamento alle fonti originali.", "Google DeepMind", "https://deepmind.google/discover/blog/", "News"),
    Story("SPEAK UP · INTERVISTA", "Le idee che orientano il dibattito sull'AI", "La sezione raccoglierà podcast, interviste e conferenze recenti. Citazioni dirette solo con trascrizioni verificabili.", "Latent Space", "https://www.latent.space/", "Speak Up")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PulseScreen() }
    }
}

@Composable
private fun PulseScreen() {
    var filter by remember { mutableStateOf("Tutto") }
    val context = LocalContext.current
    val filters = listOf("Tutto", "News", "GitHub", "Speak Up")
    val visible = stories.filter { filter == "Tutto" || it.type == filter }
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
                        IconButton(onClick = { filter = "Tutto" }) { Icon(Icons.Default.Refresh, "Mostra tutto", tint = Terra) }
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(7.dp).background(Sage, RoundedCornerShape(50)))
                        Spacer(Modifier.width(7.dp))
                        Text("ANTEPRIMA · CONTENUTI DIMOSTRATIVI", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
                items(visible) { story ->
                    Column(
                        Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(18.dp))
                            .clickable { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(story.url))) } }
                            .padding(17.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (story.type) {
                                "GitHub" -> Icons.Default.Code
                                "Speak Up" -> Icons.Default.GraphicEq
                                else -> Icons.Default.AutoAwesome
                            }
                            Icon(icon, null, tint = Terra, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(story.section, color = Terra, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(story.title, color = Ink, fontSize = 18.sp, lineHeight = 23.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(7.dp))
                        Text(story.summary, color = Muted, fontSize = 13.sp, lineHeight = 19.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(12.dp))
                        Text("Fonte: ${story.source}  ·  Apri ↗", color = Terra, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
                item {
                    Text("I contenuti visualizzati sono segnaposto. Il collegamento a Firebase e ai collector verrà completato dopo la configurazione delle credenziali del proprietario.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                }
            }
        }
    }
}
