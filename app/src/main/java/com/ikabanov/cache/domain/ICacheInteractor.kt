package com.ikabanov.cache.domain

import com.ikabanov.cache.Reason
import com.ikabanov.cache.data.cache.ElementCache
import com.ikabanov.cache.data.db.Element

/**
 * ICacheInteractor is an interface that should be used to connect a presenter and a cache.
 */
interface ICacheInteractor {
    fun addElement(element: Element, level: Int, lastID: Int): Reason
    fun getElementsWithTreeRelations(): List<ElementCache>
    fun clearCache()
    fun deleteElement(element: ElementCache): Reason
    fun editElement(oldElement: ElementCache, newName: String): Reason
    fun getNewElementsCount(): Int
    fun unmodifyCache()
}