package net.pengcook.android.presentation

import android.net.Uri
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import net.pengcook.android.R
import net.pengcook.android.presentation.core.listener.SpinnerItemChangeListener
import net.pengcook.android.presentation.core.model.Ingredient

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

@BindingAdapter("app:imageUri")
fun loadImage(
    view: ImageView,
    uri: Uri?,
) {
    if (uri != null) {
        Glide
            .with(view.context)
            .load(uri)
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
    ingredients: List<Ingredient>?,
) {
    val context = view.context
    if (ingredients.isNullOrEmpty()) return
    val ingredientsText =
        ingredients.joinToString(context.getString(R.string.separator)) {
            it.ingredientName
        }
    view.text = ingredientsText
}

@BindingAdapter("app:timeRequired")
fun timeRequiredText(
    view: TextView,
    time: Int,
) {
    val context = view.context
    view.text = context.getString(R.string.time_format_required).format(time)
}

@BindingAdapter("app:category")
fun categoryText(
    view: TextView,
    category: List<String>?,
) {
    val context = view.context
    if (category.isNullOrEmpty()) return
    view.text = category.joinToString(context.getString(R.string.separator))
}

@BindingAdapter("bind:selectedValue")
fun Spinner.bindSpinnerValue(value: Any?) {
    if (adapter == null) return
    val adapter = adapter as ArrayAdapter<Any>
    val position = adapter.getPosition(value)
    setSelection(position, false)
    tag = position
}

@BindingAdapter("bind:spinnerItemChangeListener")
fun Spinner.setInverseBindingListener(spinnerItemChangeListener: SpinnerItemChangeListener?) {
    onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val item = parent.getItemAtPosition(position) as String
                spinnerItemChangeListener?.onSelectionChange(item)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
}