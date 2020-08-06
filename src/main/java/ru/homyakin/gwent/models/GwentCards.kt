package ru.homyakin.gwent.models

data class GwentCardsStats(
        val type: FactionType,
        val currentCount: Int,
        val totalCount: Int
)

enum class FactionType(
        val engName: String,
        val rusName: String,
        val shortName: String
) {
    GENERAL(
            engName = "Total Cards",
            rusName = "Всего карт",
            shortName = "GWT"
    ),
    MONSTERS(
            engName = "Monsters",
            rusName = "Монстры",
            shortName = "MNS"
    ),
    NILFGAARD(
            engName = "Nilfgaard",
            rusName = "Нильфгаард",
            shortName = "NLF"
    ),
    SCOIATAEL(
            engName = "Scoiatael",
            rusName = "Скоя’таэли",
            shortName = "SCT"
    ),
    SKELLIGE(
            engName = "Skellige",
            rusName = "Скеллиге",
            shortName = "SKL"
    ),
    NORTHERN_REALMS(
            engName = "Northern Realms",
            rusName = "Королевства Севера",
            shortName = "NRL"
    ),
    SYNDICATE(
            engName = "Syndicate",
            rusName = "Синдикат",
            shortName = "SDC"
    ),
    NEUTRAL(
            engName = "Neutral",
            rusName = "Нейтральные",
            shortName = "NTL"
    )
}