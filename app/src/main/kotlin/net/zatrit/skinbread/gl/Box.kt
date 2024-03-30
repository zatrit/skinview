package net.zatrit.skinbread.gl

class Box(
    private val x1: Float, private val y1: Float, private val z1: Float,
    private val x2: Float, private val y2: Float, private val z2: Float
) {
    private val width = x2 - x1
    private val height = y2 - y1
    private val depth = z2 - z1

    val vertices = floatArrayOf(
        // back
        x1, y2, z1, // top right
        x2, y2, z1, // bottom right
        x2, y1, z1, // bottom left
        x1, y1, z1, // top left

        // front
        x1, y2, z2, // top right
        x2, y2, z2, // bottom right
        x2, y1, z2, // bottom left
        x1, y1, z2, // top left

        // left
        x1, y2, z2, // top right
        x1, y2, z1, // bottom right
        x1, y1, z1, // bottom left
        x1, y1, z2, // top left

        // right
        x2, y2, z1, // top right
        x2, y2, z2, // bottom right
        x2, y1, z2, // bottom left
        x2, y1, z1, // top left

        // bottom
        x1, y1, z1, // top right
        x2, y1, z1, // bottom right
        x2, y1, z2, // bottom left
        x1, y1, z2, // top left

        // top
        x1, y2, z1, // top right
        x2, y2, z1, // bottom right
        x2, y2, z2, // bottom left
        x1, y2, z2, // top left
    )

    init {
        assert(x1 < x2)
        assert(y1 < y2)
        assert(z1 < z2)
    }

    fun translate(x: Float, y: Float, z: Float): Box =
        Box(x1 + x, y1 + y, z1 + z, x2 + x, y2 + y, z2 + z)

    fun scale(s: Float): Box {
        val s2 = (s - 1) / 2
        val dw = width * s2
        val dh = height * s2
        val dd = depth * s2

        return Box(x1 - dw, y1 - dh, z1 - dd, x2 + dw, y2 + dh, z2 + dd)
    }

    fun uv(x: Float, y: Float, s: Float) =
        boxUV(x, y, width * s, height * s, depth * s)
}

fun boxUV(x: Float, y: Float, w: Float, h: Float, d: Float): FloatArray {
    // Column [number]
    val c1 = x + d
    val c2 = c1 + w
    val c3 = c2 + d
    val c4 = c3 + w

    // Row [number]
    val r1 = y + d
    val r2 = y + d + h

    return floatArrayOf(
        // back
        c4, r1, // top right
        c3, r1, // bottom right
        c3, r2, // bottom left
        c4, r2, // top left

        // front
        c1, r1, // top right
        c2, r1, // bottom right
        c2, r2, // bottom left
        c1, r2, // top left

        // left
        c1, r1, // top right
        x, r1, // bottom right
        x, r2, // bottom left
        c1, r2, // top left

        // right
        c3, r1, // top right
        c2, r1, // bottom right
        c2, r2, // bottom left
        c3, r2, // top left

        // bottom
        c2, y, // top left
        c2 + w, y, // top right
        c2 + w, r1, // bottom left
        c2, r1, // bottom right

        // top
        c1, y, // top left
        c2, y, // top right
        c2, r1, // bottom right
        c1, r1, // bottom left
    )
}