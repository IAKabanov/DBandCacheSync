package com.ikabanov.cache.domain

import com.ikabanov.cache.data.cache.ElementCache
import com.ikabanov.cache.data.db.Element

interface IDBInteractor {
    fun addManyElements(elements: List<ElementCache>)
    fun getElementsWithTreeRelations(): List<Element>
    fun resetDB()
}