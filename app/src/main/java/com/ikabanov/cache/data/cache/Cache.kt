package com.ikabanov.cache.data.cache

import com.ikabanov.cache.ElementState
import com.ikabanov.cache.Reason
import com.ikabanov.cache.data.db.Element

/**
 * Cache is an object, that contains and operates with temporary nodes.
 */
object Cache : ICacheContract {
    private val listOfNodes = mutableListOf<ElementCache>()

    override fun add(element: Element, level: Int, isFromDB: Boolean): Reason {
        val tempParent = element.parent
        var parentElementName = "null"
        if (tempParent != null) {
            parentElementName = element.parent!!.name
            val realParentIndex = listOfNodes.indexOf(ElementCache(tempParent.name))
            if (realParentIndex != -1) {
                val realParent = listOfNodes[realParentIndex]
                if (realParent.deleted) {
                    return Reason.ABORTED
                }
            }
        }

        val elementCache = ElementCache(element.name, parentElementName, element.deleted, level)
        if (!listOfNodes.contains(elementCache)) {
            if (!isFromDB) {
                elementCache.setState(ElementState.NEW_ELEMENT)
            }
            listOfNodes.add(elementCache)
            return Reason.DONE
        }
        return Reason.ABORTED
    }

    override fun delete(element: ElementCache): Reason {
        if ((element.parent != null) && (element.parent!!.deleted) && (element.deleted)) {
            return Reason.ABORTED
        }
        element.deleted = !element.deleted
        if (element.elementState != ElementState.NEW_ELEMENT) {
            element.setState(ElementState.MODIFIED_ELEMENT)
        }
        if (element.deleted) {
            for (child in element.children) {
                delete(child)
            }
        }
        return if (element.deleted) Reason.DONE else Reason.DONE_NEGATIVE
    }

    override fun alter(element: ElementCache, newName: String): Reason {
        val indexInCache = listOfNodes.indexOf(element)
        if (indexInCache == -1) {
            return Reason.ABORTED
        }

        val modifiedElement = listOfNodes[indexInCache]
        modifiedElement.newName = newName
        modifiedElement.setState(ElementState.MODIFIED_ELEMENT)

        return Reason.DONE
    }

    override fun clearCache() {
        listOfNodes.clear()
    }

    override fun getElements(): List<ElementCache> {
        return listOfNodes.toList()
    }

    override fun getElementsWithTreeRelations(): List<ElementCache> {
        val elements = listOfNodes.sorted()  // Sorted by its level in the tree. We need that for
        // easier finding its relations.
        for (element in elements) {
            for (parentCandidate in elements) { // Go through all the elements to find an element that
                if (parentCandidate.level == (element.level - 1)) { // has level less than the picked element
                    if (element.parentName == parentCandidate.name) { // element knows its parent name, and if it's equals to the candidate
                        if (!parentCandidate.children.contains(element)) { // check whether we added this child to the parent
                            parentCandidate.children.add(element) // add to children list to the parent
                            element.hasLocalParent =
                                true // that's a flag to define roots in future. E.g. two nodes, that isn't connected explicitly.
                            element.parent = parentCandidate
                        }
                        break // Parent has been found. No need to check the other parent candidates. Going to check a new element.
                    }
                } else {
                    continue // If we found a candidate with a level that is not exactly one less than the child,
                    // that's definitely not the parent candidate. Going to the next candidate.
                }
            }
        }
        return elements
    }
}