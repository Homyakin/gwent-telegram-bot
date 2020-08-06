package ru.homyakin.gwent.models

enum class Command(val value: String) {
    GET_PROFILE("/get_profile"),
    GET_CARDS("/get_cards");

    fun getTextAfterCommand(fullText: String) = fullText.substringAfter(this.value).trim()
}
