package com.demo.movingtarget.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.demo.movingtarget.R
import com.demo.movingtarget.databinding.ActivitySettingsBinding
import com.demo.movingtarget.databinding.FragmentSettingsBinding
import com.demo.movingtarget.utils.SessionManager

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var session: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = SessionManager(this)
        binding.startTimerCheckBox.isChecked = session.startTimerSound
        binding.endTimerCheckBox.isChecked = session.endTimerSound
        setOnClickListeners()

    }

    private fun setOnClickListeners() {
        binding.backArrow.setOnClickListener(this@SettingsActivity)
        binding.startTimerCheckBox.setOnClickListener(this@SettingsActivity)
        binding.endTimerCheckBox.setOnClickListener(this@SettingsActivity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> finish()
            R.id.startTimerCheckBox -> session.startTimerSound = !session.startTimerSound
            R.id.endTimerCheckBox -> session.endTimerSound = !session.endTimerSound
        }
    }
}