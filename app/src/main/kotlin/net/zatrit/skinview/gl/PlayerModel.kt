package net.zatrit.skinview.gl

import net.zatrit.skinview.gl.PartType.*
import java.util.*

enum class PartType {
    HEAD, HEAD_EXTRA, BODY, BODY_EXTRA, RIGHT_ARM, RIGHT_ARM_EXTRA, LEFT_ARM, LEFT_ARM_EXTRA, RIGHT_LEG, RIGHT_LEG_EXTRA, LEFT_LEG, LEFT_LEG_EXTRA,
}

enum class ModelType(val handWidth: Float) {
    DEFAULT(1f), SLIM(0.75f),
}

typealias PartsMap = Map<PartType, ModelPart>
typealias EnumPartsMap = EnumMap<PartType, ModelPart>

val Box.extra get() = this.scale(1.1f)
fun Box.uv(x: Float, y: Float) = this.uv(x, y, 0.125f)

private fun createHands(modelType: ModelType): EnumPartsMap {
    val map = EnumPartsMap(PartType::class.java)
    val offset = 0.5f * (1f - modelType.handWidth)
    val rightArm = Box(-1f + offset, -0.5f, -0.25f, -0.5f, 1f, 0.25f)
    val leftArm = rightArm.translate(1.5f - offset, 0f, 0f)

    val rightArmUV = rightArm.uv(0.625f, 0.25f)
    val rightArmExtraUV = rightArm.uv(0.625f, 0.5f)
    val leftArmUV = leftArm.uv(0.5f, 0.75f)
    val leftArmExtraUV = leftArm.uv(0.75f, 0.75f)

    // Right arm
    map[RIGHT_ARM] = ModelPart(rightArm.vertices, rightArmUV)
    // Right arn extra layer
    map[RIGHT_ARM_EXTRA] = ModelPart(rightArm.extra.vertices, rightArmExtraUV)
    // Left arm
    map[LEFT_ARM] = ModelPart(leftArm.vertices, leftArmUV)
    // Left arm extra layer
    map[LEFT_ARM_EXTRA] = ModelPart(leftArm.extra.vertices, leftArmExtraUV)

    return map
}


class PlayerModel(private val options: RenderOptions) {
    private val parts = EnumMap<_, ModelPart>(PartType::class.java)
    private val typeParts = EnumMap<_, PartsMap>(ModelType::class.java)

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

        // Head
        parts[HEAD] = ModelPart(head.vertices, headUV)
        // Head extra layer
        parts[HEAD_EXTRA] = ModelPart(head.extra.vertices, headExtraUV)
        // Body
        parts[BODY] = ModelPart(body.vertices, bodyUV)
        // Body extra layer
        parts[BODY_EXTRA] = ModelPart(body.extra.vertices, bodyExtraUV)
        // Right leg
        parts[RIGHT_LEG] = ModelPart(rightLeg.vertices, rightLegUV)
        // Right leg extra layer
        parts[RIGHT_LEG_EXTRA] =
            ModelPart(rightLeg.extra.vertices, rightLegExtraUV)
        // Left leg
        parts[LEFT_LEG] = ModelPart(leftLeg.vertices, leftLegUV)
        // Left leg extra layer
        parts[LEFT_LEG_EXTRA] =
            ModelPart(leftLeg.extra.vertices, leftLegExtraUV)
    }

    fun draw() {
        parts.values.forEach { it.draw() }

        typeParts.computeIfAbsent(options.modelType) {
            createHands(it)
        }.values.forEach { it.draw() }
    }
}