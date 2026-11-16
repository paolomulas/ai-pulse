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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.time.LocalDate
import java.time.format.TextStyle

private val Paper = Color(0xFFFAF9F5)
private val Warm = Color(0xFFF0EEE6)
private val Ink = Color(0xFF25231F)
private val Muted = Color(0xFF777166)
private val Terra = Color(0xFFC6613F)
private val Sage = Color(0xFF788C5D)

private object I18n {
    private val values = mapOf(
        "it" to mapOf(
            "nav_briefing" to "Briefing", "nav_radar" to "Radar", "nav_speak_up" to "Speak Up", "nav_my_pulse" to "My Pulse",
            "tagline" to "INTELLIGENCE FOR THE AUTONOMOUS ERA", "refresh" to "Aggiorna",
            "daily_briefing" to "IL BRIEFING QUOTIDIANO", "briefing_title" to "Intelligenza, senza rumore.",
            "briefing_subtitle" to "Una selezione ragionata delle idee e dei sistemi che stanno definendo l'era autonoma.",
            "exclusive" to "ESCLUSIVA", "editors_pick" to "SCELTA DELLA REDAZIONE", "featured_analysis" to "ANALISI IN EVIDENZA",
            "why_it_matters" to "PERCHÉ CONTA", "source" to "Fonte:", "listen" to "Ascolta",
            "loading" to "Caricamento delle fonti originali…", "no_stories" to "Nessuna storia corrisponde alle tue preferenze. Aggiorna My Pulse o riprova.",
            "radar_kicker" to "RADAR", "radar_title" to "Dove si muove l'AI", "radar_subtitle" to "Temi, persone e segnali da tenere d'occhio.",
             "radar_agents" to "AGENTI AUTONOMI", "radar_agents_body" to "I sistemi che imparano a pianificare, usare strumenti e agire nel mondo.",
             "radar_models" to "MODELLI DI FRONTIERA", "radar_models_body" to "La corsa ai modelli più capaci, efficienti e aperti.",
             "radar_creator" to "SIMONE RIZZO · RADAR DEI CREATOR", "radar_creator_body" to "Voci indipendenti che spiegano cosa sta succedendo dietro il prossimo rilascio.",
             "top_repos" to "TOP REPOSITORIES · GIT RADAR", "open_github" to "Apri su GitHub ↗", "open_episode" to "Ascolta sul web ↗",
             "radar_tab_signals" to "Segnali & Creator", "radar_tab_repos" to "Pop Git",
             "scouted_badge" to "SCOUTED VIA RADAR · SEGNALE DI FRONTIERA", "primary_source_label" to "Fonte primaria:",
             "read_more" to "Leggi approfondimento ↗", "radar_frontier" to "CREATOR RADAR & ANALISI DI FRONTIERA",
            "speak_kicker" to "SPEAK UP", "speak_title" to "Le voci dell'AI", "speak_subtitle" to "Briefing audio e conversazioni per ascoltare la frontiera.",
            "play_episode" to "Riproduci episodio", "my_pulse_kicker" to "MY PULSE", "my_pulse_title" to "Personalizza il tuo Briefing",
            "my_pulse_subtitle" to "Scegli cosa vuoi capire ogni mattina.", "topics" to "I TUOI ARGOMENTI",
            "briefing_language" to "LINGUA DEL BRIEFING", "editorial_level" to "LIVELLO EDITORIALE",
            "curated_only" to "Solo sintesi curate", "all_news" to "Tutte le notizie", "sync_title" to "Sincronizza il tuo Pulse",
            "sync_body" to "Accedi con Google per sincronizzare le tue preferenze e ricevere il Briefing mattutino",
            "sign_in_google" to "Accedi con Google", "synced_account" to "Account sincronizzato", "save" to "Salva Preferenze",
            "saved" to "Preferenze salvate", "footer_brand" to "PULSE AI · INDEPENDENT AI JOURNAL",
            "footer_sources" to "Contenuti e collegamenti rimandano sempre alle fonti originali", "privacy" to "Privacy Policy", "contact" to "Contatti",
            "refresh_content" to "Aggiorna", "topic_agents" to "Agenti Autonomi", "topic_llm" to "LLM & Modelli",
             "topic_open_source" to "Open Source", "topic_research" to "Ricerca & Sviluppo", "topic_chips" to "Chip & Robotica", "topic_business" to "Business AI",
             "morning_edition" to "EDIZIONE MATTINA", "top_story" to "STORIA IN EVIDENZA", "key_takeaways" to "PUNTI CHIAVE", "share" to "Condividi", "open_source_link" to "Apri fonte originale ↗", "read_time" to "MIN DI LETTURA"
             ,"git_filter_all" to "Tutti", "git_filter_weekly" to "⚡ Trending Settimana", "git_filter_monthly" to "🌟 Top Mese"
        ),
        "en" to mapOf(
            "nav_briefing" to "Briefing", "nav_radar" to "Radar", "nav_speak_up" to "Speak Up", "nav_my_pulse" to "My Pulse",
            "tagline" to "INTELLIGENCE FOR THE AUTONOMOUS ERA", "refresh" to "Refresh", "daily_briefing" to "THE DAILY BRIEFING",
            "briefing_title" to "Intelligence, without the noise.", "briefing_subtitle" to "A considered selection of the ideas and systems defining the autonomous era.",
            "exclusive" to "EXCLUSIVE", "editors_pick" to "EDITOR'S PICK", "featured_analysis" to "FEATURED ANALYSIS",
            "why_it_matters" to "WHY IT MATTERS", "source" to "Source:", "listen" to "Listen", "loading" to "Loading original sources…",
            "no_stories" to "No story matches your preferences. Update My Pulse or try again.", "radar_kicker" to "RADAR",
            "radar_title" to "Where AI is moving", "radar_subtitle" to "Topics, people and signals to watch.",
             "radar_agents" to "AUTONOMOUS AGENTS", "radar_agents_body" to "Systems learning to plan, use tools and act in the world.",
             "radar_models" to "FRONTIER MODELS", "radar_models_body" to "The race for more capable, efficient and open models.",
             "radar_creator" to "SIMONE RIZZO · CREATOR RADAR", "radar_creator_body" to "Independent voices explaining what is happening behind the next release.",
              "top_repos" to "TOP REPOSITORIES · GIT RADAR", "open_github" to "Open on GitHub ↗", "open_episode" to "Listen on web ↗",
              "radar_tab_signals" to "Signals & Creators", "radar_tab_repos" to "Pop Git",
              "scouted_badge" to "SCOUTED VIA RADAR · FRONTIER SIGNAL", "primary_source_label" to "Primary source:",
             "read_more" to "Read more ↗", "radar_frontier" to "CREATOR RADAR & FRONTIER ANALYSIS",
            "speak_kicker" to "SPEAK UP", "speak_title" to "The voices of AI", "speak_subtitle" to "Audio briefings and conversations from the frontier.",
            "play_episode" to "Play episode", "my_pulse_kicker" to "MY PULSE", "my_pulse_title" to "Customize your Briefing",
            "my_pulse_subtitle" to "Choose what you want to understand every morning.", "topics" to "YOUR TOPICS",
            "briefing_language" to "BRIEFING LANGUAGE", "editorial_level" to "EDITORIAL LEVEL", "curated_only" to "Curated summaries only",
            "all_news" to "All news", "sync_title" to "Sync your Pulse", "sync_body" to "Sign in with Google to sync your preferences and receive the morning Briefing",
            "sign_in_google" to "Sign in with Google", "synced_account" to "Account synced", "save" to "Save Preferences",
            "saved" to "Preferences saved", "footer_brand" to "PULSE AI · INDEPENDENT AI JOURNAL",
            "footer_sources" to "Content and links always point to the original sources", "privacy" to "Privacy Policy", "contact" to "Contact",
            "refresh_content" to "Refresh", "topic_agents" to "Autonomous Agents", "topic_llm" to "LLM & Models",
             "topic_open_source" to "Open Source", "topic_research" to "Research & Development", "topic_chips" to "Chips & Robotics", "topic_business" to "Business AI",
              "morning_edition" to "MORNING EDITION", "top_story" to "TOP STORY", "key_takeaways" to "KEY TAKEAWAYS", "share" to "Share", "open_source_link" to "Open original source ↗", "read_time" to "MIN READ"
              ,"git_filter_all" to "All", "git_filter_weekly" to "⚡ Weekly Trending", "git_filter_monthly" to "🌟 Monthly Top"
        ),
        "es" to mapOf(
            "nav_briefing" to "Briefing", "nav_radar" to "Radar", "nav_speak_up" to "Speak Up", "nav_my_pulse" to "Mi Pulse", "tagline" to "INTELIGENCIA PARA LA ERA AUTÓNOMA",
            "refresh" to "Actualizar", "daily_briefing" to "EL BRIEFING DIARIO", "briefing_title" to "Inteligencia, sin ruido.",
            "briefing_subtitle" to "Una selección razonada de las ideas y sistemas que definen la era autónoma.", "exclusive" to "EXCLUSIVA",
            "editors_pick" to "SELECCIÓN EDITORIAL", "featured_analysis" to "ANÁLISIS DESTACADO", "why_it_matters" to "POR QUÉ IMPORTA",
            "source" to "Fuente:", "listen" to "Escuchar", "loading" to "Cargando fuentes originales…", "no_stories" to "Ninguna historia coincide con tus preferencias. Actualiza Mi Pulse o inténtalo de nuevo.",
            "radar_kicker" to "RADAR", "radar_title" to "Dónde se mueve la IA", "radar_subtitle" to "Temas, personas y señales que conviene observar.",
             "radar_agents" to "AGENTES AUTÓNOMOS", "radar_agents_body" to "Sistemas que aprenden a planificar, usar herramientas y actuar en el mundo.",
             "radar_models" to "MODELOS DE VANGUARDIA", "radar_models_body" to "La carrera por modelos más capaces, eficientes y abiertos.",
             "radar_creator" to "SIMONE RIZZO · RADAR DE CREADORES", "radar_creator_body" to "Voces independientes que explican qué ocurre detrás del próximo lanzamiento.",
              "top_repos" to "TOP REPOSITORIOS · RADAR GIT", "open_github" to "Abrir en GitHub ↗", "open_episode" to "Escuchar en web ↗",
              "radar_tab_signals" to "Señales y Creadores", "radar_tab_repos" to "Pop Git",
              "scouted_badge" to "SCOUTED VIA RADAR · SEÑAL DE VANGUARDIA", "primary_source_label" to "Fuente primaria:",
             "read_more" to "Leer análisis ↗", "radar_frontier" to "RADAR DE CREADORES Y ANÁLISIS DE VANGUARDIA",
            "speak_kicker" to "SPEAK UP", "speak_title" to "Las voces de la IA", "speak_subtitle" to "Briefings de audio y conversaciones para escuchar la frontera.",
            "play_episode" to "Reproducir episodio", "my_pulse_kicker" to "MI PULSE", "my_pulse_title" to "Personaliza tu Briefing", "my_pulse_subtitle" to "Elige qué quieres entender cada mañana.",
            "topics" to "TUS TEMAS", "briefing_language" to "IDIOMA DEL BRIEFING", "editorial_level" to "NIVEL EDITORIAL", "curated_only" to "Solo resúmenes seleccionados",
            "all_news" to "Todas las noticias", "sync_title" to "Sincroniza tu Pulse", "sync_body" to "Accede con Google para sincronizar tus preferencias y recibir el Briefing matutino",
            "sign_in_google" to "Acceder con Google", "synced_account" to "Cuenta sincronizada", "save" to "Guardar Preferencias", "saved" to "Preferencias guardadas",
            "footer_brand" to "PULSE AI · REVISTA INDEPENDIENTE DE IA", "footer_sources" to "El contenido y los enlaces siempre remiten a las fuentes originales", "privacy" to "Política de privacidad", "contact" to "Contacto",
             "refresh_content" to "Actualizar", "topic_agents" to "Agentes Autónomos", "topic_llm" to "LLM y Modelos", "topic_open_source" to "Código Abierto", "topic_research" to "Investigación y Desarrollo", "topic_chips" to "Chips y Robótica", "topic_business" to "Negocios de IA",
              "morning_edition" to "EDICIÓN DE LA MAÑANA", "top_story" to "HISTORIA DESTACADA", "key_takeaways" to "PUNTOS CLAVE", "share" to "Compartir", "open_source_link" to "Abrir fuente original ↗", "read_time" to "MIN DE LECTURA"
              ,"git_filter_all" to "Todos", "git_filter_weekly" to "⚡ Tendencias Semana", "git_filter_monthly" to "🌟 Top Mes"
        ),
        "zh" to mapOf(
            "nav_briefing" to "简报", "nav_radar" to "雷达", "nav_speak_up" to "AI之声", "nav_my_pulse" to "我的 Pulse", "tagline" to "自主时代的智能",
            "refresh" to "刷新", "daily_briefing" to "每日简报", "briefing_title" to "智能，摒除噪音。", "briefing_subtitle" to "精选定义自主时代的思想与系统。",
            "exclusive" to "独家", "editors_pick" to "编辑精选", "featured_analysis" to "重点分析", "why_it_matters" to "核心看点", "source" to "来源:", "listen" to "收听",
            "loading" to "正在加载原始来源…", "no_stories" to "没有符合您偏好的故事。请更新“我的 Pulse”或重试。", "radar_kicker" to "雷达", "radar_title" to "AI 正在何处发展", "radar_subtitle" to "值得关注的主题、人物与信号。",
             "radar_agents" to "自主智能体", "radar_agents_body" to "学习规划、使用工具并在现实世界中行动的系统。", "radar_models" to "前沿模型", "radar_models_body" to "更强大、高效且开放的模型竞赛。",
             "radar_creator" to "SIMONE RIZZO · 创作者雷达", "radar_creator_body" to "独立声音，解读下一次发布背后的动态。", "speak_kicker" to "AI之声", "speak_title" to "AI 的声音", "speak_subtitle" to "聆听前沿的音频简报与对话。",
              "top_repos" to "热门开源仓库 · GIT雷达", "open_github" to "在GitHub打开 ↗", "open_episode" to "网页收听 ↗",
              "radar_tab_signals" to "信号与创作者", "radar_tab_repos" to "Pop Git (热门榜)",
              "scouted_badge" to "SCOUTED VIA RADAR · 前沿信号", "primary_source_label" to "原始来源:",
             "read_more" to "阅读详情 ↗", "radar_frontier" to "创作者雷达与前沿分析",
            "play_episode" to "播放剧集", "my_pulse_kicker" to "我的 PULSE", "my_pulse_title" to "定制您的简报", "my_pulse_subtitle" to "选择您每天早上想了解的内容。",
            "topics" to "您的主题", "briefing_language" to "简报语言", "editorial_level" to "编辑级别", "curated_only" to "仅精选摘要", "all_news" to "所有新闻",
            "sync_title" to "同步您的 Pulse", "sync_body" to "使用 Google 登录，同步您的偏好并接收每日早报", "sign_in_google" to "使用 Google 登录", "synced_account" to "账号已同步", "save" to "保存偏好设置", "saved" to "偏好设置已保存",
            "footer_brand" to "PULSE AI · 独立人工智能杂志", "footer_sources" to "内容和链接始终指向原始来源", "privacy" to "隐私政策", "contact" to "联系我们", "refresh_content" to "刷新",
             "topic_agents" to "自主智能体", "topic_llm" to "大语言模型", "topic_open_source" to "开源", "topic_research" to "研发", "topic_chips" to "芯片与机器人", "topic_business" to "AI 商业",
              "morning_edition" to "早间版", "top_story" to "重点故事", "key_takeaways" to "关键要点", "share" to "分享", "open_source_link" to "打开原始来源 ↗", "read_time" to "分钟阅读"
              ,"git_filter_all" to "全部", "git_filter_weekly" to "⚡ 本周热门", "git_filter_monthly" to "🌟 本月精选"
        ),
        "de" to mapOf(
            "nav_briefing" to "Briefing", "nav_radar" to "Radar", "nav_speak_up" to "Speak Up", "nav_my_pulse" to "Mein Pulse", "tagline" to "INTELLIGENZ FÜR DAS AUTONOME ZEITALTER",
            "refresh" to "Aktualisieren", "daily_briefing" to "DAS TÄGLICHE BRIEFING", "briefing_title" to "Intelligenz, ohne Rauschen.", "briefing_subtitle" to "Eine kuratierte Auswahl der Ideen und Systeme, die das autonome Zeitalter prägen.",
            "exclusive" to "EXKLUSIV", "editors_pick" to "REDAKTIONSAUSWAHL", "featured_analysis" to "HERVORGEHOBENE ANALYSE", "why_it_matters" to "WARUM ES WICHTIG IST", "source" to "Quelle:", "listen" to "Anhören",
            "loading" to "Originalquellen werden geladen…", "no_stories" to "Keine Geschichte passt zu Ihren Präferenzen. Aktualisieren Sie My Pulse oder versuchen Sie es erneut.", "radar_kicker" to "RADAR", "radar_title" to "Wo sich die KI bewegt", "radar_subtitle" to "Themen, Personen und Signale, die Sie im Blick behalten sollten.",
             "radar_agents" to "AUTONOME AGENTEN", "radar_agents_body" to "Systeme, die lernen zu planen, Werkzeuge zu nutzen und in der Welt zu handeln.", "radar_models" to "GRENZMODELLE", "radar_models_body" to "Der Wettlauf um leistungsfähigere, effizientere und offene Modelle.",
             "radar_creator" to "SIMONE RIZZO · CREATOR-RADAR", "radar_creator_body" to "Unabhängige Stimmen erklären, was hinter der nächsten Veröffentlichung passiert.", "speak_kicker" to "SPEAK UP", "speak_title" to "Die Stimmen der KI", "speak_subtitle" to "Audio-Briefings und Gespräche von der Grenze des Machbaren.",
              "top_repos" to "TOP REPOSITORIES · GIT RADAR", "open_github" to "Auf GitHub öffnen ↗", "open_episode" to "Im Web anhören ↗",
              "radar_tab_signals" to "Signale & Creator", "radar_tab_repos" to "Pop Git",
              "scouted_badge" to "SCOUTED VIA RADAR · GRENZSIGNAL", "primary_source_label" to "Primärquelle:",
             "read_more" to "Mehr lesen ↗", "radar_frontier" to "CREATOR-RADAR & GRENZANALYSE",
            "play_episode" to "Episode abspielen", "my_pulse_kicker" to "MEIN PULSE", "my_pulse_title" to "Passen Sie Ihr Briefing an", "my_pulse_subtitle" to "Wählen Sie, was Sie jeden Morgen verstehen möchten.",
            "topics" to "IHRE THEMEN", "briefing_language" to "BRIEFING-SPRACHE", "editorial_level" to "REDAKTIONELLES NIVEAU", "curated_only" to "Nur kuratierte Zusammenfassungen", "all_news" to "Alle Nachrichten",
            "sync_title" to "Synchronisieren Sie Ihren Pulse", "sync_body" to "Melden Sie sich mit Google an, um Ihre Präferenzen zu synchronisieren und das morgendliche Briefing zu erhalten", "sign_in_google" to "Mit Google anmelden", "synced_account" to "Konto synchronisiert", "save" to "Einstellungen speichern", "saved" to "Einstellungen gespeichert",
            "footer_brand" to "PULSE AI · UNABHÄNGIGES KI-JOURNAL", "footer_sources" to "Inhalte und Links verweisen immer auf die Originalquellen", "privacy" to "Datenschutzrichtlinie", "contact" to "Kontakt", "refresh_content" to "Aktualisieren",
             "topic_agents" to "Autonome Agenten", "topic_llm" to "LLMs & Modelle", "topic_open_source" to "Open Source", "topic_research" to "Forschung & Entwicklung", "topic_chips" to "Chips & Robotik", "topic_business" to "KI-Geschäft",
              "morning_edition" to "MORGENAUSGABE", "top_story" to "TOP-STORY", "key_takeaways" to "DIE WICHTIGSTEN PUNKTE", "share" to "Teilen", "open_source_link" to "Originalquelle öffnen ↗", "read_time" to "MIN LESEZEIT"
              ,"git_filter_all" to "Alle", "git_filter_weekly" to "⚡ Trending Woche", "git_filter_monthly" to "🌟 Top Monat"
        )
    )

    fun tr(key: String, lang: String): String = values[lang]?.get(key) ?: values["it"]?.get(key) ?: key
    fun topic(topic: String, lang: String): String = tr(
        when (topic) {
            "Agenti Autonomi" -> "topic_agents"; "LLM & Modelli" -> "topic_llm"; "Open Source" -> "topic_open_source"
            "Ricerca & Sviluppo" -> "topic_research"; "Chip & Robotica" -> "topic_chips"; else -> "topic_business"
        }, lang
    )
    fun languageName(code: String, lang: String): String = mapOf(
        "it" to mapOf("it" to "Italiano", "en" to "Italian", "es" to "Italiano", "zh" to "意大利语", "de" to "Italienisch"),
        "en" to mapOf("it" to "Inglese", "en" to "English", "es" to "Inglés", "zh" to "英语", "de" to "Englisch"),
        "es" to mapOf("it" to "Spagnolo", "en" to "Spanish", "es" to "Español", "zh" to "西班牙语", "de" to "Spanisch"),
        "zh" to mapOf("it" to "Cinese", "en" to "Chinese", "es" to "Chino", "zh" to "中文", "de" to "Chinesisch"),
        "de" to mapOf("it" to "Tedesco", "en" to "German", "es" to "Alemán", "zh" to "德语", "de" to "Deutsch")
    )[code]?.get(lang) ?: code
    fun editorial(story: Story, lang: String): JSONObject? = story.editorial?.optJSONObject(if (lang == "zh") "zh-CN" else lang)
        ?: story.editorial?.optJSONObject(lang) ?: story.editorial?.optJSONObject("en") ?: story.editorial?.optJSONObject("it")
}

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
    val stars: Int = 0,
    val ttsAudioUrl: String = "",
    val ttsAudioByLanguage: JSONObject? = null,
    val editorial: JSONObject? = null,
    val editorialLevel: String = "raw_feed",
    val editorPickReason: String = "",
     val relevanceScore: Int = 0,
     val hypeVelocity: Double? = null,
     val hypeScore: Int = 0,
     val hypeLabel: String = "",
     val period: String = "",
     val keyTakeaways: List<String> = emptyList(),
    val extendedAnalysis: String = ""
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

private fun editorialLanguageObject(editorial: JSONObject?, language: String): JSONObject? = editorial?.optJSONObject(language)
    ?: editorial?.optJSONObject(if (language == "zh") "zh-CN" else language)
    ?: editorial?.optJSONObject("en")
    ?: editorial?.optJSONObject("it")

private fun takeawaysFrom(editorial: JSONObject?, language: String): List<String> {
    val values = editorialLanguageObject(editorial, language)?.optJSONArray("key_takeaways") ?: return emptyList()
    return buildList { for (index in 0 until values.length()) values.optString(index).takeIf { it.isNotBlank() }?.let(::add) }
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
                val editorial = item.optJSONObject("editorial")
                val editorialLanguage = editorialLanguageObject(editorial, "it")
                val takeaways = editorialLanguage?.optJSONArray("key_takeaways")?.let { values ->
                    buildList { for (takeawayIndex in 0 until values.length()) values.optString(takeawayIndex).takeIf { it.isNotBlank() }?.let(::add) }
                } ?: emptyList()
                add(Story(
                    id = item.optString("id", "$index-$title"),
                    section = section,
                    title = title,
                    summary = item.optString("summary"),
                    source = item.optString("sourceName", "Fonte"),
                    url = url,
                    type = type,
                    duration = item.optString("duration"),
                    language = item.optString("language"),
                     audioUrl = item.optString("audioUrl"),
                      stars = item.optInt("stars", 0),
                     hypeVelocity = item.optDouble("hype_velocity", Double.NaN).takeIf { !it.isNaN() },
                     hypeScore = item.optInt("hype_score", 0),
                     hypeLabel = item.optString("hype_label"),
                     period = item.optString("period"),
                     ttsAudioUrl = item.optString("ttsAudioUrl"),
                    ttsAudioByLanguage = item.optJSONObject("ttsAudioByLanguage"),
                     editorial = editorial,
                     editorialLevel = item.optString("editorial_level", "raw_feed"),
                     editorPickReason = item.optString("editor_pick_reason"),
                     relevanceScore = item.optInt("relevance_score", item.optInt("impact_score", 0)),
                     keyTakeaways = takeaways,
                     extendedAnalysis = editorialLanguage?.optString("extended_analysis", "") ?: ""
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

private fun editionKicker(language: String): String {
    val locale = Locale.forLanguageTag(if (language == "zh") "zh-CN" else language)
    val day = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, locale).uppercase(locale)
    return "$day ${LocalDate.now().dayOfMonth} · ${I18n.tr("morning_edition", language)}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PulseScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) }
    var preferences by remember { mutableStateOf(loadPreferences(context)) }
    var stories by remember { mutableStateOf<List<Story>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var readerStory by remember { mutableStateOf<Story?>(null) }

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
                Column {
                    HorizontalDivider(color = Warm, thickness = 1.dp)
                    NavigationBar(
                        containerColor = Paper.copy(alpha = 0.94f),
                        tonalElevation = 0.dp,
                        windowInsets = androidx.compose.material3.NavigationBarDefaults.windowInsets
                    ) {
                        listOf(
                            I18n.tr("nav_briefing", preferences.language) to Icons.Default.AutoAwesome,
                            I18n.tr("nav_radar", preferences.language) to Icons.Default.Sensors,
                            I18n.tr("nav_speak_up", preferences.language) to Icons.Default.GraphicEq,
                            I18n.tr("nav_my_pulse", preferences.language) to Icons.Default.Tune
                        ).forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                icon = { Icon(item.second, contentDescription = item.first) },
                                label = { Text(item.first, fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Terra,
                                    selectedTextColor = Terra,
                                    indicatorColor = Color(0xFFE8E4DA),
                                    unselectedIconColor = Color(0xFF4A453E),
                                    unselectedTextColor = Color(0xFF4A453E)
                                )
                            )
                        }
                    }
                }
            }
        ) { padding ->
            when (selectedTab) {
                0 -> BriefingScreen(stories, preferences, loading, ::refresh, padding) { readerStory = it }
                1 -> RadarScreen(stories, preferences.language, padding)
                2 -> SpeakUpScreen(stories, preferences.language, padding)
                else -> MyPulseScreen(preferences, { newPreferences ->
                    preferences = newPreferences
                    savePreferences(context, newPreferences)
                    Toast.makeText(context, I18n.tr("saved", newPreferences.language), Toast.LENGTH_SHORT).show()
                }, padding)
            }
        }
        readerStory?.let { story ->
            ModalBottomSheet(onDismissRequest = { readerStory = null }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = Paper) {
                ReaderView(story, preferences.language, onClose = { readerStory = null })
            }
        }
    }
}

@Composable
private fun ScreenColumn(padding: PaddingValues, content: @Composable (Modifier) -> Unit) {
    content(Modifier.fillMaxSize().padding(padding))
}

@Composable
private fun Masthead(language: String, onRefresh: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(editionKicker(language), color = Terra, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
            Spacer(Modifier.height(4.dp))
            Text("PULSE AI", fontFamily = FontFamily.Serif, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text(I18n.tr("tagline", language), color = Muted, fontSize = 9.sp, letterSpacing = 1.2.sp)
        }
        IconButton(onClick = onRefresh) { Icon(Icons.Default.Refresh, I18n.tr("refresh", language), tint = Terra) }
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
    padding: PaddingValues,
    onOpenReader: (Story) -> Unit
) {
    val context = LocalContext.current
    val visible = stories.filter { matchesTopic(it, preferences.topics) && (!preferences.curatedOnly || it.editorialLevel != "raw_feed") }
    val hero = visible.firstOrNull()
    ScreenColumn(padding) { modifier ->
        LazyColumn(modifier, contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { Masthead(preferences.language, refresh) }
            item {
                Text(I18n.tr("daily_briefing", preferences.language), color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
                Spacer(Modifier.height(7.dp))
                Text(I18n.tr("briefing_title", preferences.language), fontFamily = FontFamily.Serif, fontSize = 32.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(I18n.tr("briefing_subtitle", preferences.language), color = Muted, fontSize = 14.sp, lineHeight = 20.sp)
            }
            if (hero != null) {
                item { StoryCard(hero, preferences.language, true, onOpenReader) }
                items(visible.drop(1), key = { it.id }) { story -> StoryCard(story, preferences.language, false, onOpenReader) }
            } else {
                item {
                    Text(
                        if (loading) I18n.tr("loading", preferences.language) else I18n.tr("no_stories", preferences.language),
                        color = Muted, fontSize = 14.sp, lineHeight = 20.sp
                    )
                }
            }
            item { Footer(context, preferences.language) }
        }
    }
}

private enum class VisualTopic {
    FRONTIER_MODELS,
    OPEN_SOURCE,
    HARDWARE_COMPUTE,
    AUTONOMOUS_AGENTS,
    AUDIO_VOICE
}

private fun detectVisualTopic(story: Story): VisualTopic {
    val text = "${story.title} ${story.summary} ${story.section} ${story.type}".lowercase()
    return when {
        story.type == "speak_up" || listOf("podcast", "audio", "voice", "speech", "ascolta", "intervista").any(text::contains) ->
            VisualTopic.AUDIO_VOICE
        story.type == "github" || listOf("github", "open source", "opensource", "repo", "repository", "framework", "library").any(text::contains) ->
            VisualTopic.OPEN_SOURCE
        listOf("chip", "robot", "nvidia", "hardware", "semiconductor", "compute", "gpu", "tpu", "quantum", "silicon").any(text::contains) ->
            VisualTopic.HARDWARE_COMPUTE
        listOf("agent", "autonom", "agentic", "tool use", "planning", "workflow", "multi-agent").any(text::contains) ->
            VisualTopic.AUTONOMOUS_AGENTS
        else ->
            VisualTopic.FRONTIER_MODELS
    }
}

@Composable
private fun AbstractCoverBanner(story: Story, isHero: Boolean, modifier: Modifier = Modifier) {
    val topic = detectVisualTopic(story)
    val hash = story.id.fold(0) { result, character -> result * 31 + character.code }
    val norm = ((hash ushr 4) and 0xFFFF) / 65535f

    Canvas(modifier.fillMaxWidth().height(if (isHero) 148.dp else 92.dp)) {
        val w = size.width
        val h = size.height

        when (topic) {
            VisualTopic.FRONTIER_MODELS -> {
                val base = listOf(Color(0xFF141312), Color(0xFF2E1A14), Color(0xFF141312))
                drawRect(Brush.linearGradient(base, start = androidx.compose.ui.geometry.Offset.Zero, end = androidx.compose.ui.geometry.Offset(w, h)))
                val cx1 = w * (0.35f + norm * 0.3f)
                val cy1 = h * 0.25f
                drawCircle(Brush.radialGradient(listOf(Color(0xFFC6613F).copy(alpha = 0.85f), Color(0xFFE28743).copy(alpha = 0.35f), Color.Transparent)), radius = h * 0.95f, center = androidx.compose.ui.geometry.Offset(cx1, cy1))
                val cx2 = w * (0.75f - norm * 0.25f)
                val cy2 = h * 0.8f
                drawCircle(Brush.radialGradient(listOf(Color(0xFFD4AF37).copy(alpha = 0.45f), Color.Transparent)), radius = h * 0.7f, center = androidx.compose.ui.geometry.Offset(cx2, cy2))
                val path = Path().apply {
                    moveTo(0f, h * (0.75f - norm * 0.2f))
                    cubicTo(w * 0.35f, h * 0.1f, w * 0.65f, h * 1.1f, w, h * (0.4f + norm * 0.2f))
                }
                drawPath(path, color = Color(0xFFFAF9F5).copy(alpha = 0.22f), style = Stroke(width = 2.dp.toPx()))
            }
            VisualTopic.OPEN_SOURCE -> {
                val base = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF0B132B))
                drawRect(Brush.linearGradient(base, start = androidx.compose.ui.geometry.Offset.Zero, end = androidx.compose.ui.geometry.Offset(w, h)))
                val cx = w * (0.5f + norm * 0.2f)
                drawCircle(Brush.radialGradient(listOf(Color(0xFF06B6D4).copy(alpha = 0.75f), Color(0xFF3B82F6).copy(alpha = 0.25f), Color.Transparent)), radius = h * 1.1f, center = androidx.compose.ui.geometry.Offset(cx, h * 0.5f))
                for (i in 0..4) {
                    val step = (i + norm) * (w / 4f)
                    drawLine(Color(0xFF38BDF8).copy(alpha = 0.18f), androidx.compose.ui.geometry.Offset(step % w, 0f), androidx.compose.ui.geometry.Offset((step % w) + w * 0.35f, h), strokeWidth = 1.5.dp.toPx())
                }
            }
            VisualTopic.HARDWARE_COMPUTE -> {
                val base = listOf(Color(0xFF1C1917), Color(0xFF292524), Color(0xFF18181B))
                drawRect(Brush.linearGradient(base, start = androidx.compose.ui.geometry.Offset.Zero, end = androidx.compose.ui.geometry.Offset(w, h)))
                val cx = w * (0.6f - norm * 0.3f)
                drawCircle(Brush.radialGradient(listOf(Color(0xFFEA580C).copy(alpha = 0.8f), Color(0xFFF97316).copy(alpha = 0.3f), Color.Transparent)), radius = h * 0.9f, center = androidx.compose.ui.geometry.Offset(cx, h * 0.45f))
                val path = Path().apply {
                    moveTo(0f, h * 0.3f)
                    lineTo(w * 0.35f, h * 0.3f)
                    lineTo(w * 0.55f, h * 0.75f)
                    lineTo(w, h * 0.75f)
                }
                drawPath(path, color = Color(0xFFFDBA74).copy(alpha = 0.35f), style = Stroke(width = 2.dp.toPx()))
            }
            VisualTopic.AUTONOMOUS_AGENTS -> {
                val base = listOf(Color(0xFF27130B), Color(0xFF451A03), Color(0xFF1C0D07))
                drawRect(Brush.linearGradient(base, start = androidx.compose.ui.geometry.Offset.Zero, end = androidx.compose.ui.geometry.Offset(w, h)))
                val cx = w * (0.4f + norm * 0.25f)
                drawCircle(Brush.radialGradient(listOf(Color(0xFFD97706).copy(alpha = 0.85f), Color(0xFFF59E0B).copy(alpha = 0.3f), Color.Transparent)), radius = h * 0.95f, center = androidx.compose.ui.geometry.Offset(cx, h * 0.4f))
                drawCircle(Color(0xFFFEF3C7).copy(alpha = 0.2f), radius = h * 0.55f, center = androidx.compose.ui.geometry.Offset(cx, h * 0.4f), style = Stroke(width = 1.5.dp.toPx()))
                drawCircle(Color(0xFFFEF3C7).copy(alpha = 0.12f), radius = h * 0.85f, center = androidx.compose.ui.geometry.Offset(cx, h * 0.4f), style = Stroke(width = 1.dp.toPx()))
            }
            VisualTopic.AUDIO_VOICE -> {
                val base = listOf(Color(0xFF2E1065), Color(0xFF4A044E), Color(0xFF1E0836))
                drawRect(Brush.linearGradient(base, start = androidx.compose.ui.geometry.Offset.Zero, end = androidx.compose.ui.geometry.Offset(w, h)))
                drawCircle(Brush.radialGradient(listOf(Color(0xFFEC4899).copy(alpha = 0.8f), Color(0xFFA855F7).copy(alpha = 0.3f), Color.Transparent)), radius = h * 1.0f, center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.3f))
                val barCount = 18
                val barWidth = w / (barCount * 1.8f)
                for (i in 0 until barCount) {
                    val barNorm = kotlin.math.sin((i.toDouble() + norm * 5.0) * 0.6).toFloat().let { kotlin.math.abs(it) }
                    val barHeight = h * (0.15f + barNorm * 0.55f)
                    val bx = (i * 1.8f) * barWidth + barWidth * 0.5f
                    drawLine(Color(0xFFF472B6).copy(alpha = 0.35f + barNorm * 0.35f), androidx.compose.ui.geometry.Offset(bx, h * 0.9f - barHeight), androidx.compose.ui.geometry.Offset(bx, h * 0.9f), strokeWidth = barWidth * 0.7f)
                }
            }
        }
    }
}

@Composable
private fun StoryCard(story: Story, language: String, hero: Boolean, onOpenReader: (Story) -> Unit) {
    val context = LocalContext.current
    val localized = I18n.editorial(story, language)
    val curated = localized != null && story.editorialLevel != "raw_feed"
    val title = if (curated) localized?.optString("title")?.takeIf { it.isNotBlank() } ?: story.title else story.title
    val summary = if (curated) localized?.optString("summary")?.takeIf { it.isNotBlank() } ?: story.summary else story.summary
    val why = if (curated) localized?.optString("why_it_matters")?.takeIf { it.isNotBlank() } else null
    val takeaways = localized?.let { takeawaysFrom(story.editorial, language) }?.ifEmpty { story.keyTakeaways } ?: story.keyTakeaways
    val extended = localized?.optString("extended_analysis")?.takeIf { it.isNotBlank() } ?: story.extendedAnalysis
    Card(
        Modifier.fillMaxWidth().clickable { onOpenReader(story) },
        colors = CardDefaults.cardColors(containerColor = if (hero) Warm else Color.White),
        shape = RoundedCornerShape(if (hero) 20.dp else 18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (hero) Color(0xFFE0DACD) else Color(0xFFE8E4DA))
    ) {
        Column {
            AbstractCoverBanner(story, hero, Modifier)
            Column(Modifier.padding(if (hero) 22.dp else 20.dp)) {
            Text(
                if (hero) I18n.tr("top_story", language) else if (story.editorialLevel == "editors_pick") I18n.tr("editors_pick", language) else I18n.tr("featured_analysis", language),
                color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp
            )
            Spacer(Modifier.height(9.dp))
            Text(title, fontFamily = FontFamily.Serif, fontSize = if (hero) 27.sp else 19.sp, lineHeight = if (hero) 31.sp else 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(summary, color = Color(0xFF5A544B), fontSize = 20.sp, lineHeight = if (hero) 32.sp else 25.sp, maxLines = if (hero) 6 else 4, overflow = TextOverflow.Ellipsis)
            Text("⏱️ ${readTime(summary, language)}", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
            if (takeaways.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(I18n.tr("key_takeaways", language), color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                takeaways.take(3).forEach { takeaway -> Text("• $takeaway", color = Color(0xFF5A544B), fontSize = 12.sp, lineHeight = 17.sp) }
            }
            if (!why.isNullOrBlank()) {
                Spacer(Modifier.height(14.dp))
                Surface(color = Color(0xFFF0EBE2), shape = RoundedCornerShape(4.dp), modifier = Modifier.border(1.dp, Color(0xFFE0DACD), RoundedCornerShape(4.dp))) {
                    Column(Modifier.padding(12.dp)) {
                        Text(I18n.tr("why_it_matters", language), color = Terra, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(why, fontSize = 12.sp, lineHeight = 17.sp)
                    }
                }
            }
            if (extended.isNotBlank()) TextButton(onClick = { onOpenReader(story) }, contentPadding = PaddingValues(0.dp)) { Text(I18n.tr("read_more", language), color = Terra, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(13.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${I18n.tr("source", language)} ${story.source}", color = Terra, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                audioUrlFor(story, language)?.let { audioUrl ->
                    TextButton(onClick = {
                        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW).apply { setDataAndType(Uri.parse(audioUrl), "audio/*") }) }
                    }, contentPadding = PaddingValues(0.dp)) {
                        Icon(Icons.Default.GraphicEq, null, tint = Terra, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(I18n.tr("listen", language), color = Terra, fontWeight = FontWeight.Bold)
                    }
                }
            }
            }
        }
    }
}

private fun readTime(text: String, language: String): String {
    val minutes = (text.trim().split(Regex("\\s+")).size / 180).coerceAtLeast(1)
    return "$minutes ${I18n.tr("read_time", language)}"
}

@Composable
private fun ReaderView(story: Story, language: String, onClose: () -> Unit) {
    val context = LocalContext.current
    val localized = I18n.editorial(story, language)
    val title = localized?.optString("title")?.takeIf { it.isNotBlank() } ?: story.title
    val summary = localized?.optString("summary")?.takeIf { it.isNotBlank() } ?: story.summary
    val why = localized?.optString("why_it_matters")?.takeIf { it.isNotBlank() }
    val extended = localized?.optString("extended_analysis")?.takeIf { it.isNotBlank() } ?: story.extendedAnalysis
    val takeaways = takeawaysFrom(story.editorial, language).ifEmpty { story.keyTakeaways }
    LazyColumn(contentPadding = PaddingValues(bottom = 28.dp)) {
        item { AbstractCoverBanner(story, true, Modifier) }
        item {
            Column(Modifier.padding(horizontal = 22.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(title, fontFamily = FontFamily.Serif, fontSize = 30.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold)
                Text("⏱️ ${readTime(summary, language)}  ·  ${story.source}", color = Muted, fontSize = 12.sp)
                Text(summary, color = Color(0xFF5A544B), fontSize = 18.sp, lineHeight = 27.sp)
                if (takeaways.isNotEmpty()) {
                    Text(I18n.tr("key_takeaways", language), color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    takeaways.take(3).forEach { Text("• $it", color = Color(0xFF5A544B), fontSize = 13.sp, lineHeight = 19.sp) }
                }
                if (!why.isNullOrBlank()) {
                    Surface(color = Color(0xFFF0EBE2), modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE0DACD), RoundedCornerShape(6.dp))) {
                        Column(Modifier.padding(14.dp)) {
                            Text(I18n.tr("why_it_matters", language), color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(Modifier.height(5.dp))
                            Text(why, fontSize = 15.sp, lineHeight = 22.sp)
                        }
                    }
                }
                if (extended.isNotBlank()) Text(extended, color = Color(0xFF5A544B), fontSize = 16.sp, lineHeight = 24.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(story.url))) } }, colors = ButtonDefaults.buttonColors(containerColor = Terra), modifier = Modifier.weight(1f)) {
                        Text(I18n.tr("open_source_link", language), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    TextButton(onClick = {
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, title)
                            putExtra(Intent.EXTRA_TEXT, "$title\n${story.url}\nLetto su Discover Pulse")
                        }
                        context.startActivity(Intent.createChooser(share, I18n.tr("share", language)))
                    }) { Text(I18n.tr("share", language), color = Terra, fontWeight = FontWeight.Bold) }
                }
                TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) { Text("×", fontSize = 24.sp, color = Muted) }
            }
        }
    }
}

private fun formatStars(stars: Int): String =
    if (stars >= 1000) "★ %.1fk".format(Locale.US, stars / 1000.0) else "★ $stars"

private fun formatVelocity(velocity: Double): String =
    "+${String.format(Locale.US, "%.0f", velocity)} stars/day"

private fun isPeriod(story: Story, period: String): Boolean =
    story.period.equals(period, ignoreCase = true)

@Composable
private fun HypeBadge(story: Story) {
    val explosive = story.hypeLabel.contains("EXPLOSIVE", ignoreCase = true) || story.hypeScore >= 85
    val trending = explosive || story.hypeLabel.contains("TRENDING", ignoreCase = true) || story.hypeScore >= 55
    if (!explosive && !trending && story.hypeVelocity == null && story.period.isBlank()) return

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        if (explosive || trending) {
            Surface(
                color = if (explosive) Color(0xFFFFE0C2) else Color(0xFFFFF1D2),
                shape = RoundedCornerShape(50),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (explosive) Color(0xFFE8794F) else Color(0xFFE2B24A))
            ) {
                Text(
                    if (explosive) "🔥 EXPLOSIVE HYPE" else "⚡ TRENDING",
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    color = if (explosive) Color(0xFFB94D2E) else Color(0xFF9A6A00),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
            story.hypeVelocity?.let { velocity ->
                Text(formatVelocity(velocity), color = Terra, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            if (story.period.equals("weekly", ignoreCase = true) || story.period.equals("monthly", ignoreCase = true)) {
                Surface(color = Warm, shape = RoundedCornerShape(50)) {
                    Text(
                        if (story.period.equals("weekly", ignoreCase = true)) "SETTIMANA" else "MESE",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RadarScreen(stories: List<Story>, language: String, padding: PaddingValues) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var gitFilter by remember { mutableStateOf(0) }
    val repositories = stories.filter { story ->
        story.type == "github" && when (gitFilter) {
            1 -> isPeriod(story, "weekly")
            2 -> isPeriod(story, "monthly")
            else -> true
        }
    }.sortedWith(compareByDescending<Story> { it.hypeScore }.thenByDescending { it.hypeVelocity ?: 0.0 }.thenByDescending { it.stars })
    val radarStories = stories.filter { it.type != "github" && it.type != "speak_up" }
    ScreenColumn(padding) { modifier ->
        LazyColumn(modifier, contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text(I18n.tr("radar_kicker", language), color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
                Text(I18n.tr("radar_title", language), fontFamily = FontFamily.Serif, fontSize = 31.sp, fontWeight = FontWeight.Bold)
                Text(I18n.tr("radar_subtitle", language), color = Muted, fontSize = 14.sp)
            }
            item {
                Row(
                    Modifier.fillMaxWidth().background(Warm, RoundedCornerShape(28.dp)).padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("radar_tab_signals", "radar_tab_repos").forEachIndexed { index, key ->
                        Surface(
                            Modifier.weight(1f).clickable { selectedTab = index },
                            color = if (selectedTab == index) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(24.dp),
                            shadowElevation = if (selectedTab == index) 1.dp else 0.dp
                        ) {
                            Text(
                                I18n.tr(key, language),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                color = if (selectedTab == index) Ink else Muted,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
            if (selectedTab == 0) {
                items(radarStories, key = { it.id }) { story ->
                    val localized = I18n.editorial(story, language)
                    val title = localized?.optString("title")?.takeIf { it.isNotBlank() } ?: story.title
                    val summary = localized?.optString("summary")?.takeIf { it.isNotBlank() } ?: story.summary
                    Card(
                        Modifier.fillMaxWidth().clickable {
                            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(story.url))) }
                        },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(17.dp)) {
                            Text(I18n.tr("scouted_badge", language), color = Sage, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
                            Spacer(Modifier.height(7.dp))
                            Text(title, fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(7.dp))
                            Text(summary, color = Muted, fontSize = 13.sp, lineHeight = 19.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.height(7.dp))
                            Text("${I18n.tr("primary_source_label", language)} ${story.source}", color = Terra, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            TextButton(onClick = {
                                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(story.url))) }
                            }, contentPadding = PaddingValues(0.dp)) {
                                Text(I18n.tr("read_more", language), color = Terra, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                item {
                    Row(
                        Modifier.fillMaxWidth().background(Warm, RoundedCornerShape(20.dp)).padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        listOf("git_filter_all", "git_filter_weekly", "git_filter_monthly").forEachIndexed { index, key ->
                            Surface(
                                Modifier.weight(1f).clickable { gitFilter = index },
                                color = if (gitFilter == index) Color.White else Color.Transparent,
                                shape = RoundedCornerShape(18.dp),
                                shadowElevation = if (gitFilter == index) 1.dp else 0.dp
                            ) {
                                Text(
                                    I18n.tr(key, language),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                                    color = if (gitFilter == index) Ink else Muted,
                                    fontSize = 10.sp,
                                    fontWeight = if (gitFilter == index) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                items(repositories, key = { it.id }) { story ->
                    Card(
                        Modifier.fillMaxWidth().clickable {
                            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(story.url))) }
                        },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(17.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(story.title, modifier = Modifier.weight(1f), fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                 Surface(color = Warm, shape = RoundedCornerShape(6.dp)) {
                                     Text(formatStars(story.stars), modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp), color = Terra, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                 }
                             }
                             Spacer(Modifier.height(9.dp))
                             HypeBadge(story)
                             if (story.language.isNotBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text(story.language, color = Sage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(7.dp))
                            Text(story.summary, color = Muted, fontSize = 13.sp, lineHeight = 19.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                            TextButton(onClick = {
                                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(story.url))) }
                            }, contentPadding = PaddingValues(0.dp)) {
                                Text(I18n.tr("open_github", language), color = Terra, fontWeight = FontWeight.Bold)
                            }
                        }
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
                Icon(Icons.Default.GraphicEq, null, tint = Terra, modifier = Modifier.size(28.dp))
                Text(I18n.tr("speak_kicker", language), color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
                Text(I18n.tr("speak_title", language), fontFamily = FontFamily.Serif, fontSize = 31.sp, fontWeight = FontWeight.Bold)
                Text(I18n.tr("speak_subtitle", language), color = Muted, fontSize = 14.sp)
            }
            items(episodes, key = { it.id }) { story ->
                val localized = I18n.editorial(story, language)
                val title = localized?.optString("title")?.takeIf { it.isNotBlank() } ?: story.title
                val summary = localized?.optString("summary")?.takeIf { it.isNotBlank() } ?: story.summary
                Card(
                    Modifier.fillMaxWidth().clickable {
                        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(story.url))) }
                    },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(17.dp)) {
                        Text(story.source.uppercase() + if (story.duration.isNotBlank()) " · ${story.duration}" else "", color = Sage, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(7.dp))
                        Text(title, fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(5.dp))
                        Text(summary, color = Muted, fontSize = 13.sp, lineHeight = 19.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        val audioUrl = audioUrlFor(story, language)
                        TextButton(onClick = {
                            runCatching {
                                if (audioUrl != null) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW).apply { setDataAndType(Uri.parse(audioUrl), "audio/*") })
                                } else {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(story.url)))
                                }
                            }
                        }, contentPadding = PaddingValues(0.dp)) {
                            Icon(if (audioUrl != null) Icons.Default.GraphicEq else Icons.Default.Sensors, null, tint = Terra)
                            Spacer(Modifier.width(5.dp))
                            Text(I18n.tr(if (audioUrl != null) "play_episode" else "open_episode", language), color = Terra, fontWeight = FontWeight.Bold)
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
    val uiLanguage = language
    ScreenColumn(padding) { modifier ->
        LazyColumn(modifier, contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            item {
                Text(I18n.tr("my_pulse_kicker", uiLanguage), color = Terra, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
                Text(I18n.tr("my_pulse_title", uiLanguage), fontFamily = FontFamily.Serif, fontSize = 30.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold)
                Text(I18n.tr("my_pulse_subtitle", uiLanguage), color = Muted, fontSize = 14.sp)
            }
            item {
                SectionLabel(I18n.tr("topics", uiLanguage))
                topicOptions.forEach { topic ->
                    Row(Modifier.fillMaxWidth().clickable {
                        topics = if (topic in topics) topics - topic else topics + topic
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = topic in topics, onCheckedChange = { checked -> topics = if (checked) topics + topic else topics - topic })
                        Text(I18n.topic(topic, uiLanguage), fontSize = 14.sp)
                    }
                }
            }
            item {
                SectionLabel(I18n.tr("briefing_language", uiLanguage))
                    listOf("it", "en", "es", "zh", "de").forEach { code ->
                        Row(Modifier.fillMaxWidth().clickable { language = code }, verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = language == code, onClick = { language = code })
                            Text("${code.uppercase()}  ${I18n.languageName(code, uiLanguage)}", fontSize = 14.sp)
                        }
                    }
            }
            item {
                SectionLabel(I18n.tr("editorial_level", uiLanguage))
                listOf(true to I18n.tr("curated_only", uiLanguage), false to I18n.tr("all_news", uiLanguage)).forEach { (value, label) ->
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
                                Text(I18n.tr("sync_title", uiLanguage), fontWeight = FontWeight.Bold)
                                Text(I18n.tr("sync_body", uiLanguage), color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = { synced = true }, colors = ButtonDefaults.buttonColors(containerColor = if (synced) Sage else Ink)) {
                            if (synced) Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                            Text(if (synced) I18n.tr("synced_account", uiLanguage) else I18n.tr("sign_in_google", uiLanguage))
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
                ) { Text(I18n.tr("save", uiLanguage), fontWeight = FontWeight.Bold) }
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
private fun Footer(context: Context, language: String) {
    Column(Modifier.padding(top = 18.dp, bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(I18n.tr("footer_brand", language), color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text(I18n.tr("footer_sources", language), color = Muted, fontSize = 11.sp)
        Text(I18n.tr("privacy", language), color = Terra, fontSize = 11.sp, modifier = Modifier.clickable { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.PRIVACY_URL))) } })
        Text(I18n.tr("contact", language), color = Terra, fontSize = 11.sp, modifier = Modifier.clickable { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.CONTACT_URL))) } })
    }
}
