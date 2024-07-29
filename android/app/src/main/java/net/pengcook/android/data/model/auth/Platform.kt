package net.pengcook.android.data.model.auth

enum class Platform(val platformName: String?) {
    GOOGLE("google"),
    NONE(null),
    ;

    companion object {
        fun find(platformName: String): Platform? {
            return Platform.entries.find { it.platformName == platformName }
        }
    }
}
