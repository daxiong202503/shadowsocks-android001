/*******************************************************************************
 *                                                                             *
 *  Copyright (C) 2017 by Max Lv <max.c.lv@gmail.com>                          *
 *  Copyright (C) 2017 by Mygod Studio <contact-shadowsocks-android@mygod.be>  *
 *                                                                             *
 *  This program is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by       *
 *  the Free Software Foundation, either version 3 of the License, or          *
 *  (at your option) any later version.                                        *
 *                                                                             *
 *  This program is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 *  GNU General Public License for more details.                               *
 *                                                                             *
 *  You should have received a copy of the GNU General Public License          *
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.     *
 *                                                                             *
 *******************************************************************************/

package com.github.shadowsocks

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.github.shadowsocks.acl.CustomRulesFragment
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.bg.VpnService
import com.github.shadowsocks.ProfilesFragment
import com.github.shadowsocks.GlobalSettingsFragment
import com.github.shadowsocks.AboutFragment
import com.github.shadowsocks.ToolbarFragment
import com.github.shadowsocks.core.R
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.preference.DataStore
import com.github.shadowsocks.preference.OnPreferenceDataStoreChangeListener
import com.github.shadowsocks.subscription.SubscriptionFragment
import com.github.shadowsocks.aidl.TrafficStats
import com.github.shadowsocks.utils.Key
import com.github.shadowsocks.utils.StartService
import com.github.shadowsocks.widget.ListHolderListener
import com.github.shadowsocks.widget.ServiceButton
import com.github.shadowsocks.widget.StatsBar
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnPreferenceDataStoreChangeListener, ListHolderListener {
    
    private lateinit var service: VpnService
    private lateinit var fab: ServiceButton
    private lateinit var stats: StatsBar
    private lateinit var drawer: DrawerLayout
    private lateinit var navigation: NavigationView
    
    private val serviceConnection = object : BaseService.Interface {
        override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
            runOnUiThread {
                fab.updateState(state, profileName, msg)
                stats.updateState(state, profileName, msg)
            }
        }
        
        override fun trafficUpdated(profile: Profile, stats: TrafficStats) {
            runOnUiThread { this@MainActivity.stats.updateTraffic(profile, stats) }
        }
    }
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        drawer = findViewById(R.id.drawer)
        navigation = findViewById(R.id.navigation)
        navigation.setNavigationItemSelectedListener(this)
        
        // 设置返回按钮处理
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawer.isDrawerOpen(navigation)) {
                    drawer.closeDrawer(navigation)
                } else {
                    finish()
                }
            }
        })
        
        // 初始化Fragment显示
        if (savedInstanceState == null) {
            navigation.menu.findItem(R.id.profiles).isChecked = true
            displayFragment(ProfilesFragment())
        }

        fab = findViewById(R.id.fab)
        fab.initProgress(findViewById(R.id.fabProgress))
        fab.setOnClickListener { toggle() }
        
        stats = findViewById(R.id.stats)
        stats.setOnClickListener { showProfiles() }
        
        DataStore.publicStore.registerChangeListener(this)
    }

    override fun onStart() {
        super.onStart()
        if (VpnService.prepare(this) == null) {
            startService()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::service.isInitialized) {
            service.bandwidthTimeout = 500
        }
    }

    override fun onPause() {
        if (::service.isInitialized) {
            service.bandwidthTimeout = 0
        }
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::service.isInitialized) {
            service.unregisterCallback(serviceConnection)
            unbindService(serviceConnection)
        }
        DataStore.publicStore.unregisterChangeListener(this)
        scope.cancel()
    }

    private fun startService() {
        if (::service.isInitialized) {
            service.registerCallback(serviceConnection)
        } else {
            StartService.register(this) { service = it as VpnService }
            service.registerCallback(serviceConnection)
        }
    }

    private fun displayFragment(fragment: ToolbarFragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_holder, fragment).commitAllowingStateLoss()
        drawer.closeDrawers()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.isChecked) drawer.closeDrawers() else {
            when (item.itemId) {
                R.id.profiles -> {
                    displayFragment(ProfilesFragment())
                }
                R.id.globalSettings -> {
                    displayFragment(GlobalSettingsFragment())
                }
                R.id.about -> {
                    displayFragment(AboutFragment())
                }
                R.id.faq -> {
                    try {
                        CustomTabsIntent.Builder()
                            .setDefaultColorSchemeParams(
                                CustomTabColorSchemeParams.Builder()
                                    .setToolbarColor(ContextCompat.getColor(this, R.color.material_blue_500))
                                    .build()
                            )
                            .build()
                            .launchUrl(this, getString(R.string.faq_url).toUri())
                    } catch (e: ActivityNotFoundException) {
                        // 如果CustomTabs不可用，使用Intent打开URL
                        val intent = Intent(Intent.ACTION_VIEW, getString(R.string.faq_url).toUri())
                        startActivity(intent)
                    }
                    return true
                }
                R.id.customRules -> displayFragment(CustomRulesFragment())
                R.id.subscriptions -> displayFragment(SubscriptionFragment())
                else -> return false
            }
            item.isChecked = true
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            drawer.openDrawer(navigation)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            drawer.openDrawer(navigation)
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    private fun toggle() {
        if (::service.isInitialized) {
            if (service.data.established) {
                service.stop()
            } else {
                service.start()
            }
        }
    }

    private fun showProfiles() {
        navigation.menu.findItem(R.id.profiles).isChecked = true
        displayFragment(ProfilesFragment())
        drawer.closeDrawers()
    }

    override fun onPreferenceDataStoreChanged(store: DataStore, key: String) {
        if (key == Key.serviceMode) {
            if (::service.isInitialized) {
                service.reload()
            }
        }
    }

    override fun onListHolderCreated(holder: ListHolderListener.Holder) {
        if (::service.isInitialized) {
            service.bandwidthTimeout = 500
        }
    }

    override fun onListHolderDestroyed(holder: ListHolderListener.Holder) {
        if (::service.isInitialized) {
            service.bandwidthTimeout = 0
        }
    }
}