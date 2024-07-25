package net.pengcook.android.presentation

import android.net.Uri
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import net.pengcook.android.R
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
fun bindSpinnerData(
    spinner: Spinner,
    newValue: String?,
) {
    val adapter = spinner.adapter as? ArrayAdapter<String>
    val position = adapter?.getPosition(newValue)
    if (position != null && position != spinner.selectedItemPosition) {
        spinner.setSelection(position, true)
    }
}

@InverseBindingAdapter(attribute = "bind:selectedValue", event = "bind:selectedValueAttrChanged")
fun captureSelectedValue(spinner: Spinner): String = spinner.selectedItem as String

@BindingAdapter("bind:selectedValueAttrChanged")
fun setSpinnerListener(
    spinner: Spinner,
    listener: InverseBindingListener?,
) {
    if (listener == null) {
        spinner.onItemSelectedListener = null
        return
    }

    val onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                listener.onChange()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // do nothing
            }
        }

    spinner.onItemSelectedListener = onItemSelectedListener
}
