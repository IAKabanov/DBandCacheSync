package com.ikabanov.cache.view

import android.view.View
import android.widget.TextView
import com.amrdeveloper.treeview.TreeNode
import com.amrdeveloper.treeview.TreeViewHolder
import com.ikabanov.cache.R
import com.ikabanov.cache.data.cache.ElementCache


class CacheViewHolder(itemView: View) : TreeViewHolder(itemView) {

    private var name: TextView
    private var expand: TextView

    init {
        name = itemView.findViewById(R.id.node_name)
        expand = itemView.findViewById(R.id.expand)
    }

    override fun bindTreeNode(node: TreeNode?) {
        super.bindTreeNode(node)
        val element = (node!!.value as ElementCache)
        val nameOfNode = if (element.newName != null) element.newName else element.name
        val styledText = StringBuffer()

        if ((node.value as ElementCache).deleted) {
            styledText.append("(D)")
        }

        styledText.append(nameOfNode)
        name.text = styledText
    }
}