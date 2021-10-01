package dev.gigafyde.apollo.core.handlers;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import dev.gigafyde.apollo.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.CommandType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public class SlashRegister extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();
        // Slash commands
        CommandData ping = new CommandData("ping", "pong");
        CommandData play = new CommandData("play", "Add a song to the queue").addOption(OptionType.STRING, "query", "Your search query or URL", true);
        CommandData rewind = new CommandData("rewind", "Rewinds the specified amount in current song").addOption(OptionType.STRING, "amount", "The amount to rewind", true);
        CommandData lyrics = new CommandData("lyrics", "Shows lyrics of a song").addOption(OptionType.STRING, "query", "Your search query", true);
        CommandData clear = new CommandData("clear", "Clears the queue");
        // Message context commands
        CommandData addToQueue = new CommandData(CommandType.MESSAGE_CONTEXT, "Add to Queue");
        // Register slash commands
//        jda.upsertCommand(clear).queue();
//        jda.upsertCommand(play).queue();
//        jda.upsertCommand(ping).queue();
//        jda.upsertCommand(rewind).queue();
        // Register message context commands
        if (!(Main.LYRICS_WEB_SERVER == null) & !(Main.LYRICS_API_KEY == null)) {
            // Only register if those values are defined
            jda.upsertCommand(lyrics).queue();
        } else {
            try {
                jda.deleteCommandById("lyrics").queue();
            } catch (Exception ignored) {
                // As per the JDA docs, this will only fail with ErrorResponse.UNKNOWN_COMMAND if there's no such command registered.
                // Which will mean it'll be already unregistered.
            }
        }
    }
}
