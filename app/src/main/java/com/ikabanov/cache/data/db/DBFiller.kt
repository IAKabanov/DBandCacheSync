package com.ikabanov.cache.data.db

class DBFiller {
    companion object {
        fun createRoot(): MutableMap<Element, Element> {
            val element1 = Element("Node1", null)
            val element2 = Element("Node2", element1)
            val element3 = Element("Node3", element1)
            val element4 = Element("Node4", element3)
            val element5 = Element("Node5", element1)
            val element6 = Element("Node6", element5)

            element1.addChild(element2)
            element1.addChild(element3)
            element3.addChild(element4)
            element1.addChild(element5)
            element5.addChild(element6)

            val result = mutableMapOf<Element, Element>()
            result[element1] = element1
            result[element2] = element2
            result[element3] = element3
            result[element4] = element4
            result[element5] = element5
            result[element6] = element6

            return result
        }
    }
}