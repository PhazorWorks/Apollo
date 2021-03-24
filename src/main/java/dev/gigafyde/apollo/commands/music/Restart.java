package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Emoji;
import net.dv8tion.jda.api.entities.Message;

public class Restart extends Command {
    public Restart() {
        this.name = "restart";
        this.triggers = new String[]{"restart"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        Message message = event.getTrigger();
        event.getClient().getMusicManager().getScheduler(event.getGuild()).getPlayer().getPlayingTrack().setPosition(0);
        message.addReaction(Emoji.SUCCESS.toString()).queue();
    }
}
