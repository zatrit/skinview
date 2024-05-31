package zatrit.skinbread.gl.model

import zatrit.skinbread.GLContext
import zatrit.skinbread.gl.*
import java.util.*

enum class ModelType(val handWidth: Float) {
    DEFAULT(1f),
    SLIM(0.75f);

    companion object {
        fun fromName(name: String?): ModelType =
            if (name?.lowercase() == "slim") SLIM else DEFAULT
    }
}

private val Box.extra get() = this.scale(1.1f)

@OptIn(GLContext::class)
typealias Parts = Array<ModelPart>

@GLContext
private fun createHands(modelType: ModelType): Parts {
    val offset = 0.5f * (1f - modelType.handWidth)
    val rightArm = Box(-1f + offset, -0.5f, -0.25f, -0.5f, 1f, 0.25f)
    val leftArm = rightArm.translate(1.5f - offset, 0f, 0f)

    val rightArmUV = rightArm.uv(0.625f, 0.25f)
    val rightArmExtraUV = rightArm.uv(0.625f, 0.5f)
    val leftArmUV = leftArm.uv(0.5f, 0.75f)
    val leftArmExtraUV = leftArm.uv(0.75f, 0.75f)

    return arrayOf(
      ModelPart(rightArm.vertices, rightArmUV),
      ModelPart(rightArm.extra.vertices, rightArmExtraUV),
      ModelPart(leftArm.vertices, leftArmUV),
      ModelPart(leftArm.extra.vertices, leftArmExtraUV),
    )
}

@GLContext
class PlayerModel {
    private val parts: Parts
    private val typeParts = EnumMap<_, Parts>(ModelType::class.java)
    var modelType = ModelType.DEFAULT

    init {
        val head = Box(-0.5f, 1f, -0.5f, 0.5f, 2f, 0.5f)
        val body = Box(-0.5f, -0.5f, -0.25f, 0.5f, 1f, 0.25f)
        val rightLeg = Box(-0.5f, -2f, -0.25f, 0f, -0.5f, 0.25f)
        val leftLeg = rightLeg.translate(0.5f, 0f, 0f)

        val headUV = head.uv(0f, 0f)
        val headExtraUV = head.uv(0.5f, 0f)
        val bodyUV = body.uv(0.25f, 0.25f)
        val bodyExtraUV = body.uv(0.25f, 0.5f)

        val rightLegUV = rightLeg.uv(0f, 0.25f)
        val rightLegExtraUV = rightLeg.uv(0f, 0.5f)
        val leftLegUV = leftLeg.uv(0.25f, 0.75f)
        val leftLegExtraUV = leftLeg.uv(0f, 0.75f)

        parts = arrayOf(
          // Head
          ModelPart(head.vertices, headUV),
          // Head extra layer
          ModelPart(head.extra.vertices, headExtraUV),
          // Body
          ModelPart(body.vertices, bodyUV),
          // Body extra layer
          ModelPart(body.scale(1.1002f).vertices, bodyExtraUV),
          // Right leg
          ModelPart(rightLeg.vertices, rightLegUV),
          // Right leg extra layer
          ModelPart(rightLeg.scale(1.1001f).vertices, rightLegExtraUV),
          // Left leg
          ModelPart(leftLeg.vertices, leftLegUV),
          // Left leg extra layer
          ModelPart(leftLeg.extra.vertices, leftLegExtraUV),
        )
    }

    fun render() {
        parts.forEach { it.render() }

        (typeParts[modelType] ?: typeParts.put(
          modelType, createHands(modelType)
        ))?.forEach { it.render() }
    }
}
