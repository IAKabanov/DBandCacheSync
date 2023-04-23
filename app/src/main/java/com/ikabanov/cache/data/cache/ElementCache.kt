package com.ikabanov.cache.data.cache

/**
 * ElementCache is a model of cache element. Will be united to Element soon.
 */
data class ElementCache(var name: String) : Comparable<ElementCache> {
    var deleted: Boolean = false
    var parent: ElementCache? = null
    var level: Int = 0
        private set
    var children: MutableList<ElementCache> = mutableListOf()

    constructor(name: String, parentName: String) : this(name) {
        this.parentName = parentName
    }

    constructor(name: String, parentName: String, deleted: Boolean, level: Int) : this(name) {
        this.parentName = parentName
        this.deleted = deleted
        this.level = level
    }

    var parentName: String = ""
        private set
    var newElement: Boolean = false
    var newName: String? = null
    var modified: Boolean = false
    var hasLocalParent = false

    override fun compareTo(other: ElementCache): Int {
        return level.compareTo(other.level)
    }
}