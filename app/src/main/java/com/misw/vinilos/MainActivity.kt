package com.misw.vinilos
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.misw.vinilos.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate: initializing UI")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.AlbumListFragment, R.id.PerformerListFragment, R.id.CollectorsFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val destId = item.itemId

            val options = NavOptions.Builder()
                .setPopUpTo(destId, inclusive = false)
                .setLaunchSingleTop(true)
                .build()

            navController.navigate(destId, null, options)
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val topLevel = setOf(
                R.id.AlbumListFragment,
                R.id.PerformerListFragment,
                R.id.CollectorsFragment
            )
            if (destination.id in topLevel) {
                binding.bottomNavigation.menu.findItem(destination.id)?.isChecked = true
            }
        }
        Log.d("MainActivity", "onCreate: navigation configured")
    }
    override fun onSupportNavigateUp(): Boolean {
        Log.d("MainActivity", "onSupportNavigateUp")
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
