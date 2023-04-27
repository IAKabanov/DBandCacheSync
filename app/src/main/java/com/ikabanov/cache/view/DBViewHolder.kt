package com.ikabanov.cache.view

import android.view.View
import android.widget.TextView
import com.amrdeveloper.treeview.TreeNode
import com.amrdeveloper.treeview.TreeViewHolder
import com.ikabanov.cache.R
import com.ikabanov.cache.data.db.Element

class DBViewHolder(itemView: View, private val expandStr: String, private val collapseStr: String) :
    TreeViewHolder(itemView) {
    private var name: TextView
    private var expand: TextView

    init {
        name = itemView.findViewById(R.id.node_name)
        expand = itemView.findViewById(R.id.expand)
    }

    override fun bindTreeNode(node: TreeNode?) {
        super.bindTreeNode(node)
        val fileNameStr = (node!!.value as Element).name
        if (node.children.isEmpty()) {
            expand.visibility = View.GONE
        } else {
            expand.visibility = View.VISIBLE
            expand.text = if (node.isExpanded) collapseStr else expandStr
        }
        val styledText = StringBuffer()

        if ((node.value as Element).deleted) {
            styledText.append("(D)")
        }

        styledText.append(fileNameStr)
        name.text = styledText
    }
}