package com.osamaalek.etest

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.osamaalek.etest.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var job : Job? = null
    private lateinit var result : Deferred<Bitmap?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // if we have a previous cached image, it will display it
        initImageView()

        binding.button.setOnClickListener {
            // to make sure only one connection at the same time
            if(job == null || job?.isActive == false)
                fetchNewImage()
        }
    }

    private fun initImageView() {
        // get cached image from internal storage
        CachingUtil.loadBitmap(this)?.apply {
            //if we have one it will display it
            binding.imageView.setImageBitmap(this)
        }

    }


    private fun fetchNewImage() {
        //disable the button
        binding.button.isEnabled = false

        result = CoroutineScope(Dispatchers.IO).async {
            try {
                // get an image from URL
                CachingUtil.getBitmapFromURL()
            } catch(e:IOException) {
                // handel the errors such as no internet, timeout, .......
                withContext(Dispatchers.Main){
                    // show general error message
                    Toast.makeText(this@MainActivity,getString(R.string.error_message_no_internet),
                        Toast.LENGTH_SHORT).show()
                    // enable the button
                    binding.button.isEnabled = true
                }
                // cancel the coroutine job (worker)
                job?.cancel()
                null
            }
        }

        // here we will handle the response (the image)
        job = CoroutineScope(Dispatchers.IO).launch {
            // it will waiting to get result
            result.await()?.apply { // if downloaded (not null)
                // save bitmap as cached image
                CachingUtil.storeBitmap(this, this@MainActivity)

                // display bitmap and enable the button
                withContext(Dispatchers.Main) {
                    binding.imageView.setImageBitmap(this@apply)
                    binding.button.isEnabled = true
                }
            }
        }
    }

}