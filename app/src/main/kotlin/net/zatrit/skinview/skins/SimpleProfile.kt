package net.zatrit.skinview.skins

import net.zatrit.skins.lib.api.Profile
import java.util.UUID

class SimpleProfile(private val id: UUID, private val name: String) : Profile {
    override fun getId() = id
    override fun getName() = name
}