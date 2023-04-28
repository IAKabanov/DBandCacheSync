package com.ikabanov.cache.data.cache

import com.ikabanov.cache.Reason
import com.ikabanov.cache.data.db.Element

/**
 * ICacheContract is an interface for interaction with cache.
 */
interface ICacheContract {
    fun add(element: ElementCache, level: Int, lastIndexDB: Int = Element.NO_ID): Reason
    fun delete(element: ElementCache): Reason
    fun alter(element: ElementCache, newName: String): Reason
    fun clearCache()
    fun getElements(): List<ElementCache>
    fun getElementsWithTreeRelations(): List<ElementCache>
    fun getNewIDCount(): Int
    fun unmodifyCache()
}