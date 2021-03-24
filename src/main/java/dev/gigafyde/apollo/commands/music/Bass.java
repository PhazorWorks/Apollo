package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import net.dv8tion.jda.api.entities.TextChannel;

public class Bass extends Command {
    public Bass() {
        this.name = "bass";
        this.triggers = new String[]{"bass"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        TextChannel channel = event.getTextChannel();
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
//        switch (event.getArgument().toLowerCase()) {
//            case "low":
//                if (!scheduler.eqActive) {
//                    scheduler.setupEq();
//                    event.getChannel().sendMessage("Equalizer activated").queue();
//                }
//                scheduler.eqStart();
//                scheduler.lowBass(0);
//                channel.sendMessage("Bassboost decreased").queue();
//                return;
//            case "high":
//                if (!scheduler.eqActive) {
//                    scheduler.setupEq();
//                    event.getChannel().sendMessage("Equalizer activated").queue();
//                }
//                scheduler.eqStart();
//                scheduler.highBass(0);
//                channel.sendMessage("Bassboost turned up to 11.").queue();
//                return;
//            case "off":
//                scheduler.disableEq();
//                channel.sendMessage("Bassboost disabled").queue();
//                return;
//            default:
//                channel.sendMessage("Valid values are `low|high|off`").queue();
//        }

    }
}
