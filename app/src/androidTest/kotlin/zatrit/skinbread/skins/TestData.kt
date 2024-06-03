package zatrit.skinbread.skins

data class TestTriplet(val name: String, val uuid: String, val json: String)

val testProfiles = listOf(
  TestTriplet(
    "Zatrit156", "4476c041-4c72-42cd-a2aa-03696afcdfd8",
    "{\"name\": \"Zatrit156\", \"id\": \"4476c041-4c72-42cd-a2aa-03696afcdfd8\"}"
  ),
  TestTriplet(
    "jeb_", "853c80ef-3c37-49fd-aa49-938b674adae6",
    "{\n\"name\": \"jeb_\",\n\"id\": \"853c80ef-3c37-49fd-aa49-938b674adae6\"\n}"
  ),
  TestTriplet(
    "Notch", "069a79f4-44e9-4726-a5be-fca90e38aaf5",
    "{\n    \"name\": \"Notch\",\n                    \"id\": \"069a79f4-44e9-4726-a5be-fca90e38aaf5\"\n}"
  ),
)