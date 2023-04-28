package com.ikabanov.cache.domain

import com.ikabanov.cache.Reason
import com.ikabanov.cache.data.cache.Cache
import com.ikabanov.cache.data.cache.ElementCache
import com.ikabanov.cache.data.cache.ICacheContract
import com.ikabanov.cache.data.db.Element

/**
 * CacheInteractor is a class that should be used to connect MainActivityPresenter and Cache.
 */
class CacheInteractor : ICacheInteractor {
    val cache: ICacheContract = Cache

    override fun addElement(element: Element, level: Int, lastID: Int): Reason {
        val localElement = ElementCache.createFromElement(element)
        return cache.add(localElement, level, lastID)
    }

    override fun getElementsWithTreeRelations(): List<ElementCache> {
        return cache.getElementsWithTreeRelations()
    }

    override fun clearCache() {
        cache.clearCache()
    }

    override fun deleteElement(element: ElementCache): Reason {
        return cache.delete(element)
    }

    override fun editElement(oldElement: ElementCache, newName: String): Reason {
        return cache.alter(oldElement, newName)
    }

    override fun getNewElementsCount(): Int {
        return cache.getNewIDCount()
    }

    override fun unmodifyCache() {
        cache.unmodifyCache()
    }
}