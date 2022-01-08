package com.vad.signs.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.vad.signs.domain.models.Sign
import com.vad.signs.domain.models.SignGroup
import java.io.IOException

object Parser {
    fun assetsToBitmap(context: Context, fileName: String): Bitmap? {
        return try {
            with(context.assets.open(fileName)) {
                BitmapFactory.decodeStream(this)
            }
        } catch (e: IOException) {
            null
        }
    }

    fun toBitmap(byteArray: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (exception: Exception) {
            null
        }
    }

    suspend fun parserSign(context: Context): List<SignGroup> {
        val result = mutableListOf<SignGroup>()
        try {
            val inputStream = context.assets.open("data/signs.csv")
            val bufferedReader = inputStream.bufferedReader()
            var line: String?
            var group: SignGroup? = null
            var signs = mutableListOf<Sign>()

            while (bufferedReader.readLine().run {
                    line = this
                    this != null
                } && line != null) {
                val notNullLine = line!!
                if (notNullLine.startsWith('1')) {
                    if (group != null) {
                        result.add(group)
                    }
                    signs = mutableListOf()
                    val title = notNullLine.split("|")[2]
                    group = SignGroup(title, signs)
                } else {
                    val texts = notNullLine.split("|")
                    val sign = Sign(
                        texts[1],
                        texts[2],
                        description = texts[3],
                        texts[4]
                    )
                    signs.add(sign)
                    group = group?.copy(signs = signs)
                }
            }
            if (group != null && !result.contains(group)) {
                result.add(group)
            }
        } catch (exception: Exception) {
            Log.e("Parser", "error copy file", exception)
        }
        return result
    }

}
