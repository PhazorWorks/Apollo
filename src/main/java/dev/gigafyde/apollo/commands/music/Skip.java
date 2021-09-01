package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.TrackScheduler;
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
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        if (event.getArgument().isEmpty()) {
            scheduler.skip();
        } else {
            int amount = Integer.parseInt(event.getArgument());
            scheduler.skip(amount);
        }
        if (scheduler.isLooped()) {
            scheduler.setLooped(false);
            event.getTrigger().reply("Loop was turned off due to manual skip").queue();
        }
        message.addReaction(Emoji.SUCCESS.toString()).queue();
    }
}
