package com.zam.storyapp.ui.detaillist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.zam.storyapp.R
import com.zam.storyapp.data.source.model.StoryResponse
import com.zam.storyapp.databinding.ActivityDetailStoryBinding
import com.zam.storyapp.ui.homelist.ListStoryActivity

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var detailStoryBinding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailStoryBinding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(detailStoryBinding.root)

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        supportActionBar?.hide()

        setupView()
    }

    @Suppress("DEPRECATION")
    private fun setupView() {
        val detail = intent.getParcelableExtra<StoryResponse.Story>(EXTRA_DETAIL)

        detailStoryBinding.apply {
            tvDetailName.text = detail?.name
            tvDetailDescription.text = detail?.description
        }
        Glide.with(applicationContext)
            .load(detail?.photoUrl)
            .apply(RequestOptions.placeholderOf(R.drawable.no_image).error(R.drawable.no_image))
            .into(detailStoryBinding.ivDetailPhoto)

        detailStoryBinding.buttonKembali.setOnClickListener{
            Intent(this, ListStoryActivity::class.java).also { startActivity(it) }
        }
    }
    companion object{
        const val EXTRA_DETAIL = "extra_detail"
    }
}
