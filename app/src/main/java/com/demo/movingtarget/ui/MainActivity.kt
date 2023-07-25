package com.demo.movingtarget.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.demo.movingtarget.R
import com.demo.movingtarget.databinding.ActivityMainBinding
import com.demo.movingtarget.utils.PreferenceHelper

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PreferenceHelper.clearAllSharedPreferences(this)

        navController = findNavController(R.id.navHostFragment)
        setOnClickListeners()

    }

    private fun setOnClickListeners() {
        with(binding) {
            m1.setOnClickListener(this@MainActivity)
            m2.setOnClickListener(this@MainActivity)
            m3.setOnClickListener(this@MainActivity)
            m4.setOnClickListener(this@MainActivity)
            quick.setOnClickListener(this@MainActivity)
            settings.setOnClickListener(this@MainActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.m1 -> {
                setSelection(binding.m1)
                navController.navigate(R.id.m1Fragment)
            }
            R.id.m2 -> {
                setSelection(binding.m2)
                navController.navigate(R.id.m2Fragment)
            }
            R.id.m3 -> {
                setSelection(binding.m3)
                navController.navigate(R.id.m3Fragment)
            }
            R.id.m4 -> {
                setSelection(binding.m4)
                navController.navigate(R.id.m4Fragment)
            }
            R.id.quick -> {
                setSelection(binding.quick)
                navController.navigate(R.id.quickFragment)
            }
            R.id.settings -> {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
        }
    }

    private fun setSelection(selectedView: TextView) {
        binding.m1.background = ContextCompat.getDrawable(this, R.drawable.text_view)
        binding.m2.background = ContextCompat.getDrawable(this, R.drawable.text_view)
        binding.m3.background = ContextCompat.getDrawable(this, R.drawable.text_view)
        binding.m4.background = ContextCompat.getDrawable(this, R.drawable.text_view)
        binding.quick.background = ContextCompat.getDrawable(this, R.drawable.text_view)
        selectedView.background = ContextCompat.getDrawable(this, R.drawable.text_view_selected)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            when (val currentFragment =
                supportFragmentManager.findFragmentById(R.id.navHostFragment)?.childFragmentManager?.fragments?.first()) {
                is M1Fragment -> {
                    currentFragment.onStartClick()
                }
                is M2Fragment -> {
                    currentFragment.onStartClick()
                }
                is M3Fragment -> {
                    currentFragment.onStartClick()
                }
                is M4Fragment -> {
                    currentFragment.onStartClick()
                }
                is QuickFragment -> {
                    currentFragment.startCountDown()
                }
            }
            return true
        }
        return super.onKeyUp(keyCode, event)
    }
}