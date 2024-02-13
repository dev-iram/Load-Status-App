package com.udacity

import android.Manifest
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager

    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadManager: DownloadManager
    private val REQUEST_CODE_PERMISSION = 123

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        notificationManager = ContextCompat.getSystemService(
            applicationContext, NotificationManager::class.java
        ) as NotificationManager
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            RECEIVER_NOT_EXPORTED
        )
        binding.mainContent.loadingButton.setOnClickListener {
            getUrlAndDownloadSelected()
        }
        createChannel(CHANNEL_ID, "DownloadApp")
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query()
            id?.let {
                query.setFilterById(it)
            }
            val cursor = downloadManager.query(query)
            if (cursor?.moveToFirst() == true) {
                val columnTitle = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
                val columnStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val title = cursor.getString(columnTitle)
                val status = cursor.getInt(columnStatus)
                val downloadContent = getDownloadContent(title, status)
                sendNotification(downloadContent)
                binding.mainContent.loadingButton.cancelAnimation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkNotificationPermission()
    }
    private fun checkNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission if not granted already
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can send the notification here if content is accessible.
                // notificationManager.sendNotification(applicationContext, CHANNEL_ID, content)
            } else {
                // Permission denied, handle it accordingly
                Toast.makeText(
                    applicationContext,
                    "Permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendNotification(content: DownloadContent) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission if not granted already
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_PERMISSION
            )
        } else {
            // Permission already granted, send notification
            notificationManager.sendNotification(applicationContext, CHANNEL_ID, content)
        }
    }

    private fun getUrlAndDownloadSelected() {
        when (binding.mainContent.selectOption.checkedRadioButtonId) {
            R.id.glide_option -> {
                download(GLIDE_URL, getString(R.string.glide_file_download))
            }

            R.id.load_app_option -> {
                download(LOADING_APP_URL, getString(R.string.loadapp_file_download))
            }

            R.id.retrofit_option -> {
                download(RETROFIT_URL, getString(R.string.retrofit_file_download))
            }

            else -> {
                Toast.makeText(
                    applicationContext,
                    getText(R.string.error_no_file_selected),
                    Toast.LENGTH_SHORT
                ).show()
                download()
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.description = "Time for breakfast"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getDownloadContent(title: String, status: Int): DownloadContent {
        var statusReal: DownloadState = DownloadState.SUCCESS
        if (status == DownloadManager.STATUS_FAILED) {
            statusReal = DownloadState.FAILED
        }
        return DownloadContent(title, statusReal)
    }

    private fun download(url: String? = null, title: String? = null) {
        url?.let {
            val request =
                DownloadManager.Request(Uri.parse(url)).setTitle(title)
                    .setDescription(getString(R.string.app_description)).setRequiresCharging(false)
                    .setAllowedOverMetered(true).setAllowedOverRoaming(true)
            downloadID = downloadManager.enqueue(request)
        }
        binding.mainContent.loadingButton.let {
            it.downloadBegin()
            if(url.isNullOrBlank() && title.isNullOrBlank()){
                it.cancelAnimation()
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "channelId"
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val LOADING_APP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"
    }
}