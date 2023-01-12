package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;

public class Shuffle extends Command {

    private CommandEvent event;

    public Shuffle() {
        this.name = "shuffle";
        this.description = "Shuffle's the current queue";
        this.triggers = new String[]{"shuffle"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {

        this.event = event;

        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                shuffle();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                shuffle();
            }
        }
    }

    protected void shuffle() {
        try {
            if (!SongUtils.userConnectedToBotVC(event)) return;
            event.getClient().getMusicManager().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler.shuffleQueue();
            event.send("Shuffled!");
        } catch (Exception e) {
            event.sendError("**Failed to shuffle! error encountered was: " + e.getMessage() + "**");
        }
    }
}
