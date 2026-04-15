package com.el.mybasekotlin.utils.extension

import com.el.mybasekotlin.data.response.BaseError
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun String.toRequestBodyPart(): RequestBody = this.toRequestBody(this.toMediaTypeOrNull())

inline fun <reified T : Any> Any.mapTo(): T =
    GsonBuilder().create().run {
        fromJson(toJson(this@mapTo), T::class.java)
    }

fun String.mapErrorMessage(): BaseError? {
    return try {
        Gson().fromJson(this, BaseError::class.java)
    } catch (e: Exception) {
        null
    }
}

fun objectToJson(T: Any): String {
    return Gson().toJson(T)
}
//val user = parseJson<User>("{\"name\": \"John\"}") //
inline fun <reified T> parseJson(json: String): T {
    return Gson().fromJson(json, T::class.java)
}




fun String.generateDataClass(className: String = "GeneratedClass") {
    val mapType = object : TypeToken<Map<String, Any>>() {}.type
    val jsonMap: Map<String, Any> = Gson().fromJson(this, mapType)

    val classes = mutableListOf<String>()
    val mainClass = buildDataClass(className, jsonMap, classes)

    // In ra tất cả các class đã tạo
    (classes + mainClass).forEach { println(it) }
}

private fun buildDataClass(className: String, jsonMap: Map<String, Any>, classes: MutableList<String>): String {
    val classBuilder = StringBuilder("data class $className(\n")

    jsonMap.forEach { (key, value) ->
        val kotlinType = when (value) {
            is String -> "String"
            is Int -> "Int"
            is Double, is Float -> "Double"
            is Boolean -> "Boolean"
            is List<*> -> "List<Any>"
            is Map<*, *> -> {
                // Nếu giá trị là Map, tạo class con
                val subClassName = key.replaceFirstChar { it.uppercaseChar() } // Viết hoa chữ cái đầu
                classes.add(buildDataClass(subClassName, value as Map<String, Any>, classes))
                subClassName
            }
            else -> "Any?"
        }
        classBuilder.append("    @SerializedName(\"$key\")\n")
        classBuilder.append("    val $key: $kotlinType,\n")
    }

    // Xóa dấu phẩy cuối cùng và đóng class
    return classBuilder.toString().trimEnd(',', '\n') + "\n)"
}



//fun String.printJsonFieldsWithSerializedName(className: String = "GeneratedClass") {
//    val mapType = object : TypeToken<Map<String, Any>>() {}.type
//    val jsonMap: Map<String, Any> = Gson().fromJson(this, mapType)
//
//    println("data class $className(")
//    jsonMap.forEach { (key, value) ->
//        val kotlinType = when (value) {
//            is String -> "String"
//            is Int -> "Int"
//            is Double, is Float -> "Double"
//            is Boolean -> "Boolean"
//            is List<*> -> "List<Any>"
//            is Map<*, *> -> "Map<String, Any>"
//            else -> "Any?"
//        }
//        println("    @SerializedName(\"$key\")")
//        println("    val $key: $kotlinType,")
//    }
//    println(")")
//}