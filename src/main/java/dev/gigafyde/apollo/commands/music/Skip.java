package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.core.command.messageCommandEvent;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Message;

public class Skip extends Command {
    public Skip() {
        this.name = "skip";
        this.triggers = new String[]{"skip", "s"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        Message message = event.getMessage();
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
//        if (scheduler == null) {
//            event.getMessage().reply("Nothing is currently playing! Queue some tracks first").queue();
//            return;
//        }
        if (event.getArgument().isEmpty()) {
            scheduler.skip();
        } else {
            int amount = Integer.parseInt(event.getArgument());
            scheduler.skip(amount);
        }
        if (scheduler.isLooped()) {
            scheduler.setLooped(false);
            event.getMessage().reply("Loop was turned off due to manual skip").mentionRepliedUser(true).queue();
        }
        message.addReaction(Emoji.SUCCESS.toString()).queue();
    }

    protected void executeSlash(SlashEvent event) {

    }

    protected void executeContext(messageCommandEvent event) {

    }
}
