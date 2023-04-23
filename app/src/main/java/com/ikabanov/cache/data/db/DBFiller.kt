package com.ikabanov.cache.data.db

/**
 * DBFiller just fills the DB with default values.
 */
class DBFiller {
    companion object {
        fun createRoot(): List<Element> {
            val element1 = Element("Node1", null)
            val element2 = Element("Node2", element1)
            val element3 = Element("Node3", element1)
            val element4 = Element("Node4", element3)
            val element5 = Element("Node5", element1)
            val element6 = Element("Node6", element5)
            val element7 = Element("Node7", element6)

            element1.addChild(element2)
            element1.addChild(element3)
            element3.addChild(element4)
            element1.addChild(element5)
            element5.addChild(element6)
            element6.addChild(element7)

            val result = mutableListOf<Element>()
            result.add(element1)
            result.add(element2)
            result.add(element3)
            result.add(element4)
            result.add(element5)
            result.add(element6)
            result.add(element7)

            return result
        }
    }
}