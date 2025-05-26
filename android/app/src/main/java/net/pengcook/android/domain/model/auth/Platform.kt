package net.pengcook.android.domain.model.auth

enum class Platform(val platformName: String) {
    GOOGLE("google"),
    NONE("none"),
    ;

    companion object {
        fun find(platformName: String): Platform {
            return Platform.entries.find { it.platformName == platformName }
                ?: throw IllegalArgumentException()
        }
    }
}
