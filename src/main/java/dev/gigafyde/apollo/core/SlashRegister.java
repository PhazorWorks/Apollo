package dev.gigafyde.apollo.core;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlashRegister extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger("SlashRegister");

//    public void onReady(@NotNull ReadyEvent event) {
//        List<Command> commands = event.getJDA().retrieveCommands().complete();
//        List<String> registeredCommands = new ArrayList<>();
//        commands.forEach(c -> registeredCommands.add(c.getName()));
//        for (CommandData commandData : getCommandList()) {
//            if (!registeredCommands.contains(commandData.getName())) {
//                event.getJDA().upsertCommand(commandData).queue();
//                log.info("Registering command " + commandData.getName());
//            }
//        }
//    }

//    private List<CommandData> getCommandList() {
//        List<CommandData> commands = new ArrayList<>();
//        CommandData ping = new CommandData("ping", "Shows the latency of the bot");
//        CommandData play = new CommandData("play", "Adds a song to the queue").addOption(OptionType.STRING, "query", "Your search query or URL", true);
//        CommandData rewind = new CommandData("rewind", "Rewinds the playing track to the specified time").addOption(OptionType.INTEGER, "amount", "The amount to rewind", true);
//        CommandData lyrics = new CommandData("lyrics", "Shows lyrics of a song").addOption(OptionType.STRING, "query", "Your search query", false);
//        CommandData clear = new CommandData("clear", "Clears the queue");
//        CommandData loop = new CommandData("loop", "Changes loop mode");
//        CommandData nowPlaying = new CommandData("now-playing", "Shows current playing song");
//        CommandData pause = new CommandData("pause", "Pauses the player");
//        CommandData volume = new CommandData("volume", "Changes or displays the volume").addOption(OptionType.INTEGER, "input", "Sets the new volume", false);
//        CommandData join = new CommandData("join", "Makes the bot join your voice channel");
//        CommandData leave = new CommandData("leave", "Makes the bot leave your voice channel");
//        CommandData grab = new CommandData("grab", "Grabs the current playing song");
//        CommandData queue = new CommandData("queue", "Displays the current queue of songs").addOption(OptionType.INTEGER, "page", "Page you wish to see");
//        CommandData remove = new CommandData("remove", "Removes the specified song").addOption(OptionType.INTEGER, "input", "Number of specified track you wish to remove", true);
//        CommandData jump = new CommandData("jump", "Jumps to the specified song in the queue.").addOption(OptionType.INTEGER, "input", "Number of specified track you wish to skip", true);
//        CommandData restart = new CommandData("restart", "Rewinds to start of song");
//        CommandData resume = new CommandData("unpause", "Unpauses the player");
//        CommandData seek = new CommandData("seek", "Seeks the playing track to the specified time").addOption(OptionType.INTEGER, "amount", "The amount to seek", true);
//        CommandData shuffle = new CommandData("shuffle", "Shuffles the queue");
//        CommandData skip = new CommandData("skip", "Skips to the next song in the queue");
//        CommandData link = new CommandData("link", "Grabs the url to the currently playing track");
//        CommandData move = new CommandData("skip", "Moves song to new position.").addOption(OptionType.INTEGER, "track", "The Track to move").addOption(OptionType.INTEGER, "position", "The new position for the track.");
//        CommandData addToQueue = new CommandData(CommandType.MESSAGE_CONTEXT, "Add to Queue");
//        CommandData playlists = new CommandData("playlists", "Manage your playlists")
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
//
//        commands.add(ping);
//        commands.add(play);
//        commands.add(rewind);
//        commands.add(lyrics);
//        commands.add(clear);
//        commands.add(loop);
//        commands.add(nowPlaying);
//        commands.add(pause);
//        commands.add(volume);
//        commands.add(join);
//        commands.add(leave);
//        commands.add(grab);
//        commands.add(queue);
//        commands.add(remove);
//        commands.add(restart);
//        commands.add(addToQueue);
//        commands.add(resume);
//        commands.add(seek);
//        commands.add(shuffle);
//        commands.add(skip);
//        commands.add(jump);
//        commands.add(move);
//        commands.add(playlists);
//        commands.add(link);
//        return commands;
//    }
}
