package net.pengcook.android.presentation

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import net.pengcook.android.R

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
    }
}

@BindingAdapter("app:favoriteCount")
fun favoriteCountText(
    view: TextView,
    count: Long,
) {
    val context = view.context
    val countContent =
        if (count == 1L) {
            context.getString(R.string.likes_format_singular).format(count)
        } else if (count > 0) {
            context.getString(R.string.likes_format_plural).format(count)
        } else {
            context.getString(R.string.likes_format_zero)
        }

    view.text = countContent
}

@BindingAdapter("app:ingredients")
fun splitIngredients(
    view: TextView,
    ingredients: List<String>?,
) {
    val context = view.context
    if (ingredients.isNullOrEmpty()) return
    view.text = ingredients.joinToString(context.getString(R.string.ingredients_separator))
}

@BindingAdapter("app:timeRequired")
fun timeRequiredText(
    view: TextView,
    time: Int,
) {
    val context = view.context
    view.text = context.getString(R.string.time_format_required).format(time)
}
