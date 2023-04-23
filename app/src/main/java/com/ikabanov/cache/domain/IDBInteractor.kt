package com.ikabanov.cache.domain

import com.ikabanov.cache.data.cache.ElementCache
import com.ikabanov.cache.data.db.Element

/**
 * ICacheInteractor is an interface that should be used to connect a presenter and a database.
 */
interface IDBInteractor {
    fun addManyElements(elements: List<ElementCache>)
    fun getElementsWithTreeRelations(): List<Element>
    fun resetDB()
}