package net.zatrit.skinview.skins.layers

import android.graphics.*
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.data.*
import net.zatrit.skins.lib.texture.LazyTexture
import java.io.ByteArrayOutputStream

typealias BitmapLayer = Layer<Bitmap>

abstract class ImageLayer(private val layer: BitmapLayer) : Layer<TypedTexture> {
    abstract fun predicate(input: TypedTexture): Boolean

    override fun apply(input: TypedTexture): TypedTexture {
        return if (!predicate(input)) {
            input
        } else {
            val old = input.texture
            val bitmap = BitmapFactory.decodeByteArray(
                old.bytes, 0, old.bytes.size
            )

            val texture = BitmapTexture(layer.apply(bitmap), old)
            TypedTexture(texture, input.type)
        }
    }
}

class BitmapTexture(
    val bitmap: Bitmap, id: String, metadata: Metadata?) :
    LazyTexture(id, metadata) {

    constructor(bitmap: Bitmap, base: Texture) : this(
        bitmap, base.id, base.metadata
    )

    override fun getBytes(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.PNG, 100, outputStream
        )

        return outputStream.toByteArray()
    }
}