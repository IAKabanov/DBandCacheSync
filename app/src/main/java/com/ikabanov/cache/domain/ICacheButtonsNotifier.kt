package com.ikabanov.cache.domain

interface ICacheButtonsNotifier {
    fun notifyAddModeEnabled(enabled: Boolean)
    fun notifyDeleteModeEnabled(enabled: Boolean)
    fun notifyAlterModeEnabled(enabled: Boolean)
}