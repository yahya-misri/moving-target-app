package com.demo.movingtarget.ui

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import android.widget.TextView.OnEditorActionListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.demo.movingtarget.R
import com.demo.movingtarget.databinding.FragmentM1Binding
import com.demo.movingtarget.utils.PreferenceHelper
import com.demo.movingtarget.utils.PreferenceHelper.get
import com.demo.movingtarget.utils.PreferenceHelper.set
import com.demo.movingtarget.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class M1Fragment : Fragment(), View.OnClickListener, TextWatcher {

    private lateinit var preference: SharedPreferences
    private var selectedRadioBtnID: Int = 0
    private var soundId = 0
    private lateinit var sp: SoundPool
    private var soundIdEnd = 0
    private lateinit var spEnd: SoundPool
    private var audioAttributes: AudioAttributes? = null
    private var timerValue: Double = 0.0
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var binding: FragmentM1Binding
    private var currentView: View? = null
    lateinit var clickAudio: MediaPlayer
    lateinit var clickAudio1: MediaPlayer
    lateinit var endMP: MediaPlayer
    lateinit var endMP1: MediaPlayer
    lateinit var session: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        preference = PreferenceHelper.customPrefs(requireContext(), findNavController().currentDestination?.id.toString())

        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_m1, container, false)
            binding = FragmentM1Binding.bind(currentView!!)
            init()
            setOnClickListener()
            textWatcher()
            textChangeListener()


            binding.playerTable.p1Time.setText(preference.get("p1", ""))
            binding.playerTable.p2Time.setText(preference.get("p2", ""))
            binding.playerTable.p3Time.setText(preference.get("p3", ""))
            binding.playerTable.p4Time.setText(preference.get("p4", ""))
            binding.playerTable.p5Time.setText(preference.get("p5", ""))
            binding.playerTable.p6Time.setText(preference.get("p6", ""))
            binding.playerTable.p1Name.setText(preference.get("p1Name", ""))
            binding.playerTable.p2Name.setText(preference.get("p2Name", ""))
            binding.playerTable.p3Name.setText(preference.get("p3Name", ""))
            binding.playerTable.p4Name.setText(preference.get("p4Name", ""))
            binding.playerTable.p5Name.setText(preference.get("p5Name", ""))
            binding.playerTable.p6Name.setText(preference.get("p6Name", ""))

        }
        return currentView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioButtonId =  preference.get("currentSelectedID", 0)
        if (radioButtonId!=0){

            val radioButton = currentView?.findViewById<RadioButton>(radioButtonId)
            radioButton?.performClick()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        storeInPreference()
    }

    private fun storeInPreference() {
        preference["p1"] = binding.playerTable.p1Time.text.toString()
        preference["p2"] = binding.playerTable.p2Time.text.toString()
        preference["p3"] = binding.playerTable.p3Time.text.toString()
        preference["p4"] = binding.playerTable.p4Time.text.toString()
        preference["p5"] = binding.playerTable.p5Time.text.toString()
        preference["p6"] = binding.playerTable.p6Time.text.toString()
        preference["p1Name"] = binding.playerTable.p1Name.text.toString()
        preference["p2Name"] = binding.playerTable.p2Name.text.toString()
        preference["p3Name"] = binding.playerTable.p3Name.text.toString()
        preference["p4Name"] = binding.playerTable.p4Name.text.toString()
        preference["p5Name"] = binding.playerTable.p5Name.text.toString()
        preference["p6Name"] = binding.playerTable.p6Name.text.toString()
        preference["currentSelectedID"] = selectedRadioBtnID
    }

    private fun textChangeListener() {
        binding.playerTable.p1Time.addTextChangedListener(this)
        binding.playerTable.p2Time.addTextChangedListener(this)
        binding.playerTable.p3Time.addTextChangedListener(this)
        binding.playerTable.p4Time.addTextChangedListener(this)
        binding.playerTable.p5Time.addTextChangedListener(this)
        binding.playerTable.p6Time.addTextChangedListener(this)
    }

    fun setCountCountTimer(time: String) {
        timerValue = time.toDouble()
        Log.e("TAG", "setCountCountTimer: $timerValue")
        var interval = 200L
        countDownTimer = object : CountDownTimer((timerValue.toDouble() * 1000).toLong(), interval) {

            override fun onTick(millisUntilFinished: Long) {
                Log.e("TAG", "onTick: $millisUntilFinished")
            }

            override fun onFinish() {
                Log.e("TAG", "onFinish: ")
                if (session.endTimerSound) {

                    if (timerValue < 1000) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            delay(200)
                            spEnd.play(soundIdEnd, 500F, 500F, 1, 0, 1f)
                        }
                    } else {
                        spEnd.play(soundIdEnd, 500F, 500F, 1, 0, 1f)
                    }
                }
                binding.playerTable.startTimer.isEnabled = true
                binding.playerTable.startTimer.isVisible = true


                // logic to handle completion could go here
            }
        }


    }


    private fun textWatcher() {
        binding.playerTable.p1Time.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.action === KeyEvent.ACTION_DOWN && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {
                    if (binding.playerTable.p1Time.text.isNotEmpty()) {
                        val newValue =
                            binding.playerTable.p1Time.text.toString().toDouble() /*  /100  */
                        binding.playerTable.p1Time.setText(newValue.toString())

                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

        binding.playerTable.p2Time.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.action === KeyEvent.ACTION_DOWN && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {
                    if (binding.playerTable.p2Time.text.isNotEmpty()) {
                        val newValue =
                            binding.playerTable.p2Time.text.toString().toDouble() /* /100 */
                        binding.playerTable.p2Time.setText(newValue.toString())

                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

        binding.playerTable.p3Time.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.action === KeyEvent.ACTION_DOWN && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {
                    if (binding.playerTable.p3Time.text.isNotEmpty()) {
                        val newValue =
                            binding.playerTable.p3Time.text.toString().toDouble() /* /100 */
                        binding.playerTable.p3Time.setText(newValue.toString())

                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

        binding.playerTable.p4Time.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.action === KeyEvent.ACTION_DOWN && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {
                    if (binding.playerTable.p4Time.text.isNotEmpty()) {
                        val newValue =
                            binding.playerTable.p4Time.text.toString().toDouble() /* /100 */
                        binding.playerTable.p4Time.setText(newValue.toString())

                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

        binding.playerTable.p5Time.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.action === KeyEvent.ACTION_DOWN && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {
                    if (binding.playerTable.p5Time.text.isNotEmpty()) {
                        val newValue =
                            binding.playerTable.p5Time.text.toString().toDouble() /* /100 */
                        binding.playerTable.p5Time.setText(newValue.toString())

                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

        binding.playerTable.p6Time.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    if (binding.playerTable.p6Time.text.isNotEmpty()) {
                        val newValue =
                            binding.playerTable.p6Time.text.toString().toDouble() /* /100 */
                        binding.playerTable.p6Time.setText(newValue.toString())

                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

    }


    private fun init() {
        session = SessionManager(requireContext())
        clickAudio = MediaPlayer.create(requireActivity().applicationContext, R.raw.u_click)
        clickAudio1 = MediaPlayer.create(requireActivity().applicationContext, R.raw.u_click)
        endMP = MediaPlayer.create(context, R.raw.just_like_that)
        endMP1 = MediaPlayer.create(context, R.raw.just_like_that)
        currentView?.setOnKeyListener { _, keyCode, _ ->
            Log.d("MyFragment", "Key event received: $keyCode")
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Log.d("MyFragment", "Volume button pressed")
                onStartClick()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }



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

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {
        with(binding.playerTable) {
            p1RadioBtn.setOnClickListener(this@M1Fragment)
            p2RadioBtn.setOnClickListener(this@M1Fragment)
            p3RadioBtn.setOnClickListener(this@M1Fragment)
            p4RadioBtn.setOnClickListener(this@M1Fragment)
            p5RadioBtn.setOnClickListener(this@M1Fragment)
            p6RadioBtn.setOnClickListener(this@M1Fragment)
            startTimer.setOnClickListener {
                onStartClick()
            }

            startTimer.setOnTouchListener { _, event ->
                when (event.action) {

                    MotionEvent.ACTION_BUTTON_PRESS,
                    MotionEvent.ACTION_DOWN -> {
                        onStartClick()
                        true
                    }

                    else -> false
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.p1RadioBtn -> setRadioSelection(
                binding.playerTable.p1RadioBtn,
                binding.playerTable.p1Time.text.toString()
            )
            R.id.p2RadioBtn -> setRadioSelection(
                binding.playerTable.p2RadioBtn,
                binding.playerTable.p2Time.text.toString()
            )
            R.id.p3RadioBtn -> setRadioSelection(
                binding.playerTable.p3RadioBtn,
                binding.playerTable.p3Time.text.toString()
            )
            R.id.p4RadioBtn -> setRadioSelection(
                binding.playerTable.p4RadioBtn,
                binding.playerTable.p4Time.text.toString()
            )
            R.id.p5RadioBtn -> setRadioSelection(
                binding.playerTable.p5RadioBtn,
                binding.playerTable.p5Time.text.toString()
            )
            R.id.p6RadioBtn -> setRadioSelection(
                binding.playerTable.p6RadioBtn,
                binding.playerTable.p6Time.text.toString()
            )
        }
    }


    fun onStartClick() {
        Log.e("TAG", "onStartClick")
        validateAndStartTimer("", "0", 1)
        return

        /*if (binding.playerTable.p1RadioBtn.isChecked) {
            val p1Name = binding.playerTable.p1Name.text.toString()
            val p1Time = binding.playerTable.p1Time.text.toString()

//            if (p1Name.isEmpty() ) {
//                binding.playerTable.p1Name.error = "Cannot Be Empty"
//                return
//            }else if( p1Time.isEmpty()){
//                binding.playerTable.p1Time.error = "Cannot Be Empty"
//                return
//            }

            validateAndStartTimer(p1Name, p1Time, 1)
        } else if (binding.playerTable.p2RadioBtn.isChecked) {
            val p2Name = binding.playerTable.p2Name.text.toString()
            val p2Time = binding.playerTable.p2Time.text.toString()

//            if (p2Name.isEmpty() ) {
//                binding.playerTable.p2Name.error = "Cannot Be Empty"
//                return
//            }else if( p2Time.isEmpty()){
//                binding.playerTable.p2Time.error = "Cannot Be Empty"
//                return
//            }

            validateAndStartTimer(p2Name, p2Time, 2)
        } else if (binding.playerTable.p3RadioBtn.isChecked) {
            val p3Name = binding.playerTable.p3Name.text.toString()
            val p3Time = binding.playerTable.p3Time.text.toString()

//            if (p3Name.isEmpty() ) {
//                binding.playerTable.p6Name.error = "Cannot Be Empty"
//                return
//            }else if( p3Time.isEmpty()){
//                binding.playerTable.p3Time.error = "Cannot Be Empty"
//                return
//            }

            validateAndStartTimer(p3Name, p3Time, 3)
        } else if (binding.playerTable.p4RadioBtn.isChecked) {
            val p4Name = binding.playerTable.p4Name.text.toString()
            val p4Time = binding.playerTable.p4Time.text.toString()

//            if (p4Name.isEmpty() ) {
//                binding.playerTable.p4Name.error = "Cannot Be Empty"
//                return
//            }else if( p4Time.isEmpty()){
//                binding.playerTable.p4Time.error = "Cannot Be Empty"
//                return
//            }

            validateAndStartTimer(p4Name, p4Time, 4)
        } else if (binding.playerTable.p5RadioBtn.isChecked) {
            val p5Name = binding.playerTable.p5Name.text.toString()
            val p5Time = binding.playerTable.p5Time.text.toString()

//            if (p5Name.isEmpty() ) {
//                binding.playerTable.p5Name.error = "Cannot Be Empty"
//                return
//            }else if( p5Time.isEmpty()){
//                binding.playerTable.p5Time.error = "Cannot Be Empty"
//                return
//            }

            validateAndStartTimer(p5Name, p5Time, 5)
        } else if (binding.playerTable.p6RadioBtn.isChecked) {
            val p6Name = binding.playerTable.p6Name.text.toString()
            val p6Time = binding.playerTable.p6Time.text.toString()

//            if (p6Name.isEmpty() ) {
//                binding.playerTable.p6Name.error = "Cannot Be Empty"
//                return
//            }else if( p6Time.isEmpty()){
//                binding.playerTable.p6Time.error = "Cannot Be Empty"
//                return
//            }

            validateAndStartTimer(p6Name, p6Time, 6)
        }*/
    }

    private fun validateAndStartTimer(pName: String, pTime: String, player: Int) {
        if (pTime.isNotEmpty()) {


            if (session.startTimerSound) {
                sp.play(soundId, 1F, 1F, 1, 0, 1f)
            }
            binding.playerTable.startTimer.isEnabled = false
            binding.playerTable.startTimer.isVisible = false

            if (timerValue < 1) {
                var timer = Timer()
                var delay = timerValue * 1000
                var task = object : TimerTask() {
                    override fun run() {
                        lifecycleScope.launch(Dispatchers.Main) {
                            delay(150)
                            spEnd.play(soundIdEnd, 500F, 500F, 1, 0, 1f)
                            binding.playerTable.startTimer.isEnabled = true
                            binding.playerTable.startTimer.isVisible = true
                            Log.e("TAG", "End Timer less than one sec $delay")
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

    suspend fun one(): Int {
        Thread.sleep(timerValue.toLong() * 1000)
        delay(timerValue.toLong() * 1000)

        return 1
    }

    private fun setRadioSelection(selectedRadioBtn: RadioButton, time: String) {
        with(binding.playerTable) {
            p1RadioBtn.isChecked = false
            p2RadioBtn.isChecked = false
            p3RadioBtn.isChecked = false
            p4RadioBtn.isChecked = false
            p5RadioBtn.isChecked = false
            p6RadioBtn.isChecked = false
        }
        selectedRadioBtnID = selectedRadioBtn.id
        selectedRadioBtn.isChecked = true
        if (time.isNotEmpty())
            setCountCountTimer(time)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {

        if (p0?.isNotEmpty() == true) {
            if (p0.length == 1 && p0[0].toString() == ".") {
                if (p0.hashCode() == binding.playerTable.p1Time.text.hashCode()) {
                    binding.playerTable.p1Time.setText("0.")
                    binding.playerTable.p1Time.setSelection(binding.playerTable.p1Time.text.length)
                } else if (p0.hashCode() == binding.playerTable.p2Time.text.hashCode()) {
                    binding.playerTable.p2Time.setText("0.")
                    binding.playerTable.p2Time.setSelection(binding.playerTable.p2Time.text.length)
                } else if (p0.hashCode() == binding.playerTable.p3Time.text.hashCode()) {
                    binding.playerTable.p3Time.setText("0.")
                    binding.playerTable.p3Time.setSelection(binding.playerTable.p3Time.text.length)
                } else if (p0.hashCode() == binding.playerTable.p4Time.text.hashCode()) {
                    binding.playerTable.p4Time.setText("0.")
                    binding.playerTable.p4Time.setSelection(binding.playerTable.p4Time.text.length)
                } else if (p0.hashCode() == binding.playerTable.p5Time.text.hashCode()) {
                    binding.playerTable.p5Time.setText("0.")
                    binding.playerTable.p5Time.setSelection(binding.playerTable.p5Time.text.length)
                } else {
                    binding.playerTable.p6Time.setText("0.")
                    binding.playerTable.p6Time.setSelection(binding.playerTable.p6Time.text.length)

                }
                setCountCountTimer("0.0")
                return
            }

            setCountCountTimer(p0.toString())
        }
    }


}