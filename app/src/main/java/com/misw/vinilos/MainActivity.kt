package com.misw.vinilos
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.misw.vinilos.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Top-level destinations para BottomNavigation (sin botón Up)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.AlbumListFragment, R.id.PerformerListFragment, R.id.CollectorsFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Conectar bottom nav con el NavController
        binding.bottomNavigation.setupWithNavController(navController)
        Log.d("MainActivity", "onCreate finished setting up Navigation")
    }
    override fun onSupportNavigateUp(): Boolean {
        Log.d("MainActivity", "onSupportNavigateUp called")
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
