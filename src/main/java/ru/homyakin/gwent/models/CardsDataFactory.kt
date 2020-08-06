package ru.homyakin.gwent.models

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class CardsDataFactory {
    private val innerFactionBarTag = "c-faction-bar__bar"
    private val factionBarTagClass = "c-faction-bar c-faction-bar--"
    private val topLevelBarsContentTagClass = "l-faction-bars__content"
    private val cardTypes = mapOf(
            "gwent" to FactionType.GENERAL,
            "monsters" to FactionType.MONSTERS,
            "nilfgaard" to FactionType.NILFGAARD,
            "scoiatael" to FactionType.SCOIATAEL,
            "northernrealms" to FactionType.NORTHERN_REALMS,
            "skellige" to FactionType.SKELLIGE,
            "neutral" to FactionType.NEUTRAL,
            "syndicate" to FactionType.SYNDICATE
    )

    fun tryToExtractCardsDataFromDocument(documentToHandle: Document) =
            documentToHandle.getElementsByClass(topLevelBarsContentTagClass).let {
                when (it.size > 0) {
                    false -> throw FactionParsingException("Failed to parse data, TopLevel contents are empty")
                    true -> {
                        extractDataByFactions(it)
                    }
                }
            }

    private fun extractDataByFactions(parsedTopLevelElements: Elements): List<GwentCardsStats> {
        val results = mutableListOf<GwentCardsStats>()
        for (tag in cardTypes.keys) {
            val factionBarBlock = parsedTopLevelElements.getFirstElementsByClass("$factionBarTagClass$tag")
            if (factionBarBlock.size > 0) {
                val innerFactionBar = factionBarBlock.getFirstElementsByClass(innerFactionBarTag)
                if (innerFactionBar.size > 0) {
                    val actualDataSpan = innerFactionBar[0].getElementsByTag("span")
                    if (actualDataSpan != null && actualDataSpan.size > 0) {
                        val actualDataText = actualDataSpan[0].text()
                        val totalAmountOfCards =
                                actualDataText.substringAfter("/").toIntOrZero()
                        val currentAmountOfCards =
                                actualDataText.substringBefore("/").toIntOrZero()
                        results.add(
                                GwentCardsStats(
                                        type = cardTypes.getOrDefault(tag, FactionType.GENERAL),
                                        currentCount = currentAmountOfCards,
                                        totalCount = totalAmountOfCards
                                )
                        )
                    } else {
                        throw FactionParsingException("The inner faction bar is empty!")
                    }
                }
            } else {
                throw FactionParsingException("Thefaction bar is empty!")
            }
        }
        return results
    }

    data class FactionParsingException(val errorMessage: String) : Throwable()

    private fun String.toIntOrZero() = try {
        this.trim().replace(",", "").replace(".", "").toInt()
    } catch (exception: NumberFormatException) {
        0
    }

    private fun Elements.getFirstElementsByClass(className: String) =
            when (this.size > 0) {
                false -> throw FactionParsingException("Failed to parse data for $className. The root contents are empty!")
                true -> {
                    this[0]?.getElementsByClass(className)
                            ?: throw FactionParsingException("Failed to parse data for $className. The inner contents are null")
                }
            }
}