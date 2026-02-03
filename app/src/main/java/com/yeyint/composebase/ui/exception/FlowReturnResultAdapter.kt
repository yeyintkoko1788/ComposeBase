package com.yeyint.composebase.ui.exception

import com.yeyint.composebase.domain.model.FlowReturnResult
import com.google.gson.TypeAdapter
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class FlowReturnResultAdapter : TypeAdapter<FlowReturnResult<*>>() {

    override fun write(out: JsonWriter, value: FlowReturnResult<*>?) {
        if (value == null) {
            out.nullValue()
            return
        }
        // Serialize based on the instance type
        when (value) {
            is FlowReturnResult.PositiveResult -> Gson().toJson(value, FlowReturnResult.PositiveResult::class.java)
            is FlowReturnResult.ErrorResult -> Gson().toJson(value, FlowReturnResult.ErrorResult::class.java)
            else -> throw IllegalArgumentException("Unknown FlowReturnResult type")
        }
    }

    override fun read(reader: JsonReader): FlowReturnResult<*>? {
        val jsonElement = JsonParser.parseReader(reader)
        return try {
            when {
                jsonElement.asJsonObject.has("errorMsg") -> Gson().fromJson(jsonElement, FlowReturnResult.ErrorResult::class.java)
                jsonElement.asJsonObject.has("data") -> Gson().fromJson(jsonElement, FlowReturnResult.PositiveResult::class.java)
                else -> throw JsonSyntaxException("Unknown FlowReturnResult type")
            }
        } catch (e: Exception) {
            null
        }
    }
}
