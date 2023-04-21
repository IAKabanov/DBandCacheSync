package com.ikabanov.cache.data.cache

data class ElementCache(
    var name: String,
) : Comparable<ElementCache> {

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
    var deleted: Boolean = false
    var newElement: Boolean = false
    var parent: ElementCache? = null
    var newName: String? = null
    var modified: Boolean = false
    var level: Int = 0 // It's not in the primary constructor because we don't need it to be
        private set // considered when hashCode(). It shouldn't be modified.
    var children: MutableList<ElementCache> = mutableListOf()
    var hasLocalParent = false


    override fun compareTo(other: ElementCache): Int {
        return level.compareTo(other.level)
    }
}