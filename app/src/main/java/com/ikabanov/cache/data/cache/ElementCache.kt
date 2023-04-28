package com.ikabanov.cache.data.cache

import com.ikabanov.cache.ElementState
import com.ikabanov.cache.data.db.Element

/**
 * ElementCache is a model of cache element. Will be united to Element soon.
 */
data class ElementCache(val id: Int) : Comparable<ElementCache> {
    companion object {
        fun createFromElement(element: Element): ElementCache {
            val elementParent = element.parent
            var elementParentID = -1
            if (elementParent != null) {
                elementParentID = elementParent.id
            }
            return ElementCache(
                element.id,
                element.name,
                elementParentID,
                element.deleted,
                element.level
            )
        }
    }

    var name: String = ""
    var parent: ElementCache? = null
    var level: Int = 0
    var deleted: Boolean = false
    var children: MutableList<ElementCache> = mutableListOf()

    constructor(id: Int, name: String) : this(id) {
        this.name = name
    }

    constructor(id: Int, name: String, parentID: Int) : this(id, name) {
        this.parentID = parentID
    }

    constructor(id: Int, name: String, parentID: Int, deleted: Boolean, level: Int) : this(
        id,
        name,
        parentID
    ) {
        this.deleted = deleted
        this.level = level
    }

    fun setState(state: ElementState) {
        // We want to change element state.
        // By vertical it's old state to be changed.
        // By horizontal it's new state that we want to set instead of the old state.
        // In the middle, it's the state that would be set.
        // For example, if we create a new node, it's set as new. If we modify it, it should
        // stay new, and not modified.
        //
        //         | want new  |  want mod  |  want non |
        // was new |    new    |    new     |    non    |
        // was mod |    mod    |    mod     |    non    |
        // was non |    new    |    mod     |    non    |

        this.elementState = when (state) {
            ElementState.NEW_ELEMENT -> when (elementState) {
                ElementState.NEW_ELEMENT -> ElementState.NEW_ELEMENT
                ElementState.MODIFIED_ELEMENT -> ElementState.MODIFIED_ELEMENT
                ElementState.NONE -> ElementState.NEW_ELEMENT
            }
            ElementState.MODIFIED_ELEMENT -> when (elementState) {
                ElementState.NEW_ELEMENT -> ElementState.NEW_ELEMENT
                ElementState.MODIFIED_ELEMENT -> ElementState.MODIFIED_ELEMENT
                ElementState.NONE -> ElementState.MODIFIED_ELEMENT
            }
            ElementState.NONE -> when (elementState) {
                ElementState.NEW_ELEMENT -> ElementState.NONE
                ElementState.MODIFIED_ELEMENT -> ElementState.NONE
                ElementState.NONE -> ElementState.NONE
            }
        }

        if (this.elementState == ElementState.NEW_ELEMENT) {
            return
        }
        this.elementState = state
    }

    var elementState = ElementState.NONE
        private set
    var parentID: Int = -1
        private set
    var hasLocalParent = false

    override fun compareTo(other: ElementCache): Int {
        return level.compareTo(other.level)
    }
}