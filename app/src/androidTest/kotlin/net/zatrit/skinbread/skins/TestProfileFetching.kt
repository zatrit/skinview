package net.zatrit.skinbread.skins

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.UUID

@RunWith(value = Parameterized::class)
class TestProfileFetching(triplet: TestTriplet) {
    private val testName = triplet.name
    private val testUUID = UUID.fromString(triplet.uuid)

    @Test
    fun testProfileByName() {
        val uuid = uuidByName(testName)
        assertEquals(uuid, testUUID)
    }

    @Test
    fun testProfileByUUID() {
        val name = nameByUuid(testUUID)
        assertEquals(name, testName)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = testProfiles
    }
}