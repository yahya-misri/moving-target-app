package com.demo.movingtarget.ui

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.demo.movingtarget.R
import com.demo.movingtarget.databinding.FragmentQuickBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*


class QuickFragment : Fragment(), View.OnClickListener {

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var binding: FragmentQuickBinding
    private var currentView: View? = null
    private var timer = ""
    private var alarmTimer = ""
    private var soundId = 0
    private lateinit var sp: SoundPool
    private var soundIdEnd = 0
    private lateinit var spEnd: SoundPool
    private var audioAttributes: AudioAttributes? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_quick, container, false)
            binding = FragmentQuickBinding.bind(currentView!!)
            init()
            setOnClickListener()
        }
        return currentView!!
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {
        with(binding) {
            one.setOnClickListener(this@QuickFragment)
            two.setOnClickListener(this@QuickFragment)
            three.setOnClickListener(this@QuickFragment)
            four.setOnClickListener(this@QuickFragment)
            five.setOnClickListener(this@QuickFragment)
            six.setOnClickListener(this@QuickFragment)
            seven.setOnClickListener(this@QuickFragment)
            eight.setOnClickListener(this@QuickFragment)
            nine.setOnClickListener(this@QuickFragment)
            zero.setOnClickListener(this@QuickFragment)
            dot.setOnClickListener(this@QuickFragment)
            clear.setOnClickListener(this@QuickFragment)
//            startTimer.setOnClickListener(this@QuickFragment)
            startTimer.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_BUTTON_PRESS,
                    MotionEvent.ACTION_DOWN -> {
                        startCountDown()
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun init() {
        audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        sp = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()
        spEnd = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()
        soundId = sp.load(requireActivity(), R.raw.just_like_that, 1)
        soundIdEnd = spEnd.load(requireActivity(), R.raw.end_sound, 1)


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.one -> {
                timer = "${timer}1"
                setTimer()

            }
            R.id.two -> {
                timer = "${timer}2"
                setTimer()

            }
            R.id.three -> {
                timer = "${timer}3"
                setTimer()

            }
            R.id.four -> {
                timer = "${timer}4"
                setTimer()

            }
            R.id.five -> {
                timer = "${timer}5"
                setTimer()

            }
            R.id.six -> {
                timer = "${timer}6"
                setTimer()

            }
            R.id.seven -> {
                timer = "${timer}7"
                setTimer()

            }
            R.id.eight -> {
                timer = "${timer}8"
                setTimer()
            }
            R.id.nine -> {
                timer = "${timer}9"
                setTimer()
            }
            R.id.zero -> {
                timer = "${timer}0"
                setTimer()
            }
            R.id.dot -> {
                timer = "${timer}."
                setTimer()
            }
            R.id.clear -> {
                timer = ""
                alarmTimer = ""
                binding.timerEdt.text = "0.00"
            }
            R.id.startTimer -> {


            }
        }
    }

    fun startCountDown() {

        if (alarmTimer.isNotEmpty()) {
            Log.e("TAG", "start Timer")
            sp.play(soundId, 1F, 1F, 1, 0, 1f)
            binding.startTimer.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red_lite
                )
            )
            binding.startTimer.isEnabled = false
            if (alarmTimer.toDouble() < 1.0f) {
                var timer = Timer()
                var delay = alarmTimer.toDouble() * 1000
                var task = object : TimerTask() {
                    override fun run() {
                        lifecycleScope.launch(Dispatchers.Main) {
                            spEnd.play(soundIdEnd, 500F, 500F, 1, 0, 1f)
                            binding.startTimer.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.red
                                )
                            )
                            Log.e("TAG", "End Timer less than one sec $delay")
                            binding.startTimer.isEnabled = true
                        }
                    }
                }
                timer.schedule(task, delay.toLong())


            } else {
                if (this::countDownTimer.isInitialized) {
                    countDownTimer.start()
                }
            }


        }
    }

    private fun setTimer() {
        val formattedValue = DecimalFormat("0.00").format(timer.toInt() / 100.0)
        alarmTimer = formattedValue
        binding.timerEdt.text = formattedValue

        countDownTimer = object : CountDownTimer(alarmTimer.toDouble().toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                spEnd.play(soundIdEnd, 1F, 1F, 1, 0, 1f)
                binding.startTimer.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                binding.startTimer.isEnabled = true

            }
        }
    }
}