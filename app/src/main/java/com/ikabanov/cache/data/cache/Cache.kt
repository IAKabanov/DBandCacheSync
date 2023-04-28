package com.ikabanov.cache.data.cache

import com.ikabanov.cache.ElementState
import com.ikabanov.cache.Reason
import com.ikabanov.cache.data.db.Element

/**
 * Cache is an object, that contains and operates with temporary nodes.
 */
object Cache : ICacheContract {
    private val listOfNodes = mutableListOf<ElementCache>()
    private var newIDsCount: Int = 0

    override fun add(element: ElementCache, level: Int, lastIndexDB: Int): Reason {
        val parentID = element.parentID
        var shallBeDeleted = element.deleted
        val realParentIndex = listOfNodes.indexOf(ElementCache(parentID))
        val id = if (element.id == Element.NO_ID) lastIndexDB + 1 + newIDsCount else element.id
        val isFromDB = lastIndexDB > id
        if (realParentIndex != Element.NO_ID) {
            val realParent = listOfNodes[realParentIndex]
            if (realParent.deleted) {
                if (!isFromDB) {
                    return Reason.ABORTED
                }
                shallBeDeleted = true
            }
        }

        val elementCache =
            ElementCache(id, element.name, parentID, shallBeDeleted, level)
        if (!listOfNodes.contains(elementCache)) {
            if (!isFromDB) {
                elementCache.setState(ElementState.NEW_ELEMENT)
                newIDsCount++
            }
            listOfNodes.add(elementCache)
            return Reason.DONE
        }
        return Reason.ABORTED
    }

    override fun delete(element: ElementCache): Reason {
        if (element.deleted) {
            return Reason.ABORTED // All the next logic is prepared to unmark as deleted in the cache.
        }
        element.deleted = !element.deleted
        element.setState(ElementState.MODIFIED_ELEMENT)
        if (element.deleted) {
            for (child in element.children) {
                delete(child)
            }
        }
        return if (element.deleted) Reason.DONE else Reason.DONE_NEGATIVE
    }

    override fun alter(element: ElementCache, newName: String): Reason {
        if (element.deleted) {
            return Reason.ABORTED // All the next logic is prepared to rename deleted element in the cache.
        }

        val indexInCache = listOfNodes.indexOf(element)
        if (indexInCache == -1) {
            return Reason.ABORTED
        }

        val modifiedElement = listOfNodes[indexInCache]
        modifiedElement.name = newName
        modifiedElement.setState(ElementState.MODIFIED_ELEMENT)

        return Reason.DONE
    }

    override fun clearCache() {
        listOfNodes.clear()
    }

    override fun unmodifyCache() {
        for (element in listOfNodes) {
            element.setState(ElementState.NONE)
        }
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
                    if (element.parentID == parentCandidate.id) { // element knows its parent name, and if it's equals to the candidate
                        if (!parentCandidate.children.contains(element)) { // check whether we added this child to the parent
                            parentCandidate.children.add(element) // add to children list to the parent
                            element.hasLocalParent =
                                true // that's a flag to define roots in future. E.g. two nodes, that isn't connected explicitly.
                            element.parent = parentCandidate
                        }
                        if (parentCandidate.deleted) {
                            element.deleted = true
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

    override fun getNewIDCount(): Int {
        return newIDsCount
    }
}