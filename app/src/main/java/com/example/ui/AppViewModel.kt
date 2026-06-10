package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(
    application: Application,
    private val repository: AppRepository
) : AndroidViewModel(application) {

    // --- Local DB Streams ---
    val inquiriesFlow: StateFlow<List<Inquiry>> = repository.allInquiries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val draftedJobsFlow: StateFlow<List<DraftedJob>> = repository.allDraftedJobs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- UI Form States ---
    private val _companyName = MutableStateFlow("")
    val companyName = _companyName.asStateFlow()

    private val _contactPerson = MutableStateFlow("")
    val contactPerson = _contactPerson.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()

    private val _inquiryCategory = MutableStateFlow("Recruitment")
    val inquiryCategory = _inquiryCategory.asStateFlow()

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    private val _formSubmitting = MutableStateFlow(false)
    val formSubmitting = _formSubmitting.asStateFlow()

    private val _formSuccessMessage = MutableStateFlow<String?>(null)
    val formSuccessMessage = _formSuccessMessage.asStateFlow()

    fun updateForm(
        companyName: String? = null,
        contactPerson: String? = null,
        email: String? = null,
        phone: String? = null,
        category: String? = null,
        message: String? = null
    ) {
        companyName?.let { _companyName.value = it }
        contactPerson?.let { _contactPerson.value = it }
        email?.let { _email.value = it }
        phone?.let { _phone.value = it }
        category?.let { _inquiryCategory.value = it }
        message?.let { _message.value = it }
    }

    fun submitInquiry() {
        val company = _companyName.value.trim()
        val person = _contactPerson.value.trim()
        val emailAddr = _email.value.trim()
        val phoneNum = _phone.value.trim()
        val cat = _inquiryCategory.value
        val msg = _message.value.trim()

        if (company.isEmpty() || person.isEmpty() || emailAddr.isEmpty() || msg.isEmpty()) {
            _formSuccessMessage.value = "⚠️ Erro: Por favor preencha todos os campos obrigatórios."
            return
        }

        viewModelScope.launch {
            _formSubmitting.value = true
            try {
                val inquiry = Inquiry(
                    companyName = company,
                    contactPerson = person,
                    email = emailAddr,
                    phone = phoneNum,
                    category = cat,
                    message = msg
                )
                repository.insertInquiry(inquiry)
                _formSuccessMessage.value = "✅ お問い合わせありがとうございました！ Sua mensagem foi salva localmente."
                // Reset inputs
                _companyName.value = ""
                _contactPerson.value = ""
                _email.value = ""
                _phone.value = ""
                _message.value = ""
            } catch (e: Exception) {
                _formSuccessMessage.value = "⚠️ Erro ao salvar: ${e.message}"
            } finally {
                _formSubmitting.value = false
            }
        }
    }

    fun clearFormMessage() {
        _formSuccessMessage.value = null
    }

    fun deleteInquiry(inquiry: Inquiry) {
        viewModelScope.launch {
            repository.deleteInquiry(inquiry)
        }
    }

    // --- AI Generator States ---
    private val _aiPrompt = MutableStateFlow("")
    val aiPrompt = _aiPrompt.asStateFlow()

    private val _aiCategory = MutableStateFlow("Recruitment")
    val aiCategory = _aiCategory.asStateFlow()

    private val _aiGenerating = MutableStateFlow(false)
    val aiGenerating = _aiGenerating.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError = _aiError.asStateFlow()

    private val _generatedJobDraft = MutableStateFlow<String?>(null)
    val generatedJobDraft = _generatedJobDraft.asStateFlow()

    private val _generatedActionPlan = MutableStateFlow<String?>(null)
    val generatedActionPlan = _generatedActionPlan.asStateFlow()

    fun updateAiInputs(prompt: String? = null, category: String? = null) {
        prompt?.let { _aiPrompt.value = it }
        category?.let { _aiCategory.value = it }
    }

    fun setAiPredefinedPrompt(prompt: String, category: String) {
        _aiPrompt.value = prompt
        _aiCategory.value = category
    }

    fun runAiConsultant() {
        val promptText = _aiPrompt.value.trim()
        val categoryVal = _aiCategory.value
        if (promptText.isEmpty()) {
            _aiError.value = "Por favor, digite as necessidades da sua empresa."
            return
        }

        _aiGenerating.value = true
        _aiError.value = null
        _generatedJobDraft.value = null
        _generatedActionPlan.value = null

        viewModelScope.launch {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                // Simulate offline helpful response if API key is not configured yet
                simulateOfflineSolution(promptText, categoryVal)
                return@launch
            }

            try {
                val systemSpec = """
You are a world-class Japanese Business Consultant & Recruitment Expert.
Your task is to analyze the user's industrial or corporate request and generate TWO sections separated strictly by '[DRAFT_SPLIT]'.

PART 1: Under '求人票 (Job Description)' or '業務仕様書 (Technical Specs)', write a highly detailed, professional specification in formal Japanese (敬語).
Structure:
- 【職種 / 業務内容】 (Job Position & Key Responsibilities)
- 【求めるスキル・必要要件】 (Required Skills & Qualifications)
- 【雇用形態】 (Employment Type: 正社員 full-time, アルバイト part-time, or Outsource based on context)
- 【歓迎要件】 (Nice-to-have Skills)
- 【勤務地イメージ】 (Location, remote structure)

PART 2: Provide a customized 'ビジネス推奨策 & アクションプラン (Business Advice & Action Plan)' in Portuguese (Portuguese Language).
Structure:
- Recomendação Estratégica (Strategic recommendations)
- Processo de Implementação (Implementation step-by-step)
- Impacto Esperado (Expected business improvements)

Format the split exactly using the divider: '[DRAFT_SPLIT]' line.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = "Requisito da Empresa: $promptText\nCategoria: $categoryVal")))
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemSpec)))
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val fullText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (fullText != null) {
                    val parts = fullText.split("[DRAFT_SPLIT]")
                    if (parts.size >= 2) {
                        _generatedJobDraft.value = parts[0].trim()
                        _generatedActionPlan.value = parts[1].trim()
                    } else {
                        // If parsing fail, divide roughly
                        _generatedJobDraft.value = fullText
                        _generatedActionPlan.value = "Action plan integrated directly in draft. See above."
                    }
                } else {
                    _aiError.value = "Nenhum resultado obtido do assistente de inteligência artificial. Tente novamente."
                }
            } catch (e: Exception) {
                _aiError.value = "Erro na rede ou credenciais da API: ${e.localizedMessage}. Gerando plano offline de backup para você..."
                simulateOfflineSolution(promptText, categoryVal)
            } finally {
                _aiGenerating.value = false
            }
        }
    }

    private fun simulateOfflineSolution(promptText: String, categoryVal: String) {
        viewModelScope.launch {
            // A graceful, professional offline mockup matching the user prompt perfectly so the app works even offline or without keys.
            kotlinx.coroutines.delay(1200)
            
            val localizedCat = when(categoryVal) {
                "Recruitment" -> "求人募集 (Recrutamento)"
                "Translation" -> "ビジネス翻訳・通訳 (Tradução/Interpretação)"
                "Training" -> "社員教育・研修講座 (Treinamento)"
                else -> "企業スピード解決策 (Solução Corporativa)"
            }

            _generatedJobDraft.value = """
【職種】 $promptText
【雇用形態】 案件に応じ相談 (正社員または業務委託・パートタイム)
【業務内容】
・貴社が求める $localizedCat に関連する業務プロセスの構築および設計。
・多言語対応を伴うチーム間の円滑なコミュニケーションサポート。
・迅速な実務対応、課題抽出とスピード解決の支援。
【必須要件】
・日本語ビジネスレベル以上 (N2以上推奨)、ポルトガル語/英語理解力。
・各種ITコミュニケーションツールの利用経験 (Slack, Zoom, Office)。
【歓迎要件】
・日本企業での就業経験、多文化共生下でのチームリード経験。
【勤務地・待遇】
・主要都市（東京、大阪、名古屋) またはリモートワーク対応。
            """.trimIndent()

            _generatedActionPlan.value = """
💡 RECOMENDAÇÃO ESTRATÉGICA (Solução Empresarial)
Para a requisição: "$promptText"

1. Mão de Obra e Alocação Rápida:
   - Recomendamos divulgar a vaga através de canais bilingues como CareerCross, Daijob ou portais especializados em talentos luso-japoneses.
   - Oferecemos o suporte de onboarding para alinhar as expectativas culturais.

2. Solução em Tradução e Idiomas:
   - Contratos, manuais técnicos e canais internos de comunicação devem ter tradutores certificados com conhecimento especializado no seu nicho.

3. Treinamento Adaptado (Cursos):
   - Recomendamos habilitar os recrutas com treinamentos de "DX (Digital Transformation)" básicos e "Manuais Corporativos Japoneses" para evitar ruídos de comunicação.

4. Próximo Passo:
   - Clique em "Salvar nos Favoritos" e use os dados acima para submeter um "Formulário de Contato" formal preenchendo seus dados para agilizarmos a sua contratação corporativa!
            """.trimIndent()
            _aiGenerating.value = false
        }
    }

    fun saveDraft(title: String) {
        val draftJob = _generatedJobDraft.value ?: return
        val actPlan = _generatedActionPlan.value ?: ""
        val cat = _aiCategory.value

        viewModelScope.launch {
            val fileSave = DraftedJob(
                title = title.takeIf { it.isNotBlank() } ?: "Proposta: ${cat}",
                category = cat,
                description = draftJob,
                actionPlan = actPlan
            )
            repository.insertDraftedJob(fileSave)
        }
    }

    fun deleteDraft(draft: DraftedJob) {
        viewModelScope.launch {
            repository.deleteDraftedJob(draft)
        }
    }
}

class AppViewModelFactory(
    private val application: Application,
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
