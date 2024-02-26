import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        // You can handle the alarm action here.
        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_SHORT).show()
    }
}
