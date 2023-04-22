package com.ikabanov.cache.data.cache

import com.ikabanov.cache.Reason
import com.ikabanov.cache.data.db.Element

interface ICacheContract {
    fun add(element: Element, level: Int, isFromDB: Boolean = true): Reason
    fun delete(element: ElementCache): Reason
    fun alter(element: ElementCache, newName: String): Reason
    fun unmodifyElements()
    fun clearCache()
    fun getElements(): List<ElementCache>
    fun getElementsWithTreeRelations(): List<ElementCache>
}