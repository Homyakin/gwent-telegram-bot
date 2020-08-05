package ru.homyakin.gwent.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.homyakin.gwent.service.actions.Action

@Component
class CommandsExecutor(
        val actions: List<Action>
) {
    private val logger = LoggerFactory.getLogger(CommandsExecutor::class.java)

    @JvmOverloads
    fun processUpdate(messagesSender: AbsSender,
                      update: Update,
                      noActionFoundRunnable: Runnable? = null) : Runnable {
        val message = update.message
                ?: update.editedMessage
                ?: update.callbackQuery.message
        logger.info("Обработка сообщения: ${message.from.userName}")

        val action = selectAction(update)

        return action?.fire(messagesSender, update) ?: noActionFoundRunnable ?: Runnable {
            logger.info("Не нашлось действий на это сообщение!")
        }
    }

    private fun selectAction(update: Update): Action? = actions
            .sortedByDescending { it.priority }
            .find {
                it.canFire(update.message ?: update.editedMessage ?: update.callbackQuery.message)
            }
}