package com.example.sanabi.Util

import android.content.res.Resources
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

fun ImageView.downloadImage(url:String){
    Picasso.get().load("${util.imageBaseUrl}/${url}").into(this)
}

fun ImageView.deneme(url:String){
    Picasso.get().load(url).into(this)
}
fun CircleImageView.downloadImage(url:String){
    Picasso.get().load("${util.imageBaseUrl}/${url}").into(this)
}
fun Int.dp():Int{
    return (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
}