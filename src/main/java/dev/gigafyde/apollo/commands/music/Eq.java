package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;

public class Eq extends Command {
    public Eq() {
        this.name = "eq";
        this.triggers = new String[]{"eq"};
        this.guildOnly = true;

    }

    public void execute(CommandEvent event) {
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        if (!scheduler.eqActive) {
//            scheduler.setupEq();
//            scheduler.eqStart();
            event.getChannel().sendMessage("Equalizer activated").queue();
        } else event.getChannel().sendMessage("Equalizer already active").queue();
    }
}
