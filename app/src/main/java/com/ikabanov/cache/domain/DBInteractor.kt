package com.ikabanov.cache.domain

import com.ikabanov.cache.ElementState
import com.ikabanov.cache.data.cache.ElementCache
import com.ikabanov.cache.data.db.DB
import com.ikabanov.cache.data.db.Element
import com.ikabanov.cache.data.db.IdbContract

/**
 * DBInteractor is a class that should be used to connect MainActivityPresenter and DB.
 */
class DBInteractor : IDBInteractor {
    private val db: IdbContract = DB
    override fun addManyElements(elements: List<ElementCache>) {
        for (element in elements) {
            if (element.elementState == ElementState.MODIFIED_ELEMENT) {
                val oldElement = Element(element.name)
                val newElement = Element(element.newName ?: element.name)
                newElement.deleted = element.deleted
                db.updateElement(oldElement, newElement)
            }
            if (element.elementState == ElementState.NEW_ELEMENT) {
                if (!element.deleted) {
                    val newElement = Element(element.newName ?: element.name)
                    val potentialParent = element.parent
                    val parentName = if (potentialParent != null) {
                        if (potentialParent.newName != null) potentialParent.newName else potentialParent.name
                    } else element.parentName
                    val parent = Element(parentName!!) // not null because of "if" above.
                    db.addElement(newElement, parent)
                }
            }
        }
    }

    override fun getElementsWithTreeRelations(): List<Element> {
        return db.getElements().toMutableList()
            .sorted() // Sorted by its level in the tree. We need that for
        // easier finding its relations.
    }

    override fun resetDB() {
        db.resetDB()
    }
}