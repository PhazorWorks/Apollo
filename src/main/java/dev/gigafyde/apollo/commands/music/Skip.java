package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Emoji;
import net.dv8tion.jda.api.entities.Message;

public class Skip extends Command {
    public Skip() {
        this.name = "skip";
        this.triggers = new String[]{"skip", "s"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        Message message = event.getTrigger();
        event.getClient().getMusicManager().getScheduler(event.getGuild()).skip();
        message.addReaction(Emoji.SUCCESS.toString()).queue();
    }
}
