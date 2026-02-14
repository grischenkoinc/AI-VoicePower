package com.aivoicepower.data.repository

import com.aivoicepower.data.content.TipsProvider
import com.aivoicepower.domain.model.home.DailyTip
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyTipsRepository @Inject constructor() {
    private var cachedTips: List<DailyTip>? = null

    /** Cold start flag — resets when process dies */
    var isColdStart: Boolean = true
        private set

    fun markColdStartHandled() {
        isColdStart = false
    }

    fun loadTips(): List<DailyTip> {
        cachedTips?.let { return it }

        val tips = TipsProvider.getAllTips().mapIndexed { index, tip ->
            DailyTip(
                id = "tip_$index",
                title = tip.author ?: "Порада дня",
                content = tip.text,
                date = ""
            )
        }

        cachedTips = tips
        return tips
    }

    fun getRandomTip(excludeId: String? = null): DailyTip {
        val tips = loadTips()
        if (tips.isEmpty()) {
            return DailyTip(
                id = "default",
                title = "Порада дня",
                content = "Розповідай історії замість фактів",
                date = ""
            )
        }

        val candidates = if (excludeId != null && tips.size > 1) {
            tips.filter { it.id != excludeId }
        } else {
            tips
        }

        return candidates.random()
    }
}
