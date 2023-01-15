package com.dzeio.chartstest.ui

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.dzeio.chartstest.R
import com.dzeio.chartstest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)

        // Comportement chelou API 28-
        // Comportement normal 31+

        // do not do the cool status/navigation bars for API 29 & 30
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.R && Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
            // allow to put the content behind the status bar & Navigation bar (one of them at least lul)
            // ALSO: make the status/navigation bars semi-transparent
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            // Make the color of the navigation bar semi-transparent
//        window.navigationBarColor = Color.TRANSPARENT
            // Make the color of the status bar transparent
//        window.statusBarColor = Color.TRANSPARENT
            // Apply the previous changes
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            // Update toolbar height with the statusbar size included
            // ALSO: make both the status/navigation bars transparent (WHYYYYYYY)
            val toolbarHeight = binding.toolbar.layoutParams.height
            window.decorView.setOnApplyWindowInsetsListener { _, insets ->
                val statusBarSize = insets.systemWindowInsetTop
                // Add padding to the toolbar (YaY I know how something works)
                binding.toolbar.updatePadding(top = statusBarSize)
                binding.toolbar.layoutParams.height = toolbarHeight + statusBarSize
                return@setOnApplyWindowInsetsListener insets
            }

            // normally makes sure icons are at the correct color but idk if it works
            when (this.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        // force to display the bars in light color
                        isAppearanceLightNavigationBars = true
                        isAppearanceLightStatusBars = false // WHY
                    }
                }
                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        // force to display the bars in dark color
                        isAppearanceLightNavigationBars = false
                        isAppearanceLightStatusBars = true // WHY
                    }
                }
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.MainFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

}
