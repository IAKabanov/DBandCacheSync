package com.ikabanov.cache.view

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ikabanov.cache.R

class MainActivity : AppCompatActivity() {
    private lateinit var btnResetDB: Button
    private lateinit var btnApplyCache: Button
    private lateinit var btnResetCache: Button
    private lateinit var btnAddElement: Button
    private lateinit var btnDeleteElement: Button
    private lateinit var btnAlterElement: Button
    private val presenter = MainActivityPresenter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cacheView = supportFragmentManager.findFragmentById(R.id.database_cache_wrapper)
        val dbView = supportFragmentManager.findFragmentById(R.id.database_wrapper)

        presenter.addMainViews(this, dbView as DBFragment, cacheView as CacheFragment)

        btnResetDB = findViewById(R.id.reset_db)
        btnResetDB.setOnClickListener {
            presenter.resetDB()
            presenter.resetCache()
        }

        btnResetCache = findViewById(R.id.reset_cache)
        btnResetCache.setOnClickListener { presenter.resetCache() }

        btnAddElement = findViewById(R.id.create_in_cache)
        btnAddElement.setOnClickListener { presenter.addElement() }

        btnDeleteElement = findViewById(R.id.mark_delete_in_cache)
        btnDeleteElement.setOnClickListener { presenter.deleteElement() }

        btnAlterElement = findViewById(R.id.alter_cache)
        btnAlterElement.setOnClickListener { presenter.alterElement() }

        btnApplyCache = findViewById(R.id.apply_cache)
        btnApplyCache.setOnClickListener { presenter.applyCache() }
    }

    fun showToast(text: String) {
        if (text.isEmpty()) {
            return
        }
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun notifyAddModeEnabled(enabled: Boolean) {
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

    fun notifyDeleteModeEnabled(enabled: Boolean) {
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

    fun notifyAlterModeEnabled(enabled: Boolean) {
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