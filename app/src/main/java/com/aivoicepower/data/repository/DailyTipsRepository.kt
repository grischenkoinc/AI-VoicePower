package com.aivoicepower.data.repository

import android.content.Context
import com.aivoicepower.domain.model.home.DailyTip
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class DailyTipsJson(
    @SerializedName("tips")
    val tips: List<DailyTipJson>
)

data class DailyTipJson(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String
)

@Singleton
class DailyTipsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private var cachedTips: List<DailyTip>? = null

    fun loadTips(): List<DailyTip> {
        // Повертаємо кешовані дані якщо є
        cachedTips?.let { return it }

        // Читаємо з JSON файлу
        val jsonString = context.assets.open("daily_tips.json")
            .bufferedReader()
            .use { it.readText() }

        val tipsJson = gson.fromJson(jsonString, DailyTipsJson::class.java)

        val tips = tipsJson.tips.map { tipJson ->
            DailyTip(
                id = tipJson.id,
                title = tipJson.title,
                content = tipJson.content,
                date = "" // Дата буде встановлена пізніше
            )
        }

        // Кешуємо
        cachedTips = tips
        return tips
    }

    fun getRandomTip(seed: Long): DailyTip {
        val tips = loadTips()
        if (tips.isEmpty()) {
            return DailyTip(
                id = "default",
                title = "Порада дня",
                content = "Розповідай історії замість фактів",
                date = ""
            )
        }

        // Використовуємо seed для стабільного вибору
        val index = (seed % tips.size).toInt()
        return tips[index]
    }
}
