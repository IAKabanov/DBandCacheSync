package com.ikabanov.cache.domain

import com.ikabanov.cache.Reason
import com.ikabanov.cache.data.cache.ElementCache
import com.ikabanov.cache.data.db.Element

class Interactor : IInteractor {
    private var dbInteractor: IDBInteractor = DBInteractor()
    private var cacheInteractor: ICacheInteractor = CacheInteractor()

    override fun resetDB() {
        dbInteractor.resetDB()
        cacheInteractor.clearCache()
    }

    override fun getCacheElementsWithTreeRelations(): List<ElementCache> {
        return cacheInteractor.getElementsWithTreeRelations()
    }

    override fun getDBElementsWithTreeRelations(): List<Element> {
        return dbInteractor.getElementsWithTreeRelations()
    }

    override fun sendElementFromDBToCache(element: Element, level: Int) {
        if (!element.deleted) {
            cacheInteractor.addElement(element, level, dbInteractor.getLastID())
        }
    }

    override fun mergeCacheToDB() {
        val elementsSorted = cacheInteractor.getElementsWithTreeRelations()
        dbInteractor.addManyElements(elementsSorted)
        cacheInteractor.unmodifyCache()
    }

    override fun clearCache() {
        cacheInteractor.clearCache()
    }

    override fun addElementToCache(elementName: String, parent: ElementCache): Reason {
        return cacheInteractor.addElement(
            Element(
                Element.NO_ID,
                elementName,
                Element(parent.id, parent.name, null)
            ),
            parent.level + 1,
            dbInteractor.getLastID()
        )
    }

    override fun editElementInCache(elementName: String, element: ElementCache): Reason {
        return cacheInteractor.editElement(
            ElementCache(element.id, element.name, element.parentID),
            elementName
        )
    }

    override fun deleteElementFromCache(element: ElementCache): Reason {
        return cacheInteractor.deleteElement(element)
    }
}