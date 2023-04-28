package com.ikabanov.cache.data.db

/**
 * Element is a model of db element. Will be united to ElementCache soon.
 */
data class Element(val id: Int) : Comparable<Element> {
    companion object {
        const val NO_ID = -1
    }
    var name: String = ""
    var parent: Element? = null
        private set
    var level = 0
    var deleted: Boolean = false
    val children: MutableList<Element> = mutableListOf()

    constructor(id: Int, name: String) : this(id) {
        this.name = name
    }

    constructor(id: Int, name: String, parent: Element?) : this(id, name) {
        this.parent = parent
    }

    fun addChild(element: Element) {
        element.level = this.level + 1
        element.parent = this
        children.add(element)
    }

    override fun compareTo(other: Element): Int {
        return level.compareTo(other.level)
    }
}