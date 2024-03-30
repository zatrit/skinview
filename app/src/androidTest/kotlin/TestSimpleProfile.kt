
import net.zatrit.skinview.skins.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.UUID

data class TestPair(val name: String, val uuid: String)

@RunWith(value = Parameterized::class)
class TestSimpleProfile(pair: TestPair) {
    private val testName = pair.name
    private val testUUID = UUID.fromString(pair.uuid)

    @Test
    fun testProfileByName() {
        val profile = profileByName(testName)
        assertEquals(profile.shortId, testUUID.toString().replace("-", ""))
        assertEquals(profile.id, testUUID)
    }

    @Test
    fun testProfileByUUID() {
        val profile = profileByUUID(testUUID)
        assertEquals(profile.name, testName)
    }

    @Test
    fun testProfileByNameNoUUID() {
        val profile = profileByName(testName, requiresUuid = false)
        assertEquals(profile.id, nullUUID)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            TestPair("Zatrit156", "4476c041-4c72-42cd-a2aa-03696afcdfd8"),
            TestPair("jeb_", "853c80ef-3c37-49fd-aa49-938b674adae6"),
            TestPair("Notch", "069a79f4-44e9-4726-a5be-fca90e38aaf5"),
        )
    }
}