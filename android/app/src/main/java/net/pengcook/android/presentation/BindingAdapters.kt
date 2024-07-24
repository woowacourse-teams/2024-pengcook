package net.pengcook.android.presentation

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
fun captureSelectedValue(spinner: Spinner): String {
    return spinner.selectedItem as String
}

@BindingAdapter("bind:selectedValueAttrChanged")
fun setSpinnerListener(
    spinner: Spinner,
    listener: InverseBindingListener?,
) {
    if (listener == null) {
        spinner.onItemSelectedListener = null
        return
    }

    val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

