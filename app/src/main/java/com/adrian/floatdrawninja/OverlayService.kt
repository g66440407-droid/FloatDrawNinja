package com.adrian.floatdrawninja

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private lateinit var drawingView: DrawingView
    private lateinit var toolbar: LinearLayout
    private lateinit var drawingParams: WindowManager.LayoutParams
    private lateinit var toolbarParams: WindowManager.LayoutParams
    private var drawingMode = false
    private var lineMode = true

    override fun onCreate() {
        super.onCreate()
        startAsForeground()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        drawingView = DrawingView()
        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        drawingParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START }
        wm.addView(drawingView, drawingParams)
        buildToolbar(overlayType)
    }

    private fun startAsForeground() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(NotificationChannel("float_draw", "Dibujador flotante", NotificationManager.IMPORTANCE_LOW))
        }
        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "float_draw").setContentTitle("Dibujador flotante activo").setContentText("La herramienta está disponible sobre NinjaTrader").setSmallIcon(android.R.drawable.ic_menu_edit).build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this).setContentTitle("Dibujador flotante activo").setContentText("La herramienta está disponible sobre NinjaTrader").setSmallIcon(android.R.drawable.ic_menu_edit).build()
        }
        startForeground(41, notification)
    }

    private fun buildToolbar(type: Int) {
        toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(6, 6, 6, 6)
            background = GradientDrawable().apply { setColor(0xEE202833.toInt()); cornerRadius = 18f; setStroke(2, 0xFF8B9AAF.toInt()) }
        }
        val title = TextView(this).apply {
            text = "NQ • DIBUJO"
            setTextColor(Color.WHITE); textSize = 12f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER
            setPadding(3, 3, 3, 7)
        }
        toolbar.addView(title)
        addButton("LÍNEA H", true) { lineMode = true; drawingMode = true; updateMode() }
        addButton("TRAZO", true) { lineMode = false; drawingMode = true; updateMode() }
        addButton("PASAR", true) { drawingMode = false; updateMode() }
        addButton("DESHACER", false) { drawingView.undo() }
        addButton("BORRAR", false) { drawingView.clearAll() }
        addButton("SALIR", false) { stopSelf() }
        toolbarParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.END; x = 8; y = 150 }
        toolbar.setOnTouchListener(object : View.OnTouchListener {
            private var startX = 0; private var startY = 0; private var touchX = 0f; private var touchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { startX = toolbarParams.x; startY = toolbarParams.y; touchX = event.rawX; touchY = event.rawY; return false }
                    MotionEvent.ACTION_MOVE -> { toolbarParams.x = startX - (event.rawX - touchX).toInt(); toolbarParams.y = startY + (event.rawY - touchY).toInt(); try { wm.updateViewLayout(toolbar, toolbarParams) } catch (_: Exception) {}; return true }
                }
                return false
            }
        })
        wm.addView(toolbar, toolbarParams)
        updateMode()
    }

    private fun addButton(label: String, prominent: Boolean, action: () -> Unit) {
        val button = Button(this).apply {
            text = label; textSize = 10f; isAllCaps = false
            setTextColor(if (prominent) Color.WHITE else 0xFF17202A.toInt())
            background = GradientDrawable().apply { setColor(if (prominent) 0xFF34506B.toInt() else 0xFFE6EAF0.toInt()); cornerRadius = 10f }
            setPadding(5, 0, 5, 0)
            minHeight = 38; minimumHeight = 38; minWidth = 74
            setOnClickListener { action() }
        }
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(0, 2, 0, 2)
        toolbar.addView(button, lp)
    }

    private fun updateMode() {
        drawingParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        if (!drawingMode) drawingParams.flags = drawingParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        try { wm.updateViewLayout(drawingView, drawingParams) } catch (_: Exception) {}
        drawingView.setMode(lineMode)
    }

    override fun onDestroy() {
        try { wm.removeView(drawingView) } catch (_: Exception) {}
        try { wm.removeView(toolbar) } catch (_: Exception) {}
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null

    inner class DrawingView : View(this@OverlayService) {
        private data class Mark(val path: Path?, val y: Float?, val color: Int, val width: Float)
        private val marks = mutableListOf<Mark>()
        private var activePath: Path? = null
        private var currentColor = 0xFFFFD54F.toInt()
        private var useLine = true
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND; strokeWidth = 5f; color = currentColor }
        init { setLayerType(View.LAYER_TYPE_SOFTWARE, null) }
        fun setMode(line: Boolean) { useLine = line }
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            marks.forEach { mark ->
                paint.color = mark.color; paint.strokeWidth = mark.width
                if (mark.y != null) canvas.drawLine(0f, mark.y, width.toFloat(), mark.y, paint)
                else if (mark.path != null) canvas.drawPath(mark.path, paint)
            }
            activePath?.let { paint.color = currentColor; paint.strokeWidth = 5f; canvas.drawPath(it, paint) }
        }
        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (useLine) { marks.add(Mark(null, event.y, currentColor, 5f)); invalidate() }
                    else { activePath = Path().apply { moveTo(event.x, event.y) }; invalidate() }
                    return true
                }
                MotionEvent.ACTION_MOVE -> { if (!useLine) { activePath?.lineTo(event.x, event.y); invalidate() }; return true }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { if (!useLine && activePath != null) { marks.add(Mark(Path(activePath), null, currentColor, 5f)); activePath = null; invalidate() }; return true }
            }
            return true
        }
        fun undo() { if (marks.isNotEmpty()) marks.removeAt(marks.lastIndex); invalidate() }
        fun clearAll() { marks.clear(); activePath = null; invalidate() }
    }
}
