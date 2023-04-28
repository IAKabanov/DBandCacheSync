package com.ikabanov.cache.data.db

/**
 * DB is object that contains Elements. It represents a database. In this task work is did in Cache
 * and than merges here.
 */
object DB : IdbContract {
    private val listOfNodes = mutableListOf<Element>()
    private var lastID = 0

    init {
        listOfNodes.addAll(DBFiller.createRoot())
        for (element in listOfNodes) {
            refreshID(element.id)
        }
    }

    private fun refreshID(id: Int) {
        if (id > lastID) {
            lastID = id
        }
    }

    override fun addElement(element: Element, parent: Element) {
        if (listOfNodes.contains(element)) {    // We cannot add existing element.
            return
        }
        val realParentIndex = listOfNodes.indexOf(parent)
        if (realParentIndex == Element.NO_ID) { // We cannot add second root.
            return
        }
        val realParent = listOfNodes[realParentIndex]   // Find the parent in db.
        realParent.addChild(element)    // Add the element to children list in the parent.
        if (realParent.deleted) {   // If its parent is deleted but we add the element, we will add it
            element.deleted = true  // but mark it deleted like its parent.
        }
        listOfNodes.add(element)
        refreshID(element.id)
    }

    override fun updateElement(oldElement: Element, newElement: Element) {
        val elementToBeUpdatedIndex = listOfNodes.indexOf(oldElement)   // Find the element in
                                                        // the database, that should be changed.
        if (elementToBeUpdatedIndex == -1) {    // If there is no such element, we cannot edit it.
            return
        }
        val elementToBeUpdated = listOfNodes[elementToBeUpdatedIndex]
        elementToBeUpdated.name = newElement.name
        elementToBeUpdated.deleted = newElement.deleted

        val parentElementToBeUpdated = elementToBeUpdated.parent
        if (parentElementToBeUpdated != null && parentElementToBeUpdated.deleted) { // If parent is deleted
            elementToBeUpdated.deleted = true   // we need to delete the element.
        }

        if (elementToBeUpdated.deleted) {   // If the element is deleted, we need to delete its children.
            for (element in elementToBeUpdated.children) {
                val toBeDeleted = Element(element.id, element.name)
                toBeDeleted.deleted = true
                updateElement(element, toBeDeleted) // Delete children.
            }
        }
    }

    override fun resetDB() {
        listOfNodes.clear()
        listOfNodes.addAll(DBFiller.createRoot())
        for (element in listOfNodes) {
            refreshID(element.id)
        }
    }

    override fun getElements(): List<Element> {
        return listOfNodes.toList()
    }

    override fun getLastID(): Int {
        return lastID
    }
}