package com.ikabanov.cache.view

import com.amrdeveloper.treeview.TreeNode
import com.ikabanov.cache.Mode
import com.ikabanov.cache.R
import com.ikabanov.cache.Reason
import com.ikabanov.cache.data.cache.ElementCache
import com.ikabanov.cache.data.db.Element
import com.ikabanov.cache.domain.*

class MainActivityPresenter {
    private var mainActivity: MainActivity? = null
    private var dbFragment: DBFragment? = null
    private var cacheFragment: CacheFragment? = null
    private var dbInteractor: IDBInteractor = DBInteractor()
    private var cacheInteractor: ICacheInteractor = CacheInteractor()
    private var selectedTreeNodeWrapper: TreeNodeWrapper? = null
    private var mode = Mode.NONE
    private val PASS: Unit = Unit

    fun addMainViews(
        mainActivity: MainActivity,
        dbFragment: DBFragment,
        cacheFragment: CacheFragment
    ) {
        this.mainActivity = mainActivity
        this.dbFragment = dbFragment
        this.cacheFragment = cacheFragment
        this.dbFragment!!.presenter = this
        this.cacheFragment!!.presenter = this
    }

    fun resetDB() {
        if (dbFragment != null) {
            dbInteractor.resetDB()
            dbFragment!!.refresh()
        }
    }

    fun fillViewTree(): List<TreeNode> {
        val elementsSorted = dbInteractor.getElementsWithTreeRelations()
        return fillViewTree(elementsSorted)
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

    private fun onElementsReceived(elements: List<ElementCache>) {
        if (dbFragment != null) {
            dbFragment!!.refresh()
        }
    }


    fun onSendToCacheTriggered(node: TreeNodeWrapper) {
        cacheFragment!!.onTreeViewReceived(node.value as Element, node.level)
    }

    fun applyCache() {
        val elementsSorted = cacheInteractor.getElementsWithTreeRelations()
        dbInteractor.addManyElements(elementsSorted)
        dbFragment?.refresh()
        resetCache()
    }

    private fun createMessage(mode: Mode, reason: Reason): String {
        return when (mode) {
            Mode.ADDITION -> when (reason) {
                Reason.DONE -> "New node added in cache successfully."
                Reason.DONE_NEGATIVE -> "How did you get there?!"
                Reason.ABORTED -> "No nodes added."
                Reason.NONE -> "Adding enabled. Push the node, child will be created."
            }
            Mode.EDITION -> when (reason) {
                Reason.DONE -> "Node name changed in cache successfully"
                Reason.DONE_NEGATIVE -> "How did you get there?!"
                Reason.ABORTED -> "No nodes renamed"
                Reason.NONE -> "Altering enabled. Push the node, it will be able to be edited."
            }
            Mode.DELETION -> when (reason) {
                Reason.DONE -> "Node marked as deleted in cache successfully."
                Reason.DONE_NEGATIVE -> "Node unmarked as deleted in cache successfully."
                Reason.ABORTED -> "No nodes marked (unmarked) as deleted."
                Reason.NONE -> "Deleting enabled. Push the node, it will be marked as deleted."
            }
            Mode.NONE -> ""
        }
    }

    fun resetCache() {
        cacheInteractor.clearCache()
        cacheFragment?.refresh()

        when (mode) {
            Mode.ADDITION -> mainActivity?.notifyAddModeEnabled(false)
            Mode.EDITION -> mainActivity?.notifyAlterModeEnabled(false)
            Mode.DELETION -> mainActivity?.notifyDeleteModeEnabled(false)
            Mode.NONE -> PASS // Nothing should happen.
        }
        mainActivity?.showToast(createMessage(Mode.NONE, Reason.ABORTED))
        mode = Mode.NONE
    }

    fun addElement(reason: Reason = Reason.NONE) {
        mainActivity?.showToast(createMessage(mode, reason))
        mode = if (mode == Mode.ADDITION) Mode.NONE else Mode.ADDITION
        val add = mode == Mode.ADDITION
        mainActivity?.notifyAddModeEnabled(add)
    }

    private fun addElement(elementName: String): Reason {
        if (selectedTreeNodeWrapper != null) {
            val elementParent = selectedTreeNodeWrapper!!.value as ElementCache
            return cacheInteractor.addElement(
                Element(elementName, Element(elementParent.name, null)),
                elementParent.level + 1
            )
        }
        return Reason.ABORTED
    }

    private fun alterElement(elementName: String): Reason {
        if (selectedTreeNodeWrapper != null) {
            val elementParent = selectedTreeNodeWrapper!!.value as ElementCache
            return cacheInteractor.editElement(
                ElementCache(elementParent.name, elementParent.parentName),
                elementName
            )
        }
        return Reason.ABORTED
    }

    fun deleteElement(reason: Reason = Reason.NONE) {
        mainActivity?.showToast(createMessage(mode, reason))
        mode = if (mode == Mode.DELETION) Mode.NONE else Mode.DELETION
        val del = mode == Mode.DELETION
        mainActivity?.notifyDeleteModeEnabled(del)
    }

    fun alterElement(reason: Reason = Reason.NONE) {
        mainActivity?.showToast(createMessage(mode, reason))
        mode = if (mode == Mode.EDITION) Mode.NONE else Mode.EDITION
        val edit = mode == Mode.EDITION
        mainActivity?.notifyAlterModeEnabled(edit)
    }

    fun selectedInCache(node: TreeNodeWrapper) {
        selectedTreeNodeWrapper = node
        when (mode) {
            Mode.ADDITION -> {
                cacheFragment?.setDialogTitle(R.string.dialog_add)
                cacheFragment?.showDialog()
            }
            Mode.EDITION -> {
                cacheFragment?.setDialogTitle(R.string.dialog_edit)
                cacheFragment?.showDialog()
            }
            Mode.DELETION -> {
                if (selectedTreeNodeWrapper != null) {
                    val reason =
                        cacheInteractor.deleteElement(selectedTreeNodeWrapper!!.value as ElementCache)
                    deleteElement(reason)
                    selectedTreeNodeWrapper = null
                    cacheFragment?.refresh()
                }

            }
            Mode.NONE -> PASS
        }
    }

    fun nameEntered(name: String?) {
        when (mode) {
            Mode.ADDITION -> {
                if (name != null) {
                    val reason = addElement(name)
                    cacheFragment?.refresh()
                    addElement(reason)
                } else {
                    addElement(Reason.ABORTED)
                }
            }
            Mode.EDITION -> {
                if (name != null) {
                    val reason = alterElement(name)
                    cacheFragment?.refresh()
                    alterElement(reason)
                } else {
                    alterElement(Reason.ABORTED)
                }
            }
            Mode.DELETION -> PASS
            Mode.NONE -> PASS
        }
        selectedTreeNodeWrapper = null
    }
}