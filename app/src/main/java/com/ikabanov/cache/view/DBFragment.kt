package com.ikabanov.cache.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amrdeveloper.treeview.TreeNode
import com.amrdeveloper.treeview.TreeViewAdapter
import com.amrdeveloper.treeview.TreeViewHolderFactory
import com.ikabanov.cache.R

class DBFragment : Fragment() {
    private var treeViewAdapter: TreeViewAdapter? = null
    var presenter: MainActivityPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_db, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.database)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.isNestedScrollingEnabled = true
        val expand = resources.getText(R.string.expand).toString()
        val collapse = resources.getText(R.string.collapse).toString()
        val noChildren = resources.getText(R.string.no_child).toString()
        val factory =
            TreeViewHolderFactory { v: View, _: Int -> DBViewHolder(v, expand, collapse, noChildren) }
        treeViewAdapter = TreeViewAdapter(factory)
        recyclerView.adapter = treeViewAdapter

        refresh()
        treeViewAdapter!!.setTreeNodeLongClickListener { treeNode: TreeNode, _: View? ->
            sendToCache(treeNode as TreeNodeWrapper)
            true
        }
        return view
    }

    private fun sendToCache(treeNode: TreeNodeWrapper?) {
        if ((treeNode != null) && (presenter != null)) {
            presenter!!.onSendToCacheTriggered(treeNode)
        }
    }

    /**
     * refresh is good for use when DB was updated.
     */
    fun refresh() {
        if (presenter != null) {
            treeViewAdapter!!.updateTreeNodes(presenter!!.fillDBViewTree())
            treeViewAdapter?.expandAll()
        }
    }
}