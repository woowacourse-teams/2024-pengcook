package net.pengcook.android

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.text.DecimalFormat

@BindingAdapter("app:imageUrl")
fun loadImage(
    view: ImageView,
    url: String?,
) {
    if (!url.isNullOrEmpty()) {
        Glide
            .with(view.context)
            .load(url)
            .into(view)
    } else {
        Log.d("BindingAdapters", "loadImage: url is null or empty")
        Log.d("BindingAdapters", "loadImage: url = $url")
    }
}

@BindingAdapter("app:favoriteCount")
fun favoriteCountText(
    view: TextView,
    count: Long,
) {
    if (count == 1L) {
        view.text = "1 like"
    } else if (count > 0) {
        view.text = "${DecimalFormat("#,###").format(count)} likes"
    } else {
        view.text = ""
    }
}

@BindingAdapter("app:ingredients")
fun splitIngredients(
    view: TextView,
    ingredients: List<String>?,
) {
    if (ingredients.isNullOrEmpty()) return
    view.text = ingredients?.joinToString(", ")
}

@BindingAdapter("app:timeRequired")
fun timeRequiredText(
    view: TextView,
    time: Int,
) {
    view.text = "$time mins"
}
