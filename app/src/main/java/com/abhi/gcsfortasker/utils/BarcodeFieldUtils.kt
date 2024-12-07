package com.abhi.gcsfortasker.utils

import android.util.Log
import com.abhi.gcsfortasker.utils.BarcodeFieldUtils.Field.FORMAT
import com.abhi.gcsfortasker.utils.BarcodeFieldUtils.Field.TYPE
import com.google.mlkit.vision.barcode.common.Barcode

/** Methods for accessing declared fields of [Barcode]*/
object BarcodeFieldUtils {

    private const val TAG = "BarcodeFieldUtils"

    /** types of fields declared in [Barcode]. we need [FORMAT] and [TYPE] */
    enum class Field(val prefix: String) {
        /** Encoding of the code*/
        FORMAT("FORMAT_"),

        /** Type of the value*/
        TYPE("TYPE_")
    }

    private fun getCodeFields(type: Field): Map<Int, String>? {
        val fields = try {
            Barcode::class.java.fields
        } catch (e: Exception) {
            Log.e(TAG, "getCodeFields: failed for type $type", e)
            null
        }
        // ensures it's type is int, starts with prefix, removes the prefix and return as map.
        return fields?.filter { it.type === Int::class.javaPrimitiveType && it.name.startsWith(type.prefix) }
            ?.associate { it.getInt(null) to it.name.removePrefix(type.prefix) }
    }

    /** pass the [Int] values of the [Field] to returns it's name as string.)*/
    fun getNameOfTheField(value: Int, type: Field): String? {
        return getCodeFields(type)?.get(value)
    }

    /** returns an array of names for the given type*/
    fun getNamesForFieldType(type: Field): List<String> {
        return getCodeFields(type)?.filterKeys {
            //removing some unwanted fields types to prevent them using as filter
            when (type) {
                FORMAT -> it !in listOf(Barcode.FORMAT_ALL_FORMATS, Barcode.FORMAT_UNKNOWN)
                TYPE -> it !in listOf(Barcode.TYPE_UNKNOWN)
            }
        }?.values?.toList() ?: emptyList()
    }

    /** returns the integer value for the given [Barcode] field.
     * @param name of the field
     * @param field specify the [Field]
     * @return integer value of the field*/
    fun getValueForField(name: String, field: Field): Int? {
        return getCodeFields(field)?.entries?.firstOrNull {
            it.value.endsWith(name, ignoreCase = true)
        }?.key
    }
}
