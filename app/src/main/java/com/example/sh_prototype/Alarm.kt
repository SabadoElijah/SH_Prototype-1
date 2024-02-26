// Import statements at the top of Alarm fragment file
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sh_prototype.AlarmReceiver
import com.example.sh_prototype.AlarmService
import com.example.sh_prototype.AlarmViewModel
import com.example.sh_prototype.R
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Alarm : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var setAlarmButton: Button
    private lateinit var timeSetTextView: TextView
    private var alarmHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var alarmRunnable: Runnable

    private var alarmViewModel: AlarmViewModel? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)

        setAlarmButton = view.findViewById(R.id.setAlarmButton)
        setAlarmButton.setOnClickListener { showTimePickerDialog() }

        timeSetTextView = view.findViewById(R.id.TimeSet)

        // Initialize the ViewModel
        alarmViewModel = ViewModelProvider(requireActivity()).get(AlarmViewModel::class.java)

        // Initialize the Runnable
        alarmRunnable = Runnable { checkAndUpdateAlarmText() }

        // Start the initial task
        alarmHandler.post(alarmRunnable)

        return view
    }

    override fun onResume() {
        super.onResume()

        // Update the text view immediately
        checkAndUpdateAlarmText()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Remove callbacks when the fragment is destroyed
        alarmHandler.removeCallbacksAndMessages(null)
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                // Save the selected time to ViewModel
                alarmViewModel?.apply {
                    isAlarmSet = true
                    alarmHour = hour
                    alarmMinute = minute
                }

                // Set the alarm
                setAlarm()
            },
            hourOfDay,
            minute,
            true
        )

        timePickerDialog.show()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm() {
        val alarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create an Intent for the BroadcastReceiver
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Set the alarm to trigger at the selected time
        val calendar = Calendar.getInstance()
        alarmViewModel?.apply {
            calendar.set(Calendar.HOUR_OF_DAY, alarmHour)
            calendar.set(Calendar.MINUTE, alarmMinute)
            calendar.set(Calendar.SECOND, 0)
        }

        // Schedule the alarm
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        // Start the AlarmService to keep the app running in the background
        startAlarmService()

        // Update the text view immediately
        checkAndUpdateAlarmText()

    }

    private fun startAlarmService() {
        val serviceIntent = Intent(requireContext(), AlarmService::class.java)
        requireContext().startService(serviceIntent)
    }

    private fun showAlarmNotification() {
        val rootView = requireActivity().findViewById<View>(android.R.id.content)

        val snackbar = Snackbar.make(
            rootView,
            "Alarm Triggered",
            Snackbar.LENGTH_LONG
        )

        // You can customize the appearance of the Snackbar
        snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.black))
        snackbar.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        snackbar.show()
    }

    private fun checkAndUpdateAlarmText() {
        alarmViewModel?.apply {
            // Get the current time
            val currentTime = Calendar.getInstance()

            // Check if the scheduled time is in the past
            val scheduledTime = Calendar.getInstance()
            scheduledTime.set(Calendar.HOUR_OF_DAY, alarmHour)
            scheduledTime.set(Calendar.MINUTE, alarmMinute)
            scheduledTime.set(Calendar.SECOND, 0)

            if (scheduledTime.time == currentTime.time) {
                // If the scheduled time is in the past or equal to the current time, reset the text
                showAlarmNotification()
                timeSetTextView.text = "Set an alarm"

                // Stop the media player if it's playing
                mediaPlayer?.stop()
                return
            } else {
                // If the scheduled time is in the future, format and update the TextView with the set alarm time
                val formattedHour = String.format("%02d", alarmHour)
                val formattedMinute = String.format("%02d", alarmMinute)

                // Use HTML formatting to make the hour and minute bold
                val formattedText = "Alarm set for: <br><b>$formattedHour:$formattedMinute</b>"
                timeSetTextView.text = HtmlCompat.fromHtml(
                    formattedText,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

                // Set the text size directly
                val textSize = 100 // your desired text size in pixels
                timeSetTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())

                // Center align the text in the TextView
                timeSetTextView.gravity = Gravity.CENTER


            }
        }

        // Schedule the task to run again after a delay
        alarmHandler.postDelayed(alarmRunnable, 1 * 1000)
    }

    private fun playAlarmSound() {
        // Stop any existing media player
        mediaPlayer?.release()

        // Create a new media player
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alarm_sound)

        // Start playing the sound
        mediaPlayer?.start()
    }


    companion object {
        private const val ALARM_REQUEST_CODE = 123

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Alarm().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
