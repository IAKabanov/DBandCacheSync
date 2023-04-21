package com.ikabanov.cache.domain

import com.ikabanov.cache.data.db.Element

interface ICacheFragmentReceiver {
    fun onTreeViewReceived(element: Element, level: Int)
}