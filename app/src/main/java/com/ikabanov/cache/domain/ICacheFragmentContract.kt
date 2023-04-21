package com.ikabanov.cache.domain

import com.ikabanov.cache.data.cache.ElementCache

interface ICacheFragmentContract {
    fun onCacheApplied(elements: List<ElementCache>)

}