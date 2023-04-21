package com.ikabanov.cache.data.db

data class Element(var name: String) : Comparable<Element> {
    var parent: Element? = null
        private set
    var level = 0
    var deleted: Boolean = false
    val children: MutableList<Element> = mutableListOf()

    constructor(name: String, parent: Element?) : this(name) {
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