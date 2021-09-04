package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.utils.SongUtils;

public class Shuffle extends Command {
    public Shuffle() {
        this.name = "shuffle";
        this.description = "Shuffle's the current queue";
        this.triggers = new String[]{"shuffle"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        try {
            event.getClient().getMusicManager().getScheduler(event.getGuild()).shuffleQueue();
            event.getMessage().reply("Shuffled!").mentionRepliedUser(false).queue();
        } catch (Exception e) {
            event.getMessage().reply("Failed to shuffle! error encountered was: " + e.getMessage()).mentionRepliedUser(true).queue();
        }
    }

    @Override
    protected void executeSlash(SlashEvent event) {

    }
}
