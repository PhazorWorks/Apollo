package dev.gigafyde.apollo.core;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public class SlashRegister extends ListenerAdapter {

    public void onReady(@NotNull ReadyEvent event) {
        List<Command> commands = event.getJDA().retrieveCommands().complete();
        for (Command command : commands) {
            for (CommandData commandData : getCommandList()) {
                if (!command.getName().equals(commandData.getName()))
                    event.getJDA().upsertCommand(commandData).queue();
            }
        }
    }

    private List<CommandData> getCommandList() {
        List<CommandData> commands = new ArrayList<>();
        CommandData ping = new CommandData("ping", "pong");
        CommandData play = new CommandData("play", "Add a song to the queue").addOption(OptionType.STRING, "query", "Your search query or URL", true);
        CommandData rewind = new CommandData("rewind", "Rewinds the specified amount in current song").addOption(OptionType.STRING, "amount", "The amount to rewind", true);
        CommandData lyrics = new CommandData("lyrics", "Shows lyrics of a song").addOption(OptionType.STRING, "query", "Your search query", false);
        CommandData clear = new CommandData("clear", "Clears the queue");
        CommandData loop = new CommandData("loop", "Loop track or queue");
        CommandData nowPlaying = new CommandData("now-playing", "Shows current playing song");
        CommandData pause = new CommandData("pause", "Pauses the player");
        CommandData volume = new CommandData("volume", "Gets or sets a new volume").addOption(OptionType.STRING, "input", "Sets the new volume", false);
        CommandData addToQueue = new CommandData(CommandType.MESSAGE_CONTEXT, "Add to Queue");
        commands.add(ping);
        commands.add(play);
        commands.add(rewind);
        commands.add(lyrics);
        commands.add(clear);
        commands.add(loop);
        commands.add(nowPlaying);
        commands.add(pause);
        commands.add(volume);
        commands.add(addToQueue);
        return commands;
    }
}
