package com.mikeschvedov.fooddiary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.mikeschvedov.fooddiary.databinding.ActivityNewEntryBinding

class NewEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewEntryBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            buttonSave.setOnClickListener {
                val replyIntent = Intent()
                if (TextUtils.isEmpty(editWord.text)) {
                    setResult(Activity.RESULT_CANCELED, replyIntent)
                } else {


                    val word = editWord.text.toString()
                    val number = 20
                    // Passing the values we got from the form as extras
                    replyIntent.putExtra("name_extra", word)
                    replyIntent.putExtra("num_extra", number)
                    setResult(Activity.RESULT_OK, replyIntent)

                }
                finish()
            }
        }


    }

}