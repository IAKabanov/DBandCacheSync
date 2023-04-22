package com.ikabanov.cache.view

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amrdeveloper.treeview.TreeNode
import com.amrdeveloper.treeview.TreeViewAdapter
import com.amrdeveloper.treeview.TreeViewHolderFactory
import com.ikabanov.cache.*
import com.ikabanov.cache.data.cache.Cache
import com.ikabanov.cache.data.cache.ElementCache
import com.ikabanov.cache.data.cache.ICacheContract
import com.ikabanov.cache.data.db.Element
import com.ikabanov.cache.domain.*

class CacheFragment : Fragment(), ICacheFragmentInteraction, ICacheFragmentReceiver {
    val cache: ICacheContract = Cache
    private var treeViewAdapter: TreeViewAdapter? = null
    private var deleteMode = false
    private var alterMode = false
    private lateinit var dialogEditText: EditText
    private lateinit var dialogTitle: TextView
    private var dialog: AlertDialog? = null
    private var selectedTreeNodeWrapper: TreeNodeWrapper? = null
    var presenter: MainActivityPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dialogEditText = EditText(activity)
        val mlp = MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT)
        mlp.setMargins(10, 0, 10, 0)
        dialogEditText.layoutParams = mlp
        dialogTitle = TextView(activity)
        val linearLayout = LinearLayout(activity)
        linearLayout.addView(dialogTitle)
        linearLayout.addView(dialogEditText)
        linearLayout.orientation = LinearLayout.VERTICAL
        dialog = createDialog(activity, linearLayout)
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
        val factory =
            TreeViewHolderFactory { v: View, _: Int -> CacheViewHolder(v) }
        treeViewAdapter = TreeViewAdapter(factory)
        recyclerView.adapter = treeViewAdapter

        treeViewAdapter!!.setTreeNodeLongClickListener { treeNode: TreeNode, nodeView: View? ->
            presenter?.selectedInCache(treeNode as TreeNodeWrapper)

            //selectedTreeNodeWrapper = treeNode as TreeNodeWrapper

            //onLongClickOnTreeItem()
            true
        }
        return view
    }

    override fun onTreeViewReceived(element: Element, level: Int) {
        cache.add(element, level)
        refresh()
    }

    fun refresh() {
        val elementsSorted = cache.getElementsWithTreeRelations()
        treeViewAdapter!!.updateTreeNodes(fillViewTree(elementsSorted))
    }

    private fun fillViewTree(elements: List<ElementCache>): List<TreeNode> {
        val roots: MutableList<TreeNode> = ArrayList()
        for (element in elements) {
            if (!element.hasLocalParent) { // Find an element, that has no local parent. So it
                // would be shown as a root. There might be more than one roots.
                roots.add(getViewOutOfRootElement(element))
            }
        }
        return roots
    }

    private fun getViewOutOfRootElement(element: ElementCache): TreeNodeWrapper {
        val root = TreeNodeWrapper(element)
        for (child in element.children) { // Add children for taken element. Recursively.
            root.addChild(getViewOutOfRootElement(child))
        }
        return root
    }

    override fun addElement(reason: Reason) {
    }

    override fun deleteElement(reason: Reason) {
    }

    override fun alterElement(reason: Reason) {
        alterMode = !alterMode
        handleAlterModeState(alterMode, reason)
    }

    override fun applyCache(reason: Reason) {
    }

    private fun handleAlterModeState(
        enabled: Boolean,
        reason: Reason,
        customMessage: String? = null
    ) {
        (activity as ICacheButtonsNotifier).notifyAlterModeEnabled(enabled)
    }

    override fun reset() {
    }

    fun setDialogTitle(title: String) {
        dialogTitle.text = title
    }

    fun setDialogTitle(title: Int) {
        dialogTitle.setText(title)
    }

    fun showDialog() {
        dialog?.show()
    }

    private fun createDialog(activity: Activity?, viewGroup: ViewGroup): AlertDialog? {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.dialog_element)
            .setView(viewGroup)
            .setPositiveButton(
                resources.getText(R.string.positive_answer)
            ) { _, _ ->
                presenter?.nameEntered(dialogEditText.text.toString())
                dialogEditText.text.clear()
            }
            .setNegativeButton(
                resources.getText(R.string.negative_answer)
            ) { _, _ ->
                presenter?.nameEntered(null)
                dialogEditText.text.clear()
            }
        return builder.create()
    }
}