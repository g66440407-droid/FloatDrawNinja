package com.adrian.floatdrawninja

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 36, 28, 28)
        }
        root.addView(TextView(this).apply {
            text = "Dibujador flotante"
            textSize = 25f
            setTextColor(0xFF17202A.toInt())
        })
        root.addView(TextView(this).apply {
            text = "Herramienta manual para marcar soportes, resistencias y trazos sobre NinjaTrader Mobile. Funciona como una capa visual; no lee los precios del gráfico."
            textSize = 16f
            setPadding(0, 18, 0, 24)
        })
        root.addView(Button(this).apply {
            text = "1. Dar permiso de superposición"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    startActivity(intent)
                } else Toast.makeText(this@MainActivity, "El permiso ya está habilitado", Toast.LENGTH_SHORT).show()
            }
        }, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        root.addView(Button(this).apply {
            text = "2. Iniciar dibujador flotante"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    Toast.makeText(this@MainActivity, "Primero habilitá 'Mostrar sobre otras apps'", Toast.LENGTH_LONG).show()
                } else {
                    val intent = Intent(this@MainActivity, OverlayService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
                    Toast.makeText(this@MainActivity, "Dibujador iniciado. Abrí NinjaTrader.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        root.addView(Button(this).apply {
            text = "Cómo usarlo"
            setOnClickListener {
                Toast.makeText(this@MainActivity, "Usá Línea para una horizontal o Trazo para dibujar. Tocá PASAR para volver a usar el gráfico.", Toast.LENGTH_LONG).show()
            }
        }, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setContentView(root)
    }
}
