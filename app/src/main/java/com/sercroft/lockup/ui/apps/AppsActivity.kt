package com.sercroft.lockup.ui.apps

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.sercroft.lockup.R
import com.sercroft.lockup.data.repository.BlockedAppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.textfield.TextInputEditText

class AppsActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var loadingContainer: View
    private lateinit var etSearch: TextInputEditText
    private lateinit var adapter: AppsAdapter

    private var allApps: List<AppItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps)

        findViewById<View>(R.id.backButton).setOnClickListener {
            finish()
        }

        val root = findViewById<View>(R.id.rootLayout)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )

            insets
        }

        recycler = findViewById(R.id.recyclerApps)
        loadingContainer = findViewById(R.id.loadingContainer)
        etSearch = findViewById(R.id.etSearch)

        adapter = AppsAdapter()

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        lifecycleScope.launch {

            allApps = withContext(Dispatchers.IO) {
                loadApps()
            }

            adapter.submitList(allApps)

            loadingContainer.visibility = View.GONE
        }

        setupSearch()
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                filterApps(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun filterApps(query: String) {
        val searchText = query.trim()

        if (searchText.isEmpty()) {
            adapter.submitList(allApps)
            return
        }

        val filteredApps = allApps.filter {
            it.name.contains(searchText, ignoreCase = true)
        }

        adapter.submitList(filteredApps)
    }

    private fun loadApps(): List<AppItem> {
        val pm = packageManager

        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter {
                pm.getLaunchIntentForPackage(it.packageName) != null
            }
            .map {
                AppItem(
                    name = pm.getApplicationLabel(it).toString(),
                    pkgName = it.packageName,
                    icon = pm.getApplicationIcon(it),
                    isBlocked = BlockedAppsRepository.isBlocked(it.packageName)
                )
            }
            .sortedBy {
                it.name.lowercase()
            }
    }
}