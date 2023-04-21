package com.ikabanov.cache.cache

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
import com.ikabanov.cache.view.CacheViewHolder
import com.ikabanov.cache.view.TreeNodeWrapper

enum class Reason {
    DONE,
    DONE_NEGATIVE,
    ABORTED,
    NONE,
    CUSTOM
}

class CacheFragment : Fragment(), ICacheFragmentReceiver, ICacheFragmentInteraction {
    val cache: ICacheContract = Cache
    private var treeViewAdapter: TreeViewAdapter? = null
    private var addMode = false
    private var deleteMode = false
    private var alterMode = false
    private val TAG = "CacheFragment"
    private lateinit var dialogEditText: EditText
    private lateinit var dialogTitle: TextView
    private var dialog: AlertDialog? = null
    private var selectedTreeNodeWrapper: TreeNodeWrapper? = null

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
        recyclerView.isNestedScrollingEnabled = false
        val factory =
            TreeViewHolderFactory { v: View, _: Int -> CacheViewHolder(v) }
        treeViewAdapter = TreeViewAdapter(factory)
        recyclerView.adapter = treeViewAdapter

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
            selectedTreeNodeWrapper = treeNode as TreeNodeWrapper
            onLongClickOnTreeItem()

            true
        }
        return view
    }

    override fun onTreeViewReceived(element: Element, level: Int) {
        cache.add(element, level)
        refresh()
    }

    private fun refresh() {
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
        addMode = !addMode
        handleAddModeState(addMode, reason)
    }

    private fun handleAddModeState(
        enabled: Boolean,
        reason: Reason,
        customMessage: String? = null
    ) {
        val message = when (reason) {
            Reason.DONE -> "New node added in cache successfully"
            Reason.DONE_NEGATIVE -> "How did you get there?!"
            Reason.ABORTED -> "No nodes added"
            Reason.NONE -> if (enabled) "Adding enabled. Push the node, child will be created." else "Adding disabled."
            Reason.CUSTOM -> customMessage
        }
        Toast.makeText(requireActivity().applicationContext, message, Toast.LENGTH_SHORT).show()
        (activity as ICacheButtonsNotifier).notifyAddModeEnabled(enabled)
    }

    override fun deleteElement(reason: Reason) {
        deleteMode = !deleteMode
        handleDeleteModeState(deleteMode, reason)
    }

    private fun handleDeleteModeState(
        enabled: Boolean,
        reason: Reason,
        customMessage: String? = null
    ) {
        val message = when (reason) {
            Reason.DONE -> "Node marked as deleted in cache successfully"
            Reason.DONE_NEGATIVE -> "Node unmarked as deleted in cache successfully"
            Reason.ABORTED -> "No nodes marked (unmarked) as deleted"
            Reason.NONE -> if (enabled) "Deleting enabled. Push the node, it will be marked as deleted." else "Deleting disabled."
            Reason.CUSTOM -> customMessage
        }
        Toast.makeText(requireActivity().applicationContext, message, Toast.LENGTH_SHORT).show()
        (activity as ICacheButtonsNotifier).notifyDeleteModeEnabled(enabled)
    }

    override fun alterElement(reason: Reason) {
        alterMode = !alterMode
        handleAlterModeState(alterMode, reason)
    }

    private fun handleAlterModeState(
        enabled: Boolean,
        reason: Reason,
        customMessage: String? = null
    ) {
        val message = when (reason) {
            Reason.DONE -> "Node name changed in cache successfully"
            Reason.DONE_NEGATIVE -> "How did you get there?!"
            Reason.ABORTED -> "No nodes renamed"
            Reason.NONE -> if (enabled) "Altering enabled. Push the node, it will be able to be edited." else "Altering disabled."
            Reason.CUSTOM -> customMessage
        }
        Toast.makeText(requireActivity().applicationContext, message, Toast.LENGTH_SHORT).show()
        (activity as ICacheButtonsNotifier).notifyAlterModeEnabled(enabled)
    }

    override fun applyCache(reason: Reason) {
        (activity as ICacheFragmentContract).onCacheApplied(cache.getElementsWithTreeRelations())
        reset()
    }

    override fun reset() {
        cache.clearCache()
        refresh()
        val needToShowMessageAdd = addMode
        if (needToShowMessageAdd) {
            addMode = false
            (activity as ICacheButtonsNotifier).notifyAddModeEnabled(addMode)
            handleAddModeState(addMode, Reason.ABORTED)
        }

        val needToShowMessageDelete = deleteMode
        if (needToShowMessageDelete) {
            deleteMode = false
            (activity as ICacheButtonsNotifier).notifyDeleteModeEnabled(addMode)
            handleAddModeState(deleteMode, Reason.ABORTED)
        }
    }

    private fun onLongClickOnTreeItem() {
        if (addMode) {
            dialogTitle.setText(R.string.dialog_add)
            dialog?.show()
        }

        if (deleteMode) {
            val result = Cache.delete(selectedTreeNodeWrapper!!.value as ElementCache)
            refresh()
            deleteElement(result)
            selectedTreeNodeWrapper = null
        }

        if (alterMode) {
            dialogEditText.text = SpannableStringBuilder((selectedTreeNodeWrapper!!.value as ElementCache).name)
            dialogTitle.setText(R.string.dialog_edit)
            dialog?.show()
        }
    }

    private fun addElement(elementName: String): Reason {
        if (selectedTreeNodeWrapper != null) {
            val elementParent = selectedTreeNodeWrapper!!.value as ElementCache
            val reason = cache.add(
                Element(elementName, Element(elementParent.name, null)),
                elementParent.level + 1, false
            )
            refresh()
            return reason
        }
        return Reason.ABORTED
    }

    private fun createDialog(activity: Activity?, viewGroup: ViewGroup): AlertDialog? {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.dialog_element)
            .setView(viewGroup)
            .setPositiveButton(
                resources.getText(R.string.positive_answer)
            ) { _, _ ->
                if (addMode) {
                    val reason = addElement(dialogEditText.text.toString())
                    addElement(reason)
                }
                if (alterMode) {
                    val reason = alterElement(dialogEditText.text.toString())
                    alterElement(reason)
                }
                dialogEditText.text.clear()
                selectedTreeNodeWrapper = null
            }
            .setNegativeButton(
                resources.getText(R.string.negative_answer)
            ) { _, _ ->
                if (addMode) {
                    addElement(Reason.ABORTED)
                }
                if (alterMode) {
                    alterElement(Reason.ABORTED)
                }
                dialogEditText.text.clear()
                selectedTreeNodeWrapper = null
            }
        return builder.create()
    }

    private fun alterElement(elementName: String): Reason {
        if (selectedTreeNodeWrapper != null) {
            val elementParent = selectedTreeNodeWrapper!!.value as ElementCache
            val reason = cache.alter(
                ElementCache(elementParent.name, elementParent.parentName),
                elementName
            )
            refresh()
            return reason
        }
        return Reason.ABORTED
    }
}