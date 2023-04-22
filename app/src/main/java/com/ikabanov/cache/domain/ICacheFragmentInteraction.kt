package com.ikabanov.cache.domain

import com.ikabanov.cache.Reason

interface ICacheFragmentInteraction {
    fun addElement(reason: Reason = Reason.NONE)
    fun deleteElement(reason: Reason = Reason.NONE)
    fun alterElement(reason: Reason = Reason.NONE)
    fun applyCache(reason: Reason = Reason.NONE)
    fun reset()
}