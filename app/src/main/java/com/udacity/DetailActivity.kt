package com.udacity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

        private lateinit var binding: ActivityDetailBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityDetailBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setSupportActionBar(binding.toolbar)

            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                val data = bundle.get(DATA_INFO_CONTENT) as? DownloadContent
                data?.let {
                    with(binding.contentDetail) {
                        fileText.text = it.title
                        statusText.text = it.status.text
                        statusText.setTextColor(
                            if (it.status == DownloadState.SUCCESS)
                                getColor(R.color.colorPrimaryDark)
                            else
                                getColor(R.color.colorAccent)
                        )
                    }
                }
            }

            binding.contentDetail.okButton.setOnClickListener {
                finish()
            }
        }
    }

