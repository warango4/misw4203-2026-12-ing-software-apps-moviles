package com.misw.vinilos

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.misw.vinilos.data.session.UserSession
import com.misw.vinilos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var roleChipView: View? = null
    private var activePopup: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
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
            updateFab(destination.id)
        }

        binding.fabCreateAlbum.setOnClickListener {
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.action_AlbumListFragment_to_CreateAlbumFragment)
        }

        setupRoleMenu()
        Log.d("MainActivity", "onCreate: navigation configured")
    }

    private fun setupRoleMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_album_list, menu)
                val actionView = menu.findItem(R.id.action_role).actionView ?: return
                roleChipView = actionView
                updateRoleChip(actionView)
                actionView.setOnClickListener { showRolePopup(it) }
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false
        })
    }

    private fun updateRoleChip(view: View? = roleChipView) {
        val v = view ?: return
        val isCollector = UserSession.isCollector(this)
        v.findViewById<TextView>(R.id.tvRoleChipLabel).text =
            if (isCollector) getString(R.string.role_collector) else getString(R.string.role_guest)
        v.findViewById<ImageView>(R.id.ivRoleChipIcon).setImageResource(
            if (isCollector) R.drawable.ic_shield else R.drawable.ic_person_role
        )
    }

    private fun showRolePopup(anchor: View) {
        activePopup?.dismiss()
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_role_switch, null)

        val isCollector = UserSession.isCollector(this)
        popupView.findViewById<View>(R.id.dotGuest).visibility =
            if (!isCollector) View.VISIBLE else View.INVISIBLE
        popupView.findViewById<View>(R.id.dotCollector).visibility =
            if (isCollector) View.VISIBLE else View.INVISIBLE

        val popupWidthPx = (260 * resources.displayMetrics.density).toInt()
        val popup = PopupWindow(popupView, popupWidthPx, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popup.elevation = 12f * resources.displayMetrics.density
        popup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popup.isOutsideTouchable = true
        activePopup = popup

        popupView.findViewById<View>(R.id.optionGuest).setOnClickListener {
            UserSession.setRole(this, UserSession.ROLE_GUEST)
            popup.dismiss()
            updateRoleChip()
            updateFab()
        }
        popupView.findViewById<View>(R.id.optionCollector).setOnClickListener {
            UserSession.setRole(this, UserSession.ROLE_COLLECTOR)
            popup.dismiss()
            updateRoleChip()
            updateFab()
        }

        popup.showAsDropDown(anchor, anchor.width - popupWidthPx, 8)
    }

    private fun updateFab(currentDestId: Int? = null) {
        val destId = currentDestId
            ?: findNavController(R.id.nav_host_fragment_content_main).currentDestination?.id
        val show = destId == R.id.AlbumListFragment && UserSession.isCollector(this)
        binding.fabCreateAlbum.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d("MainActivity", "onSupportNavigateUp")
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        activePopup?.dismiss()
        activePopup = null
        roleChipView = null
    }
}
