package com.fameli.budget.data.model

data class FamilyGroup(
    val id: String = "",
    val name: String = "Моя семья",
    val inviteCode: String = "",
    val members: List<FamilyMember> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class FamilyMember(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val role: FamilyRole = FamilyRole.MEMBER,
    val joinedAt: Long = System.currentTimeMillis()
)

enum class FamilyRole {
    ADMIN,   // Создатель семьи
    MEMBER   // Обычный член
}
