package com.ikabanov.cache.domain

import com.ikabanov.cache.Reason
import com.ikabanov.cache.data.cache.ElementCache
import com.ikabanov.cache.data.db.Element

interface ICacheInteractor {
    fun addElement(element: Element, level: Int, isFromDB: Boolean = false): Reason
    fun getElementsWithTreeRelations(): List<ElementCache>
    fun clearCache()
    fun deleteElement(element: ElementCache): Reason
    fun editElement(oldElement: ElementCache, newName: String): Reason
}