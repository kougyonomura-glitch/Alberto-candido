package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DraftedJob
import com.example.data.Inquiry
import com.example.ui.AppViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        TabItem("サービス紹介", Icons.Default.BusinessCenter, "Services"),
        TabItem("AIコンサル", Icons.Default.Psychology, "AI Consultant"),
        TabItem("お問い合わせ", Icons.Default.Email, "Contact & Logs"),
        TabItem("会社案内", Icons.Default.CorporateFare, "About & Trust")
    )

    Scaffold(
        topBar = {
            Surface(
                color = Color(0xFFF7F9FC),
                modifier = Modifier.statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo + App Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // NX Icon Box in blue
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF005AC1), shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "NX",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        
                        // NEXUS Japan Title
                        Row {
                            Text(
                                text = "NEXUS ",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1C1B1F),
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Japan",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF005AC1),
                                fontSize = 18.sp
                            )
                        }
                    }

                    // JP / PT Language badge pill decoration
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0xFFDDE2EA), shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "JP",
                            color = Color(0xFF005AC1),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(12.dp)
                                .background(Color(0xFFDDE2EA))
                        )
                        Text(
                            text = "PT",
                            color = Color(0xFF44474E),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column {
                // Top border divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFDDE2EA))
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val isSelected = selectedTab == index
                        Column(
                            modifier = Modifier
                                .clickable { selectedTab = index }
                                .padding(horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Rounded geometric pill indicator for selected screen icon
                            Box(
                                modifier = Modifier
                                    .width(48.dp)
                                    .height(32.dp)
                                    .background(
                                        color = if (isSelected) Color(0xFF005AC1).copy(alpha = 0.1f) else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.englishLabel,
                                    tint = if (isSelected) Color(0xFF005AC1) else Color(0xFF1C1B1F).copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            // Small text label
                            Text(
                                tab.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF005AC1) else Color(0xFF1C1B1F).copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background mesh layout
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Slate50,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    0 -> ServicesCatalogScreen(
                        onNavigateToAi = { prompt, category ->
                            viewModel.setAiPredefinedPrompt(prompt, category)
                            selectedTab = 1
                        },
                        onNavigateToContact = { category ->
                            viewModel.updateForm(category = category)
                            selectedTab = 2
                        }
                    )
                    1 -> AiConsultantScreen(viewModel = viewModel)
                    2 -> ContactAndLogsScreen(viewModel = viewModel)
                    3 -> TrustProfileScreen()
                }
            }
        }
    }
}

data class TabItem(val label: String, val icon: ImageVector, val englishLabel: String)

// ==========================================
// TAB 1: SERVICES CATALOG SHOWCASE
// ==========================================
@Composable
fun ServicesCatalogScreen(
    onNavigateToAi: (String, String) -> Unit,
    onNavigateToContact: (String) -> Unit
) {
    var activeCategoryFilter by remember { mutableStateOf("All") }

    val serviceClasses = listOf(
        ServiceItem(
            id = "rec_1",
            category = "Recruitment",
            jpTitle = "正社員・スペシャリスト採用紹介",
            ptTitle = "Recrutamento Regular / Tempo Integral",
            jpDesc = "高度IT人材、バイリンガル事務、営業職など即戦力のグローバル・ローカル人材を紹介。確実なマッチングを提供。",
            ptDesc = "Introdução de profissionais de TI, secretariado bilingue e vendas prontos para atuar na liderança corporativa.",
            icon = Icons.Default.Work,
            suggestPrompt = "Preciso de um profissional de TI bilingue (Português/Japonês) especializado em desenvolvimento Java em Tóquio, período integral."
        ),
        ServiceItem(
            id = "rec_2",
            category = "Recruitment",
            jpTitle = "パートタイム・アルバit雇用紹介",
            ptTitle = "Recrutamento Meio Período (Arubaito)",
            jpDesc = "ホテル、翻訳アシスタント、カスタマーサポート、飲食・製造の現場でお役立ちできる真面目な外国人・現地人材の配置。",
            ptDesc = "Suplementação ágil para hotelaria, helpdesk bilíngue, suporte ao cliente, e atendimento em frentes operacionais.",
            icon = Icons.Default.AccountBox,
            suggestPrompt = "Preciso de 3 funcionários para atendimento e recepção bilíngue em regime de meio período em Yokohama."
        ),
        ServiceItem(
            id = "trans_1",
            category = "Translation",
            jpTitle = "ビジネス契約書・法務マニュアル翻訳",
            ptTitle = "Tradução Técnica e Jurídica de Contratos",
            jpDesc = "機密契約書、仕様書、広報マニュアル、就業規則を日英・日葡・日中で確実にプロフェッショナル翻訳・ローカライズ。",
            ptDesc = "Tradução e localização de contratos de sigilo, manuais corporativos e termos de serviço em múltiplos idiomas.",
            icon = Icons.Default.Translate,
            suggestPrompt = "Necessito de tradução juramentada e localização técnica para um contrato de parceria comercial e termos de trabalho de japonês para português."
        ),
        ServiceItem(
            id = "trans_2",
            category = "Translation",
            jpTitle = "商談派遣・展示会ビジネス通訳",
            ptTitle = "Intérpretes Executivos para Feiras e Negócios",
            jpDesc = "海外クライアントとの重要商談、大規模展示会（MICE）、監査立ち会いで齟齬のない対話を実現するプロ通訳者を派遣。",
            ptDesc = "Envio de intérpretes executivos especializados para reuniões comerciais estratégicas e feiras internacionais.",
            icon = Icons.Default.Groups,
            suggestPrompt = "Preciso de um intérprete profissional Português-Japonês por 3 dias para nos acompanhar em uma feira de tecnologia em Osaka."
        ),
        ServiceItem(
            id = "train_1",
            category = "Training",
            jpTitle = "社内DX・ITベーシックス研修講座",
            ptTitle = "Cursos de Capacitação DX e IT Corporativa",
            jpDesc = "新入社員や外国籍人材がシステム構築や日本独自のITインフラ、セキュリティ規格を学ぶための実践的トレーニングプログラム。",
            ptDesc = "Programas práticos voltados à capacitação em Transformação Digital (DX), segurança corporativa e ferramentas de TI.",
            icon = Icons.Default.ImportContacts,
            suggestPrompt = "Quero planejar um curso corporativo para treinar 10 funcionários em transformação digital (DX) e segurança corporativa básica."
        ),
        ServiceItem(
            id = "train_2",
            category = "Training",
            jpTitle = "日本マナー・マインド・異文化適応研修",
            ptTitle = "Treinamento de Etiqueta de Negócios e Adaptação",
            jpDesc = "敬語の使い方、電話応対、名刺交換、報連相（ほうれんそう）の習慣を学び、グローバル recruits の離職を防止します。",
            ptDesc = "Treinamento intensivo sobre etiqueta de negócios no Japão, comunicação 'Hou-Ren-So' e comportamento organizacional.",
            icon = Icons.Default.MenuBook,
            suggestPrompt = "Desejo organizar um workshop intensivo de etiqueta de negócios e conduta no Japão (Hou-Ren-So) para novos funcionários estrangeiros."
        ),
        ServiceItem(
            id = "sol_1",
            category = "Solutions",
            jpTitle = "就労ビザ・在留資格取得スピード診断",
            ptTitle = "Assessoria Ágil para Visto de Trabalho e Imigração",
            jpDesc = "入国管理局への申請手続き、特定技能ビザや技術・人文知識・国際業務ビザの切替・取得サポートをベテラン行政書士と連携して実施。",
            ptDesc = "Diagnóstico e assessoria para obtenção, extensão e transição de Vistos de Trabalho e vistos de Habilidades Específicas.",
            icon = Icons.Default.Gavel,
            suggestPrompt = "Busco assistência e consultoria rápida para renovar e alterar a categoria do visto de trabalho de engenheiro para 5 novos contratados."
        ),
        ServiceItem(
            id = "sol_2",
            category = "Solutions",
            jpTitle = "24時間対応 多言語コンタクトセンター構築",
            ptTitle = "Terceirização de Central Multilíngue (BPO Helpdesk)",
            jpDesc = "外国籍社員やグローバル顧客からの問い合わせを、24/7体制でプロスタッフが英語・ポルトガル語・中国語で代行サポート。",
            ptDesc = "Configuração rápida de Helpdesk terceirizado bilíngue para dar suporte ininterrupto a equipes ou clientes estrangeiros.",
            icon = Icons.Default.SupportAgent,
            suggestPrompt = "Gostaria de terceirizar meu suporte ao cliente pós-venda configurando uma linha de Helpdesk bilingue disponível 24 horas por dia."
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Headline Section from Geometric Balance
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 12.dp)
        ) {
            Text(
                text = "企業の成長を",
                fontWeight = FontWeight.Black,
                color = Color(0xFF1C1B1F),
                fontSize = 26.sp,
                lineHeight = 30.sp
            )
            Text(
                text = "加速させる",
                fontWeight = FontWeight.Black,
                color = Color(0xFF005AC1),
                fontSize = 26.sp,
                lineHeight = 30.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Soluções integradas de mão de obra e serviços",
                fontSize = 12.sp,
                color = Color(0xFF44474E),
                fontWeight = FontWeight.Medium,
                style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            )
        }

        // Subtitle Card description
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            border = BorderStroke(1.dp, Color(0xFFDDE2EA))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "人材、言語、教育、実務のあらゆる課題をワンストップで解決。まずはカテゴリーを選択してサービスを確認してください。",
                    color = Color(0xFF44474E),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Smooth Horizontal Scrollable Filter Chips Row
        val categoriesList = listOf(
            FilterCat("All", "全て"),
            FilterCat("Recruitment", "求人・採用紹介"),
            FilterCat("Translation", "ビジネス翻訳・通訳"),
            FilterCat("Training", "企業研修・講座"),
            FilterCat("Solutions", "スピード解決・BPO")
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            categoriesList.forEach { cat ->
                val isSelected = activeCategoryFilter == cat.id
                FilterChip(
                    selected = isSelected,
                    onClick = { activeCategoryFilter = cat.id },
                    label = { 
                        Text(
                            cat.label, 
                            fontSize = 11.sp, 
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ) 
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF005AC1),
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = Color(0xFF44474E)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) Color.Transparent else Color(0xFFDDE2EA),
                        selectedBorderColor = Color.Transparent,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 0.dp
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
            }
        }

        val filteredItems = if (activeCategoryFilter == "All") {
            serviceClasses
        } else {
            serviceClasses.filter { it.category == activeCategoryFilter }
        }

        // List of Services
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filteredItems) { item ->
                ServiceCard(
                    item = item,
                    onMatchAi = { onNavigateToAi(item.suggestPrompt, item.category) },
                    onContact = { onNavigateToContact(item.category) }
                )
            }
        }
    }
}

data class FilterCat(val id: String, val label: String)
data class ServiceItem(
    val id: String,
    val category: String,
    val jpTitle: String,
    val ptTitle: String,
    val jpDesc: String,
    val ptDesc: String,
    val icon: ImageVector,
    val suggestPrompt: String
)

@Composable
fun ServiceCard(
    item: ServiceItem,
    onMatchAi: () -> Unit,
    onContact: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("service_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFDDE2EA))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0E2EC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = Color(0xFF005AC1),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.jpTitle,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1B1F),
                        fontSize = 14.sp
                    )
                    Text(
                        text = item.ptTitle,
                        color = Color(0xFF005AC1),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
                // Small Category Tag
                Box(
                    modifier = Modifier
                        .background(
                            color = when (item.category) {
                                "Recruitment" -> EmeraldGreen.copy(alpha = 0.1f)
                                "Translation" -> TrustBlue.copy(alpha = 0.1f)
                                "Training" -> TrueGold.copy(alpha = 0.1f)
                                else -> Slate900.copy(alpha = 0.1f)
                            },
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = when (item.category) {
                            "Recruitment" -> "採用"
                            "Translation" -> "翻訳"
                            "Training" -> "研修"
                            else -> "即解決"
                        },
                        color = when (item.category) {
                            "Recruitment" -> EmeraldGreen
                            "Translation" -> TrustBlue
                            "Training" -> TrueGold
                            else -> Slate700
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // JP Description
            Text(
                text = item.jpDesc,
                color = Color(0xFF44474E),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(6.dp))

            // PT Description
            Text(
                text = "📌 ${item.ptDesc}",
                color = Color(0xFF44474E).copy(alpha = 0.8f),
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMatchAi,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("btn_ai_${item.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF005AC1),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("💡 AI即時提案", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onContact,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("btn_contact_${item.id}"),
                    border = BorderStroke(1.dp, Color(0xFFDDE2EA)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1C1B1F),
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("💬 窓口相談", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// TAB 2: AI CONSULTOR & PORTAL GENERATOR
// ==========================================
@Composable
fun AiConsultantScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    var saveTitleInput by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }

    val prompt = viewModel.aiPrompt.collectAsStateWithLifecycle().value
    val category = viewModel.aiCategory.collectAsStateWithLifecycle().value
    val isGenerating = viewModel.aiGenerating.collectAsStateWithLifecycle().value
    val apiError = viewModel.aiError.collectAsStateWithLifecycle().value
    val generatedDraft = viewModel.generatedJobDraft.collectAsStateWithLifecycle().value
    val actionPlan = viewModel.generatedActionPlan.collectAsStateWithLifecycle().value

    val savedDrafts = viewModel.draftedJobsFlow.collectAsStateWithLifecycle().value

    val categories = listOf("Recruitment", "Translation", "Training", "Solutions")
    val categoryLabels = mapOf(
        "Recruitment" to "人材採用 (Vagas)",
        "Translation" to "翻訳通訳 (Traduções)",
        "Training" to "企業研修 (Cursos)",
        "Solutions" to "解決ソリューション"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Explanatory Intro Card (Geometric Balance Theme)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFDDE2EA)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0E2EC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TipsAndUpdates,
                        contentDescription = null,
                        tint = Color(0xFF005AC1),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "AI 企業相談アドバイザー (Gemini Agent)",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1C1B1F),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Escreva em PORTUGUÊS ou JAPONÊS a demanda da sua empresa. O Gemini Flash criará especificações técnicas/vagas formais in japonês e traçará um plano estratégico em português.",
                        fontSize = 11.sp,
                        color = Color(0xFF44474E),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Text input field - high-contrast curved border style
        OutlinedTextField(
            value = prompt,
            onValueChange = { viewModel.updateAiInputs(prompt = it) },
            label = { Text("Quais necessidades de mão de obra ou serviços você tem?") },
            placeholder = { Text("Ex: Preciso de 2 tradutores inglês-japonês para traduzir manuais de segurança de maquinário...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .testTag("ai_prompt_input"),
            shape = RoundedCornerShape(16.dp),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF005AC1),
                unfocusedBorderColor = Color(0xFFDDE2EA),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Selection
        Text(
            "Selecione uma Categoria Geral:",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = Color(0xFF44474E),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = category == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateAiInputs(category = cat) },
                    label = { Text(categoryLabels[cat] ?: cat, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF005AC1),
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = Color(0xFF44474E)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) Color.Transparent else Color(0xFFDDE2EA),
                        selectedBorderColor = Color.Transparent,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 0.dp
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
            }
        }

        // Fast Prompts templates helper
        Text(
            "Modelos Rápidos (Toque para usar):",
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = Color(0xFF44474E),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        val quickPrompts = listOf(
            QuickPrompt("Contratar bilingue", "Recruitment", "Preciso de um engenheiro de manufatura fluente em português e japonês para atuar no controle de qualidade da fábrica."),
            QuickPrompt("Tradução rápida", "Translation", "Preciso traduzir com urgência um manual técnico de prevenção de acidentes industriais."),
            QuickPrompt("Curso de etiqueta", "Training", "Necessito organizar um treinamento presencial de boas maneiras corporativas japonesas.")
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickPrompts.forEach { qp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setAiPredefinedPrompt(qp.prompt, qp.category)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFDDE2EA))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFE0E2EC), shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = Color(0xFF005AC1),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(qp.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1B1F))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(qp.prompt, fontSize = 10.sp, color = Color(0xFF44474E), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        // Generate Action Button (Obsidian-black core action design)
        Button(
            onClick = { viewModel.runAiConsultant() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("ai_generate_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1C1B1F),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = !isGenerating && prompt.isNotBlank()
        ) {
            if (isGenerating) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Processando via Gemini AI...", fontSize = 13.sp)
            } else {
                Icon(imageVector = Icons.Default.Psychology, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Consultar Inteligência Artificial & Criar Planilha", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // API Indicator / Warnings
        apiError?.let { err ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }

        // RESULT DISPLAY
        if (generatedDraft != null || actionPlan != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, TrustBlue.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "📋 RESULTADO DA CONSULTORIA",
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                            fontSize = 13.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Copy button
                            IconButton(onClick = {
                                val fullClipText = "【求人票・仕様書】\n$generatedDraft\n\n【アクションプラン】\n$actionPlan"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("BizLink Draft", fullClipText))
                                Toast.makeText(context, "Copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = TrustBlue)
                            }

                            // Favorite save button
                            IconButton(onClick = {
                                saveTitleInput = "Proposta: " + prompt.take(15) + "..."
                                showSaveDialog = true
                            }) {
                                Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Save Favorite", tint = EmeraldGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "📄 求人票・要件仕様書 (Japanese Spec)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    SelectionContainer {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Slate100)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = generatedDraft ?: "",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Slate900,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "💡 Estratégia de Atuação (Action Plan in PT)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SelectionContainer {
                        Text(
                            text = actionPlan ?: "",
                            fontSize = 12.sp,
                            color = Slate800,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Saved Drafts List (Favorites)
        if (savedDrafts.isNotEmpty()) {
            Text(
                "📂 Consultorias & Vagas Salvas (${savedDrafts.size})",
                fontWeight = FontWeight.Bold,
                color = Slate900,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            savedDrafts.forEach { draft ->
                var expanded by remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(draft.title, fontWeight = FontWeight.Bold, color = Slate900, fontSize = 12.sp)
                                Text("Categoria: ${categoryLabels[draft.category] ?: draft.category}", fontSize = 10.sp, color = Slate600)
                            }
                            Row {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Expand"
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteDraft(draft) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }

                        if (expanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Slate600.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))
                            SelectionContainer {
                                Column {
                                    Text("📝 Vaga/Especificação Japonesa:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Slate900)
                                    Text(draft.description, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Slate700, modifier = Modifier.padding(top = 4.dp))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("💡 Recomendações e Planejamento:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Slate900)
                                    Text(draft.actionPlan, fontSize = 11.sp, color = Slate700, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialogue to input template title
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Salvar Proposta nos Favoritos", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Diga um título para identificar este rascunho de vaga:", fontSize = 11.sp, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = saveTitleInput,
                        onValueChange = { saveTitleInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.saveDraft(saveTitleInput)
                    showSaveDialog = false
                    Toast.makeText(context, "Vaga salva com sucesso!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

data class QuickPrompt(val label: String, val category: String, val prompt: String)

// ==========================================
// TAB 3: CONTACT FORM & SUBMISSION HISTORY LOGS
// ==========================================
@Composable
fun ContactAndLogsScreen(viewModel: AppViewModel) {
    val inquiries = viewModel.inquiriesFlow.collectAsStateWithLifecycle().value
    val companyName = viewModel.companyName.collectAsStateWithLifecycle().value
    val contactPerson = viewModel.contactPerson.collectAsStateWithLifecycle().value
    val email = viewModel.email.collectAsStateWithLifecycle().value
    val phone = viewModel.phone.collectAsStateWithLifecycle().value
    val category = viewModel.inquiryCategory.collectAsStateWithLifecycle().value
    val message = viewModel.message.collectAsStateWithLifecycle().value
    val isFormSubmitting = viewModel.formSubmitting.collectAsStateWithLifecycle().value
    val successMsg = viewModel.formSuccessMessage.collectAsStateWithLifecycle().value

    val categories = listOf("Recruitment", "Translation", "Training", "Solutions")
    val categoryLabels = mapOf(
        "Recruitment" to "求人・採用 (Workforce Recruits)",
        "Translation" to "翻訳・通訳 (Translation & Interpreting)",
        "Training" to "企業研修・講座 (Cursos & Coaching)",
        "Solutions" to "スピード解決・BPO (Fast Resolution)"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFDDE2EA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        "窓口相談フォーム / Form de Contato",
                        color = Color(0xFF1C1B1F),
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Submeta seu requerimento formal abaixo. As informações serão salvas com segurança no banco de dados local (Room Database) da sua empresa para acompanhamento ágil.",
                        color = Color(0xFF44474E),
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        // Submissions feedback status card
        successMsg?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (msg.contains("Erro")) MaterialTheme.colorScheme.errorContainer else EmeraldGreen.copy(alpha = 0.15f)
                    ),
                    border = BorderStroke(1.dp, if (msg.contains("Erro")) MaterialTheme.colorScheme.error else EmeraldGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = msg,
                            color = if (msg.contains("Erro")) MaterialTheme.colorScheme.onErrorContainer else Slate900,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f),
                            lineHeight = 15.sp
                        )
                        IconButton(onClick = { viewModel.clearFormMessage() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Forms fields
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFDDE2EA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("お問い合わせ情報入力 (Formulário)", fontWeight = FontWeight.Bold, color = Color(0xFF1C1B1F), fontSize = 13.sp, modifier = Modifier.padding(bottom = 12.dp))
 
                    // Company Name
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { viewModel.updateForm(companyName = it) },
                        label = { Text("会社名 (Nome da Empresa) *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .testTag("form_company_name"),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF005AC1),
                            unfocusedBorderColor = Color(0xFFDDE2EA),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
 
                    // Contact Person
                    OutlinedTextField(
                        value = contactPerson,
                        onValueChange = { viewModel.updateForm(contactPerson = it) },
                        label = { Text("役職・担当者名 (Pessoa de Contato) *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .testTag("form_contact_person"),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF005AC1),
                            unfocusedBorderColor = Color(0xFFDDE2EA),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
 
                    // Email Address
                    OutlinedTextField(
                        value = email,
                        onValueChange = { viewModel.updateForm(email = it) },
                        label = { Text("メールアドレス (Email) *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .testTag("form_email"),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF005AC1),
                            unfocusedBorderColor = Color(0xFFDDE2EA),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
 
                    // Phone Number
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { viewModel.updateForm(phone = it) },
                        label = { Text("電話番号 (Telefone - Opcional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .testTag("form_phone"),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF005AC1),
                            unfocusedBorderColor = Color(0xFFDDE2EA),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    // Category Select Dropdown alternatives or visual clickable items
                    Text("相談ジャンル (Categoria da Demanda):", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Slate700, modifier = Modifier.padding(bottom = 4.dp))
                    categories.forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateForm(category = cat) }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = category == cat,
                                onClick = { viewModel.updateForm(category = cat) },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF005AC1))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(categoryLabels[cat] ?: cat, fontSize = 11.sp, color = Slate800, fontWeight = if (category == cat) FontWeight.Bold else FontWeight.Normal)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Message Content
                    OutlinedTextField(
                        value = message,
                        onValueChange = { viewModel.updateForm(message = it) },
                        label = { Text("具体的相談内容 / 詳細条件 (Sua Mensagem) *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .padding(bottom = 14.dp)
                            .testTag("form_remarks"),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF005AC1),
                            unfocusedBorderColor = Color(0xFFDDE2EA),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        maxLines = 4
                    )

                    Button(
                        onClick = { viewModel.submitInquiry() },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("form_submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1C1B1F),
                            contentColor = Color.White
                        ),
                        enabled = !isFormSubmitting && companyName.isNotBlank() && contactPerson.isNotBlank() && email.isNotBlank() && message.isNotBlank()
                    ) {
                        Text("📩 相談を送信・履歴に保存", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // HISTORICAL LOGS
        item {
            Text(
                "📜 お問い合わせ・提出履歴 (${inquiries.size})",
                fontWeight = FontWeight.Bold,
                color = Slate900,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        if (inquiries.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate100),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(28.dp), tint = Slate600.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nenhuma solicitação arquivada até o momento.",
                            color = Slate600,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        } else {
            items(inquiries) { inquiry ->
                InquiryLogItem(inquiry = inquiry, onDelete = { viewModel.deleteInquiry(inquiry) })
            }
        }
    }
}

@Composable
fun InquiryLogItem(inquiry: Inquiry, onDelete: () -> Unit) {
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val formattedDate = remember(inquiry.timestamp) { formatter.format(Date(inquiry.timestamp)) }

    val categoryColor = when (inquiry.category) {
        "Recruitment" -> EmeraldGreen
        "Translation" -> TrustBlue
        "Training" -> TrueGold
        else -> Slate700
    }

    val categoryText = when (inquiry.category) {
        "Recruitment" -> "採用紹介"
        "Translation" -> "翻訳通訳"
        "Training" -> "企業研修"
        else -> "IT解決"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inquiry_log_item_${inquiry.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFDDE2EA))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(categoryColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(categoryText, color = categoryColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = inquiry.companyName,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(formattedDate, fontSize = 9.sp, color = Slate600)
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text("👤 Contato: ${inquiry.contactPerson} (${inquiry.email})", fontSize = 11.sp, color = Slate700, fontWeight = FontWeight.SemiBold)
            if (inquiry.phone.isNotBlank()) {
                Text("📞 Telefone: ${inquiry.phone}", fontSize = 11.sp, color = Slate700)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Divider(color = Slate100)
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = inquiry.message,
                fontSize = 11.sp,
                color = Slate800,
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

// ==========================================
// TAB 4: ABOUT US & TRUST METRICS (会社案内)
// ==========================================
@Composable
fun TrustProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Philosophy Card (Geometric Balance Theme)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFDDE2EA)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0E2EC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupervisorAccount,
                        contentDescription = null,
                        tint = Color(0xFF005AC1),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "企業理念 (Our Philosophy)",
                    color = Color(0xFF1C1B1F),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "「世界の才能と日本の企業を繋ぐ、新たな価値への貢献」",
                    color = Color(0xFF005AC1),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Conectando talentos globais ao mercado corporativo japonês para assegurar estabilidade, conformidade regulatória e excelência operacional.",
                    color = Color(0xFF44474E),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // ご利用の流れ (Service Flow Process Tracker)
        Text("ご利用の流れ (Fluxo de Atendimento)", fontWeight = FontWeight.Bold, color = Slate900, fontSize = 13.sp)

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFDDE2EA)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                val flowSteps = listOf(
                    FlowStep("1", "ご相談確認", "Análise e preenchimento das exigências iniciais da sua corporação."),
                    FlowStep("2", "AI最適化提案", "Modelagem da proposta in japonês (求人票) e criação do blueprint de soluções pelo nosso assistente."),
                    FlowStep("3", "人材マッチング・研修", "Busca bilingue ativa e cursos de adaptação e conformidade cultural japonesa."),
                    FlowStep("4", "業務稼働・サポート", "Início ágil das operações com acompanhamento pós-contratação e BPO.")
                )

                flowSteps.forEachIndexed { idx, step ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE0E2EC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(step.num, color = Color(0xFF005AC1), fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(step.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1C1B1F))
                            Text(step.desc, fontSize = 11.sp, color = Color(0xFF44474E), lineHeight = 15.sp)
                        }
                    }

                    if (idx < flowSteps.size - 1) {
                        // Vertical Connecting line
                        Box(
                            modifier = Modifier
                                .padding(start = 13.dp, top = 4.dp, bottom = 4.dp)
                                .width(2.dp)
                                .height(16.dp)
                                .background(Color(0xFFE0E2EC))
                        )
                    }
                }
            }
        }

        // 会社概要 (Official Company Profile)
        Text("会社概要 (Perfil Corporativo)", fontWeight = FontWeight.Bold, color = Slate900, fontSize = 13.sp)

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFDDE2EA)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileRow("会社名 (Nome)", "ビズリンク・ジャパン株式会社 (BizLink Japan Corp.)")
                ProfileRow("設立 (Fundação)", "2018年5月15日")
                ProfileRow("資本金 (Capital)", "50,000,000 円 (JPY)")
                ProfileRow("代表取締役 (CEO)", "健三 寛 (Hiroshi Kenzo)")
                ProfileRow("所在地 (Endereço)", "東京都港区六本木6丁目 (Roppongi, Minato-ku, Tokyo)")
                ProfileRow("許認可 (Licenças)", "厚生労働省：一般労働者派遣事業 派13-309112")
                ProfileRow("事業内容 (Negócio)", "グローバル人材紹介派遣、多言語ビジネス翻訳、社員研修、IT-BPO事業")
            }
        }
    }
}

data class FlowStep(val num: String, val title: String, val desc: String)

@Composable
fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Slate600,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(100.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 11.sp,
            color = Slate900,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
    }
    Divider(color = Slate100)
}
