package com.tfuerholzer.darkmodewallpaper.math

fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)