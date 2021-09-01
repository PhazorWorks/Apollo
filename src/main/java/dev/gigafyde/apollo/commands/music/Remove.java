package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Emoji;

public class Remove extends Command {
    public Remove() {
        this.name = "remove";
        this.triggers = new String[]{"remove", "rm"};
    }

    public void execute(CommandEvent event) {
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        try {
            int numberToRemove = Integer.parseInt(event.getArgument());
            if (scheduler.getQueue().size() < numberToRemove) {
                event.getTrigger().reply("").mentionRepliedUser(false).queue();
                return;
            }
            if (numberToRemove <= 0) {
                event.getTrigger().reply("").mentionRepliedUser(false).queue();
                return;
            }
            event.getTrigger().reply(Emoji.SUCCESS + " **Removed** `" + scheduler.getSongTitleByPosition(numberToRemove - 1) + "` **from the queue!**").mentionRepliedUser(false).queue();
            scheduler.removeSong(numberToRemove - 1);
        } catch (NumberFormatException exception) {
            event.getTrigger().reply("Failed to parse number from input").mentionRepliedUser(false).queue();
        }
    }
}
