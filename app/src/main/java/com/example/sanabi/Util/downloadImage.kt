package com.example.sanabi.Util

import android.content.res.Resources
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

fun ImageView.downloadImage(url:String){
    Picasso.get().load("${util.imageBaseUrl}/${url}").into(this)
}

fun ImageView.deneme(url:String){
    Picasso.get().load(url).into(this)
}
fun CircleImageView.downloadImage(url:String){
    Picasso.get().load("${util.imageBaseUrl}/${url}").into(this)
}

fun EditText.dateFormat(cal: Calendar){
    val format = SimpleDateFormat("dd.MM.yyyy")
    val strDate: String = format.format(cal.getTime())
    this.setText(strDate)
}

fun TextView.decimalFormet(double: Double) {
    val otherSymbol = DecimalFormatSymbols()
    otherSymbol.decimalSeparator = ','
    otherSymbol.groupingSeparator = '.'
    val df = DecimalFormat("0.00")
    df.decimalFormatSymbols = otherSymbol
    df.roundingMode = RoundingMode.DOWN
    this.setText("${df.format(double)} TL")
}