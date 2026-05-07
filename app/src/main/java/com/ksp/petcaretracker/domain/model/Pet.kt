package com.ksp.petcaretracker.domain.model

data class Pet(
    val id: Long = 0,
    val name: String = "",
    val type: PetType = PetType.DOG,
    val breed: String = "",
    val dateOfBirth: Long = System.currentTimeMillis(),
    val weight: Float = 0f,
    val gender: Gender = Gender.MALE,
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class PetType(val displayName: String, val emoji: String) {
    DOG("Dog", "\uD83D\uDC15"),
    CAT("Cat", "\uD83D\uDC08"),
    RABBIT("Rabbit", "\uD83D\uDC07"),
    BIRD("Bird", "\uD83D\uDC26"),
    PARROT("Parrot", "\uD83E\uDD9C"),
    FISH("Fish", "\uD83D\uDC1F"),
    TROPICAL_FISH("Tropical Fish", "\uD83D\uDC20"),
    HAMSTER("Hamster", "\uD83D\uDC39"),
    GUINEA_PIG("Guinea Pig", "\uD83D\uDC02"),
    MOUSE("Mouse", "\uD83D\uDC2D"),
    FERRET("Ferret", "\uD83E\uDD8A"),
    HEDGEHOG("Hedgehog", "\uD83E\uDD94"),
    TURTLE("Turtle", "\uD83D\uDC22"),
    LIZARD("Lizard", "\uD83E\uDD8E"),
    SNAKE("Snake", "\uD83D\uDC0D"),
    FROG("Frog", "\uD83D\uDC38"),
    HORSE("Horse", "\uD83D\uDC0E"),
    PIG("Pig", "\uD83D\uDC16"),
    CHICKEN("Chicken", "\uD83D\uDC14"),
    DUCK("Duck", "\uD83E\uDD86"),
    OTHER("Other", "\uD83D\uDC3E")
}

enum class Gender(val displayName: String) {
    MALE("Male"),
    FEMALE("Female")
}
