package com.ikabanov.cache.data.db

object DB : IdbContract {
    private val listOfNodes = mutableMapOf<Element, Element>()

    init {
        listOfNodes.putAll(DBFiller.createRoot())
    }

    override fun addElement(element: Element, parent: Element) {
        if (listOfNodes[element] != null) {
            return
        }

        val realParent = listOfNodes[parent]
        if (realParent != null) {
            realParent.addChild(element)
            listOfNodes[element] = element
        }
    }

    override fun updateElement(oldElement: Element, newElement: Element) {
        val elementToBeUpdated = listOfNodes[oldElement]
        if (elementToBeUpdated != null) {
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
    }

    override fun resetDB() {
        listOfNodes.clear()
        listOfNodes.putAll(DBFiller.createRoot())
    }

    override fun getElementsWithTreeRelations(): List<Element> {
        val elements = listOfNodes.keys.toMutableList().sorted()  // Sorted by its level in the tree. We need that for
                                                        // easier finding its relations.
        return elements
    }
}