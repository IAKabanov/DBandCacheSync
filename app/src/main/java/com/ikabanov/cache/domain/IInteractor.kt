package com.ikabanov.cache.domain

import com.ikabanov.cache.Reason
import com.ikabanov.cache.data.cache.ElementCache
import com.ikabanov.cache.data.db.Element

interface IInteractor {
    fun resetDB()
    fun getCacheElementsWithTreeRelations(): List<ElementCache>
    fun getDBElementsWithTreeRelations(): List<Element>
    fun sendElementFromDBToCache(element: Element, level: Int)
    fun mergeCacheToDB()
    fun clearCache()
    fun addElementToCache(elementName: String, parent: ElementCache): Reason
    fun editElementInCache(elementName: String, element: ElementCache): Reason
    fun deleteElementFromCache(element: ElementCache): Reason
}