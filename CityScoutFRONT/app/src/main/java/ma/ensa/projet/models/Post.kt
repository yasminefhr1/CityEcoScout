package ma.ensa.projet.models

data class Post(
    val id: Long? = null,
    val content: String,
    val type: PostType,
    val imageBytes: ByteArray?,
    val utilisateur: User,
    val createdAt: String? = null,
    val likes: MutableList<User> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
