package net.pengcook.android.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class OutlineTextView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : AppCompatTextView(context, attrs, defStyleAttr) {
        // 테두리(스트로크) 설정 – 필요에 따라 XML 속성으로 확장할 수 있습니다.
        private val strokeColor: Int = Color.BLACK
        private val strokeWidth: Float = 2.0f

        override fun onDraw(canvas: Canvas) {
            // 텍스트가 이미 레이아웃 되어 있다면 직접 그리도록 합니다.
            val textLayout = layout
            if (textLayout == null) {
                super.onDraw(canvas)
                return
            }

            // 캔버스 상태 저장 및 TextView의 패딩에 맞게 좌표 이동
            canvas.save()
            canvas.translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())

            // 현재 TextView에 설정된 텍스트 색상(흰색으로 XML에서 지정되었다면 흰색)이 채움 색상입니다.
            val fillColor = currentTextColor

            // 1. 스트로크(테두리)를 먼저 그립니다.
            val textPaint: Paint = paint
            textPaint.style = Paint.Style.STROKE
            textPaint.strokeWidth = strokeWidth
            textPaint.color = strokeColor
            textLayout.draw(canvas)

            // 2. 텍스트 채우기를 그립니다.
            textPaint.style = Paint.Style.FILL
            textPaint.color = fillColor
            textLayout.draw(canvas)

            canvas.restore()
        }
    }
