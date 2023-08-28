package com.kys.broadcastapp.utils

import android.view.View
import androidx.viewbinding.ViewBinding

fun View.gone(){
    this.visibility = View.GONE
}

fun View.show(){
    this.visibility = View.VISIBLE
}
fun View.hide(){
    this.visibility=View.INVISIBLE
}

fun ViewBinding.gone(){
    this.root.gone()
}

fun ViewBinding.show(){
    this.root.show()
}

fun View.onClick(fn:()->Unit){
    this.setOnClickListener {
        fn()
    }
}