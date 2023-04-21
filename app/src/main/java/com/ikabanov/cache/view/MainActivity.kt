package com.ikabanov.cache.view

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ikabanov.cache.R
import com.ikabanov.cache.data.cache.ElementCache
import com.ikabanov.cache.data.db.Element
import com.ikabanov.cache.domain.*


class MainActivity : AppCompatActivity(), IdbFragmentContract, ICacheFragmentContract, ICacheButtonsNotifier {
    private lateinit var cacheFragment: ICacheFragmentReceiver
    private lateinit var dbFragment: IdbFragmentReceiver
    private lateinit var dbFragmentReset: IResetView
    private lateinit var cacheFragmentViewInteraction: ICacheFragmentInteraction
    private lateinit var btnResetDB: Button
    private lateinit var btnApplyCache: Button
    private lateinit var btnResetCache: Button
    private lateinit var btnAddElement: Button
    private lateinit var btnDeleteElement: Button
    private lateinit var btnAlterElement: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cacheView = supportFragmentManager.findFragmentById(R.id.database_cache_wrapper)
        val dbView = supportFragmentManager.findFragmentById(R.id.database_wrapper)

        cacheFragment = cacheView as ICacheFragmentReceiver
        cacheFragmentViewInteraction = cacheView as ICacheFragmentInteraction

        dbFragment = dbView as IdbFragmentReceiver
        dbFragmentReset = dbView as IResetView

        btnResetDB = findViewById(R.id.reset_db)
        btnResetDB.setOnClickListener {
            dbFragmentReset.reset()
            cacheFragmentViewInteraction.reset()
        }

        btnResetCache = findViewById(R.id.reset_cache)
        btnResetCache.setOnClickListener { cacheFragmentViewInteraction.reset() }

        btnAddElement = findViewById(R.id.create_in_cache)
        btnAddElement.setOnClickListener { cacheFragmentViewInteraction.addElement() }

        btnDeleteElement = findViewById(R.id.mark_delete_in_cache)
        btnDeleteElement.setOnClickListener { cacheFragmentViewInteraction.deleteElement() }

        btnAlterElement = findViewById(R.id.alter_cache)
        btnAlterElement.setOnClickListener { cacheFragmentViewInteraction.alterElement() }

        btnApplyCache = findViewById(R.id.apply_cache)
        btnApplyCache.setOnClickListener { cacheFragmentViewInteraction.applyCache() }
    }

    override fun onSendToCacheTriggered(node: TreeNodeWrapper) {
        cacheFragment.onTreeViewReceived(node.value as Element, node.level)
    }

    override fun onCacheApplied(elements: List<ElementCache>) {
        dbFragment.onElementsReceived(elements)
    }

    override fun notifyAddModeEnabled(enabled: Boolean) {
        if (enabled) {
            btnAddElement.setBackgroundColor(resources.getColor(R.color.purple_200))
            btnDeleteElement.isEnabled = false
            btnAlterElement.isEnabled = false
            btnApplyCache.isEnabled = false
        } else {
            btnAddElement.setBackgroundColor(resources.getColor(R.color.purple_500))
            btnDeleteElement.isEnabled = true
            btnAlterElement.isEnabled = true
            btnApplyCache.isEnabled = true
        }
    }

    override fun notifyDeleteModeEnabled(enabled: Boolean) {
        if (enabled) {
            btnDeleteElement.setBackgroundColor(resources.getColor(R.color.purple_200))
            btnAddElement.isEnabled = false
            btnAlterElement.isEnabled = false
            btnApplyCache.isEnabled = false
        } else {
            btnDeleteElement.setBackgroundColor(resources.getColor(R.color.purple_500))
            btnAddElement.isEnabled = true
            btnAlterElement.isEnabled = true
            btnApplyCache.isEnabled = true
        }
    }

    override fun notifyAlterModeEnabled(enabled: Boolean) {
        if (enabled) {
            btnAlterElement.setBackgroundColor(resources.getColor(R.color.purple_200))
            btnDeleteElement.isEnabled = false
            btnAddElement.isEnabled = false
            btnApplyCache.isEnabled = false
        } else {
            btnAlterElement.setBackgroundColor(resources.getColor(R.color.purple_500))
            btnDeleteElement.isEnabled = true
            btnAddElement.isEnabled = true
            btnApplyCache.isEnabled = true
        }
    }
}