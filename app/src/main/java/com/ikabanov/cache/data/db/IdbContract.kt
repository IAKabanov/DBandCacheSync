package com.ikabanov.cache.data.db

interface IdbContract {
    fun addElement(element: Element, parent: Element)
    fun updateElement(oldElement: Element, newElement: Element)

    fun resetDB()
    fun getElements(): List<Element>
}