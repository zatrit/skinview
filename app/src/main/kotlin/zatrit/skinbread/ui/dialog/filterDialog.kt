package zatrit.skinbread.ui.dialog

import zatrit.skinbread.*
import zatrit.skinbread.skins.defaultSources
import zatrit.skinbread.ui.TexturesActivity

fun filterDialog(context: TexturesActivity) = dialogBuilder(context).apply {
    val names = Array(defaultSources.size - VANILLA) { defaultSources[it + VANILLA].name.getName(context) }
    setMultiChoiceItems(names, allowedSources) { _, which, checked ->
        allowedSources[which] = checked
    }

    setPositiveButton(android.R.string.ok) { _, _ ->
        context.preferences.edit {
            it.putInt(ENABLED_SOURCES, allowedSources.toInt())
        }
    }
}.create().applyDialogTheme()