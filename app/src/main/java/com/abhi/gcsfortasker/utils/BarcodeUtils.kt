package com.abhi.gcsfortasker.utils

import android.util.Log
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions

object BarcodeUtils {
    /**Get the name of the field. isFormat should be true if values is from [Barcode.getFormat]*/
    fun getNameOfTheField(value: Int, isFormat: Boolean): String? {
        val prefix = if (isFormat) "FORMAT_" else "TYPE_"
        val fields = Barcode::class.java.fields
        for (field in fields) {
            if (field.name.startsWith(prefix) && field.type == Int::class.javaPrimitiveType && field.getInt(
                    null
                ) == value
            ) {
                return field.name.removePrefix(prefix).lowercase()
            }
        }
        return null
    }

    /**Returns an formatted string array of all code formats if [isFormat == true] otherwise code types */
    fun getCodeFieldNames(isFormat: Boolean): Array<String> {
        return getCodeFields(isFormat).map { it ->
            it.split("_")
                .joinToString(" ") { it.replaceFirstChar { c: Char -> c.uppercaseChar() } }
        }.toTypedArray()
    }

    /**Returns an array of all code types without formatting*/
    fun getCodeFields(isFormat: Boolean): Array<String> {
        val prefix = if (isFormat) "FORMAT_" else "TYPE_"
        return Barcode::class.java.fields.filter { it.name.startsWith(prefix) }.map {
            it.name.replaceFirst(prefix, "").lowercase()
        }.toTypedArray()
    }

    /**sets formats for scanner option from action input filter*/
    fun setBarcodeFormatsFromString(
        builder: GmsBarcodeScannerOptions.Builder,
        formatFilter: String
    ) {
        val formatNames = formatFilter.split(",").map { it.trim() }
        val formats = mutableListOf<Int>()
        for (formatName in formatNames) {
            try {
                val constantName = "FORMAT_${formatName.uppercase()}"
                Log.d("utils", "setBarcodeFormatsFromString: $constantName")
                val field = Barcode::class.java.getDeclaredField(constantName)
                val constantValue = field.getInt(null)
                if (constantValue < 1) continue //to avoid conflicts
                formats.add(constantValue)
            } catch (e: NoSuchFieldException) {
                Log.e("utils", "formatFromString: Field not found for formatName: $formatName")
            } catch (e: IllegalAccessException) {
                Log.e(
                    "utils",
                    "formatFromString: Access to field denied for formatName: $formatName"
                )
            }
        }
        if (formats.size == 1) builder.setBarcodeFormats(formats[0]) else
            builder.setBarcodeFormats(formats[0], *formats.subList(1, formats.size).toIntArray())
    }
}