package com.ikabanov.cache.data.db

object DB : IdbContract {
    private val listOfNodes = mutableListOf<Element>()

    init {
        listOfNodes.addAll(DBFiller.createRoot())
    }

    override fun addElement(element: Element, parent: Element) {
        if (listOfNodes.contains(element)) {
            return
        }

        val realParentIndex = listOfNodes.indexOf(parent)
        if (realParentIndex == -1) {
            return
        }
        val realParent = listOfNodes[realParentIndex]
        realParent.addChild(element)
        listOfNodes.add(element)
    }

    override fun updateElement(oldElement: Element, newElement: Element) {
        val elementToBeUpdatedIndex = listOfNodes.indexOf(oldElement)
        if (elementToBeUpdatedIndex == -1) {
            return
        }
        val elementToBeUpdated = listOfNodes[elementToBeUpdatedIndex]
        elementToBeUpdated.name = newElement.name
        elementToBeUpdated.deleted = newElement.deleted
        if (elementToBeUpdated.deleted) {
            for (element in elementToBeUpdated.children) {
                val toBeDeleted = Element(element.name)
                toBeDeleted.deleted = true
                updateElement(element, toBeDeleted)
            }
        }
        val parentElementToBeUpdated = elementToBeUpdated.parent
        if (parentElementToBeUpdated != null && parentElementToBeUpdated.deleted) {
            elementToBeUpdated.deleted = true

        }
    }

    override fun resetDB() {
        listOfNodes.clear()
        listOfNodes.addAll(DBFiller.createRoot())
    }

    override fun getElements(): List<Element> {
        return listOfNodes.toList()
    }
}