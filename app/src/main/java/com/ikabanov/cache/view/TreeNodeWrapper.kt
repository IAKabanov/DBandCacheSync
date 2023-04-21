package com.ikabanov.cache.view

import com.amrdeveloper.treeview.TreeNode
import com.ikabanov.cache.R

class TreeNodeWrapper(value: Any?, layoutId: Int) : TreeNode(value, layoutId) {

    constructor(value: Any?) : this(value, defaultLayoutId)

    companion object {
        const val defaultLayoutId = R.layout.list_item
    }
}