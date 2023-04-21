package com.ikabanov.cache.view

import android.os.Bundle
import android.util.Log
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
import com.ikabanov.cache.data.cache.ElementCache
import com.ikabanov.cache.data.db.DB
import com.ikabanov.cache.data.db.Element
import com.ikabanov.cache.data.db.IdbContract
import com.ikabanov.cache.domain.IResetView
import com.ikabanov.cache.domain.IdbFragmentContract
import com.ikabanov.cache.domain.IdbFragmentReceiver

class DBFragment : Fragment(), IResetView, IdbFragmentReceiver {
    private var treeViewAdapter: TreeViewAdapter? = null
    private val db: IdbContract = DB


    private val TAG = "FileTreeFragment"

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
        recyclerView.isNestedScrollingEnabled = false
        val factory =
            TreeViewHolderFactory { v: View, _: Int -> DBViewHolder(v) }
        treeViewAdapter = TreeViewAdapter(factory)
        recyclerView.adapter = treeViewAdapter

        refresh()
        treeViewAdapter!!.setTreeNodeClickListener { treeNode: TreeNode, nodeView: View? ->
            Log.d(
                TAG,
                "Click on TreeNode with value " + treeNode.value.toString()
            )
        }
        treeViewAdapter!!.setTreeNodeLongClickListener { treeNode: TreeNode, nodeView: View? ->
            Log.d(
                TAG,
                "LongClick on TreeNode with value " + treeNode.value.toString()
            )
            sendToCache(treeNode as TreeNodeWrapper)
            true
        }
        return view
    }

    private fun sendToCache(treeNode: TreeNodeWrapper?) {
        if (treeNode != null) {
            (activity as IdbFragmentContract).onSendToCacheTriggered(treeNode)
        }
    }

    private fun refresh() {
        val elementsSorted = db.getElementsWithTreeRelations()
        treeViewAdapter!!.updateTreeNodes(fillViewTree(elementsSorted))
    }

    private fun fillViewTree(elements: List<Element>): List<TreeNode> {
        val roots: MutableList<TreeNode> = ArrayList()
        for (element in elements) {
            if (element.parent == null) { // Find an element, that has no local parent. So it
                // would be shown as a root. There might be more than one roots.
                roots.add(getViewOutOfRootElement(element))
            }
        }
        return roots
    }

    private fun getViewOutOfRootElement(element: Element): TreeNodeWrapper {
        val root = TreeNodeWrapper(element)
        for (child in element.children) { // Add children for taken element. Recursively.
            root.addChild(getViewOutOfRootElement(child))
        }
        return root
    }

    override fun reset() {
        db.resetDB()
        refresh()
    }

    override fun onElementsReceived(elements: List<ElementCache>) {
        for (element in elements) {
            if (element.modified) {
                val oldElement = Element(element.name)
                val newElement = Element(element.newName ?: element.name)
                newElement.deleted = element.deleted
                db.updateElement(oldElement, newElement)
            }
            if (element.newElement) {
                if (!element.deleted) {
                    val newElement = Element(element.newName ?: element.name)
                    val parent = Element(element.parentName)
                    db.addElement(newElement, parent)
                }
            }
        }
        refresh()
    }
}