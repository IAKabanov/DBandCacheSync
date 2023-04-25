package com.ikabanov.cache.data.cache

import com.ikabanov.cache.ElementState

/**
 * ElementCache is a model of cache element. Will be united to Element soon.
 */
data class ElementCache(var name: String) : Comparable<ElementCache> {
    var parent: ElementCache? = null
    var level: Int = 0
    var deleted: Boolean = false
    var children: MutableList<ElementCache> = mutableListOf()

    constructor(name: String, parentName: String) : this(name) {
        this.parentName = parentName
    }

    constructor(name: String, parentName: String, deleted: Boolean, level: Int) : this(name) {
        this.parentName = parentName
        this.deleted = deleted
        this.level = level
    }

    fun setState(state: ElementState) {
        if (this.elementState != ElementState.NEW_ELEMENT) {
            this.elementState = state
        }
        if (this.elementState != ElementState.MODIFIED_ELEMENT) {
            this.elementState = state
        }
    }

    var elementState = ElementState.NONE
        private set
    var parentName: String = ""
        private set
    var newName: String? = null
    var hasLocalParent = false

    override fun compareTo(other: ElementCache): Int {
        return level.compareTo(other.level)
    }
}