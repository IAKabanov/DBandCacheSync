package com.ikabanov.cache.view

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amrdeveloper.treeview.TreeNode
import com.amrdeveloper.treeview.TreeViewAdapter
import com.amrdeveloper.treeview.TreeViewHolderFactory
import com.ikabanov.cache.*
import com.ikabanov.cache.data.cache.Cache
import com.ikabanov.cache.data.cache.ICacheContract

class CacheFragment : Fragment() {
    val cache: ICacheContract = Cache
    private var treeViewAdapter: TreeViewAdapter? = null
    private lateinit var dialogEditText: EditText
    private lateinit var dialogTitle: TextView
    private var dialog: AlertDialog? = null
    var presenter: MainActivityPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dialogEditText = EditText(activity)
        val mlp =
            MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT)
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
        val expand = resources.getText(R.string.expand).toString()
        val collapse = resources.getText(R.string.collapse).toString()
        val noChildren = resources.getText(R.string.no_child).toString()
        val factory =
            TreeViewHolderFactory { v: View, _: Int -> CacheViewHolder(v, expand, collapse, noChildren) }
        treeViewAdapter = TreeViewAdapter(factory)
        recyclerView.adapter = treeViewAdapter

        treeViewAdapter!!.setTreeNodeLongClickListener { treeNode: TreeNode, _: View? ->
            presenter?.selectedInCache(treeNode as TreeNodeWrapper)
            true
        }
        return view
    }

    /**
     * refresh is needed when Cache has been changed.
     */
    fun refresh() {
        treeViewAdapter?.updateTreeNodes(presenter?.fillCacheViewTree())
        treeViewAdapter?.expandAll()
    }

    /**
     * setDialogTitle is used when mode is changed.
     * @param title is the reference to the resource, that would be a source of text to be added
     * in title.
     */
    fun setDialogTitle(title: Int) {
        dialogTitle.setText(title)
    }

    /**
     * showDialog shows the dialog when adding or editing enabled.
     */
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