package com.ikabanov.cache.domain

import com.ikabanov.cache.view.TreeNodeWrapper

interface IdbFragmentContract {
    fun onSendToCacheTriggered(node: TreeNodeWrapper)
}