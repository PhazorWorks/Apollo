package dev.gigafyde.apollo.commands.secret;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.CommandType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SlashRegister extends Command {
    public SlashRegister() {
        this.name = "slash";
        this.description = "";
        this.triggers = new String[]{"slash"};
        this.ownerOnly = true;
    }

    public void execute(CommandEvent event) {
        JDA jda = event.getJDA();
        // Slash commands
        CommandData ping = new CommandData("ping", "pong");
        CommandData play = new CommandData("play", "Add a song to the queue").addOption(OptionType.STRING, "query", "Your search query or URL", true);
        CommandData rewind = new CommandData("rewind", "Rewinds the specified amount in current song").addOption(OptionType.STRING, "amount", "The amount to rewind", true);
        CommandData lyrics = new CommandData("lyrics", "Shows lyrics of a song").addOption(OptionType.STRING, "query", "Your search query", false);
        CommandData clear = new CommandData("clear", "Clears the queue");
        CommandData loop = new CommandData("loop", "Loop track or queue");
        CommandData nowPlaying = new CommandData("now-playing", "Shows current playing song");
        CommandData pause = new CommandData("pause", "Pauses the player");
        CommandData volume = new CommandData("volume", "Gets or sets a new volume").addOption(OptionType.STRING, "input", "Sets the new volume", false);
        // Message context commands
        CommandData addToQueue = new CommandData(CommandType.MESSAGE_CONTEXT, "Add to Queue");
        // Register slash commands
//        jda.upsertCommand(clear).queue();
//        jda.upsertCommand(play).queue();
//        jda.upsertCommand(ping).queue();
//        jda.upsertCommand(rewind).queue();
//        jda.upsertCommand(loop).queue();
//        jda.upsertCommand(nowPlaying).queue();
//        jda.upsertCommand(pause).queue();
        // Register message context commands
//           jda.upsertCommand(addToQueue).queue();
//        if (!(Main.LYRICS_WEB_SERVER == null) & !(Main.LYRICS_API_KEY == null)) {
        // Only register if those values are defined
//            jda.upsertCommand(lyrics).queue();
//        } else {
//            try {
//                jda.deleteCommandById("lyrics").queue();
//            } catch (Exception ignored) {
        // As per the JDA docs, this will only fail with ErrorResponse.UNKNOWN_COMMAND if there's no such command registered.
        // Which will mean it'll be already unregistered.
    }
}
