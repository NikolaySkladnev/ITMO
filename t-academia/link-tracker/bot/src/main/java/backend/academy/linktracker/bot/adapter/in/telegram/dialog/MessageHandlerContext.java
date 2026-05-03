package backend.academy.linktracker.bot.adapter.in.telegram.dialog;

import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;
import com.pengrad.telegrambot.model.Update;

public record MessageHandlerContext(long chatId, String text, Update update, TrackingDialogState state) {}
