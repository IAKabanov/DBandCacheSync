package com.ikabanov.cache.data.db

/**
 * IdbContract is an interface for interaction with database.
 */
interface IdbContract {
    fun addElement(element: Element, parent: Element)
    fun updateElement(oldElement: Element, newElement: Element)
    fun resetDB()
    fun getElements(): List<Element>
}