package ru.homyakin.gwent.service.actions

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.homyakin.gwent.config.BotConfiguration
import ru.homyakin.gwent.models.CardsDataFactory
import ru.homyakin.gwent.models.Command
import ru.homyakin.gwent.service.HttpService
import java.lang.Exception
import kotlin.random.Random

@Service
class GwentCardsAction(
        private val httpService: HttpService,
        private val botData: BotConfiguration
) : Action {
    private val logger = LoggerFactory.getLogger(GwentCardsAction::class.java)

    override val priority = 1

    private val imWorkingMessages = listOf(
            "Сомневаешься в моих способностях!?",
            "За работу!",
            "Разойдитесь свинопасы, господин Бот работает!",
            "Меня лишили права выбора! Я хочу его вернуть!",
            "Уф-уф-уф, ладно, поехали!"
    )

    override fun fire(absSender: AbsSender, update: Update): Runnable {
        val username = update.getCommandUsernameFromMessage()
        return if (username.isEmpty()) {
            Runnable {
                logger.info("Зачем спрашиваешь за пустого ноунейма?!")
            }
        } else {
            Runnable {
                val randomMessage = imWorkingMessages[Random.nextInt(imWorkingMessages.size)]
                absSender.execute(SendMessage(update.chatId(), randomMessage))
                val body = httpService.getHtmlBodyByUrl("https://www.playgwent.com/en/profile/$username")
                Thread.sleep(1000) // шоб не спамить слишком много
                if (body.isEmpty) {
                    logger.info("The HtmlBody is null!")
                    absSender.execute(SendMessage(update.chatId(), "Что-то непонятное... пустая страничка вернулась!"))
                } else {
                    val htmlDoc = try {
                        Jsoup.parse(body.get())
                    } catch (exception: Exception) {
                        logger.info("There was an error during parsing: ${exception.message}")
                        null
                    }
                    htmlDoc?.let {
                        val nameFromWebsite = it.getElementsByClass("l-player-details__name")[0].text()
                        val cardsData = CardsDataFactory().tryToExtractCardsDataFromDocument(it)
                        val cardsInformationMessage = cardsData.map { cardInfo ->
                            "${cardInfo.type.rusName}: ${cardInfo.currentCount} из ${cardInfo.totalCount}"
                        }
                        val resultInformationMessage = "Я достал данные по пользователю $nameFromWebsite:\n" +
                                cardsInformationMessage.joinToString("\n")
                        absSender.execute(SendMessage(update.chatId(), resultInformationMessage))
                        return@Runnable
                    }
                }
            }
        }
    }

    override fun canFire(message: Message): Boolean {
        logger.info("I'm trying to fire! Is dedicated: ${isCommandDedicatedToThisBot(message.text)}")
        val entities = message.entities
        val resultOfCanFire = entities?.let {
            it.any { entity ->
                entity.type == "bot_command"
                        && entity.text.startsWith(Command.GET_CARDS.value)
                        && isCommandDedicatedToThisBot(entity.text)
            }
        } ?: false
        logger.info("Can I fire? Result: $resultOfCanFire")
        return resultOfCanFire
    }

    private fun Update.getCommandUsernameFromMessage() = when {
        // Не стоит доставать сообщение из того, которое отредактировали
        this.hasMessage() && !this.hasEditedMessage() -> {
            val commandName = Command.GET_CARDS.value
            if (message.text.startsWith(commandName)) {
                Command.GET_CARDS.getTextAfterCommand(message.text)
            } else {
                ""
            }
        }
        else -> ""
    }

    private fun isCommandDedicatedToThisBot(commandText: String) =
            commandText.contains("@").not()
                    .or(commandText.contains("${Command.GET_CARDS.value}@${botData.username}"))

    fun Update.chatId(): Long {
        return when {
            this.hasMessage() -> message.chat.id
            this.hasEditedMessage() -> editedMessage.chat.id
            else -> callbackQuery.message.chat.id
        }
    }
}