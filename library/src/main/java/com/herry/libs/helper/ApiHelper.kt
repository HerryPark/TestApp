package com.herry.libs.helper

import android.os.Build

@Suppress("MemberVisibilityCanBePrivate", "unused")
class ApiHelper {

    companion object {
        fun hasLollipop(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

        fun hasMarshmallow(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

        fun hasNougat(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N // 24

        fun hasNougatPlusPlus(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 // 25

        fun hasOreo(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O // 26

        fun hasOSv8(): Boolean = hasOreo()

        fun hasPie(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P // 28

        fun hasOSv9(): Boolean = hasPie()

        fun hasOSv10(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q // 29

    }
}