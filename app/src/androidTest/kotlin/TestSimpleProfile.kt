
import net.zatrit.skinbread.skins.*
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
        fun data() = listOf(
            TestPair("Zatrit156", "4476c041-4c72-42cd-a2aa-03696afcdfd8"),
            TestPair("jeb_", "853c80ef-3c37-49fd-aa49-938b674adae6"),
            TestPair("Notch", "069a79f4-44e9-4726-a5be-fca90e38aaf5"),
        )
    }
}