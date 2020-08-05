package ru.homyakin.gwent.service.actions

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

interface Action {

    val priority: Int

    fun fire(absSender: AbsSender, update: Update): Runnable

    fun canFire(message: Message): Boolean
}