package ma.ensa.projet.retrofit

import android.util.Base64
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import io.ktor.util.reflect.Type

class ByteArrayDeserializer : JsonDeserializer<ByteArray> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ByteArray {
        return Base64.decode(json.asString, Base64.DEFAULT)
    }
}
