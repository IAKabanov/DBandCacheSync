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
    override fun addElement(element: Element, level: Int, isFromDB: Boolean): Reason {
        return cache.add(element, level, isFromDB)
    }

    override fun getElementsWithTreeRelations(): List<ElementCache> {
        return cache.getElementsWithTreeRelations()// Sorted by its level in the tree. We need that for
        // easier finding its relations.
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
}