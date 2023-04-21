package com.ikabanov.cache.domain

import com.ikabanov.cache.data.cache.ElementCache

interface IdbFragmentReceiver {
    fun onElementsReceived(elements: List<ElementCache>)
}