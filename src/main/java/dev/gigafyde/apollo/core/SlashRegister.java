package dev.gigafyde.apollo.core;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlashRegister extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger("SlashRegister");

    public void onReady(@NotNull ReadyEvent event) {
        List<Command> commands = event.getJDA().retrieveCommands().complete();
        List<String> registeredCommands = new ArrayList<>();
        commands.forEach(c -> registeredCommands.add(c.getName()));
        for (CommandData commandData : getCommandList()) {
            if (!registeredCommands.contains(commandData.getName())) {
                event.getJDA().upsertCommand(commandData).queue();
                log.info("Registering command " + commandData.getName());
            }
        }
        event.getJDA().updateCommands().addCommands(getCommandList()).queue();
//        log.info(getCommandList().get(0).getName());
//        event.getJDA().upsertCommand(getCommandList().get(0)).queue();
    }

    private List<CommandData> getCommandList() {
        List<CommandData> commands = new ArrayList<>();

        CommandData ping = Commands.slash("ping", "Shows the latency of the bot");
        CommandData play = Commands.slash("play", "Adds a song to the queue").addOption(OptionType.STRING, "query", "Your search query or URL", true, true);
        CommandData rewind = Commands.slash("rewind", "Rewinds the playing track to the specified time").addOption(OptionType.INTEGER, "amount", "The amount to rewind", true);
        CommandData lyrics = Commands.slash("lyrics", "Shows lyrics of a song").addOption(OptionType.STRING, "query", "Your search query", false);
        CommandData clear = Commands.slash("clear", "Clears the queue");
        CommandData loop = Commands.slash("loop", "Changes loop mode");
        CommandData nowPlaying = Commands.slash("now-playing", "Shows current playing song");
        CommandData pause = Commands.slash("pause", "Pauses the player");
        CommandData volume = Commands.slash("volume", "Changes or displays the volume").addOption(OptionType.INTEGER, "input", "Sets the new volume", false);
        CommandData join = Commands.slash("join", "Makes the bot join your voice channel");
        CommandData leave = Commands.slash("leave", "Makes the bot leave your voice channel");
        CommandData grab = Commands.slash("grab", "Grabs the current playing song");
        CommandData queue = Commands.slash("queue", "Displays the current queue of songs").addOption(OptionType.INTEGER, "page", "Page you wish to see");
        CommandData remove = Commands.slash("remove", "Removes the specified song").addOption(OptionType.INTEGER, "input", "Number of specified track you wish to remove", true);
        CommandData jump = Commands.slash("jump", "Jumps to the specified song in the queue.").addOption(OptionType.INTEGER, "input", "Number of specified track you wish to skip", true);
        CommandData restart = Commands.slash("restart", "Rewinds to start of song");
        CommandData resume = Commands.slash("unpause", "Unpauses the player");
        CommandData seek = Commands.slash("seek", "Seeks the playing track to the specified time").addOption(OptionType.INTEGER, "amount", "The amount to seek", true);
        CommandData shuffle = Commands.slash("shuffle", "Shuffles the queue");
        CommandData skip = Commands.slash("skip", "Skips to the next song in the queue");
        CommandData link = Commands.slash("link", "Grabs the url to the currently playing track");
        CommandData move = Commands.slash("move", "Moves song to new position.").addOption(OptionType.INTEGER, "track", "The Track to move").addOption(OptionType.INTEGER, "position", "The new position for the track.");

//        CommandData addToQueue = Commands.slash(CommandType.MESSAGE_CONTEXT, "Add to Queue");
//        CommandData playlists = Commands.slash("playlists", "Manage your playlists")
//                .addSubcommands(new SubcommandData("save", "Save current queue as a playlist")
//                        .addOption(OptionType.STRING, "name", "Name of playlist", true))
//                .addSubcommands(new SubcommandData("update", "Add current queue as to existing playlist")
//                        .addOption(OptionType.STRING, "name", "Name of playlist", true))
//                .addSubcommands(new SubcommandData("add", "Add current track to an existing playlist")
//                        .addOption(OptionType.STRING, "name", "Name of playlist", true))
//                .addSubcommands(new SubcommandData("load", "Load existing playlist")
//                        .addOption(OptionType.STRING, "name", "Name of playlist", true))
//                .addSubcommands(new SubcommandData("delete", "Delete a playlist")
//                        .addOption(OptionType.STRING, "name", "Name of playlist", true))
//                .addSubcommands(new SubcommandData("share", "Share a playlist")
//                        .addOption(OptionType.STRING, "name", "Name of playlist", true))
//                .addSubcommands(new SubcommandData("list", "List playlists")
//                        .addOption(OptionType.INTEGER, "page", "Page you wish to see"));

        commands.add(ping);
        commands.add(play);
        commands.add(rewind);
        commands.add(lyrics);
        commands.add(clear);
        commands.add(loop);
        commands.add(nowPlaying);
        commands.add(pause);
        commands.add(volume);
        commands.add(join);
        commands.add(leave);
        commands.add(grab);
        commands.add(queue);
        commands.add(remove);
        commands.add(restart);
//        commands.add(addToQueue);
        commands.add(resume);
        commands.add(seek);
        commands.add(shuffle);
        commands.add(skip);
        commands.add(jump);
        commands.add(move);
//        commands.add(playlists);
        commands.add(link);
        return commands;
    }
}
