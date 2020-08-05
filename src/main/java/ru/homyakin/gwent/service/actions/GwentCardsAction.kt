package ru.homyakin.gwent.service.actions

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.homyakin.gwent.config.BotConfiguration
import ru.homyakin.gwent.service.HttpService
import java.lang.Exception

@Service
class GwentCardsAction(
        val httpService: HttpService,
        val botData: BotConfiguration
) : Action {
    val logger = LoggerFactory.getLogger(GwentProfileAction::class.java)

    private val commandName = "get_cards"

    override val priority = 1

    override fun fire(absSender: AbsSender, update: Update): Runnable {
        val username = update.getCommandUsernameFromMessage()
        return if (username.isEmpty()) {
            Runnable {
                logger.info("Зачем спрашиваешь за пустого ноунейма?!")
            }
        } else {
            Runnable {
                val body = httpService.getHtmlBodyByUrl("https://www.playgwent.com/en/profile/$username")
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
                        absSender.execute(SendMessage(update.chatId(), "Я достал имя пользователя! $nameFromWebsite"))
                        return@Runnable
                    }
                }
                return@Runnable
            }
        }
    }

    override fun canFire(message: Message): Boolean {
        logger.info("I'm trying to fire! Is dedicated: ${isCommandDedicatedToThisBot(message.text)}")
        val entities = message.entities
        val resultOfCanFire = entities?.let {
            it.any { entity ->
                entity.type == "bot_command"
                        && isCommandDedicatedToThisBot(entity.text)
            }
        } ?: false
        logger.info("Can I fire? Result: $resultOfCanFire")
        return resultOfCanFire
    }

    private fun Update.getCommandUsernameFromMessage() = when {
        // Не стоит доставать сообщение из того, которое отредактировали
        this.hasMessage() && !this.hasEditedMessage() -> {
            if(message.text.startsWith("/$commandName")) {
               message.text.substringAfter(commandName).trim()
            } else {
                ""
            }
        }
        else -> ""
    }

    private fun isCommandDedicatedToThisBot(commandText: String) =
            commandText.contains("@").not()
                    .or(commandText.contains("$commandName@${botData.username}"))

    fun Update.chatId(): Long {
        return when {
            this.hasMessage() -> message.chat.id
            this.hasEditedMessage() -> editedMessage.chat.id
            else -> callbackQuery.message.chat.id
        }
    }
}