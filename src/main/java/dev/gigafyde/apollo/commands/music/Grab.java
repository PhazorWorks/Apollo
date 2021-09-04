package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.User;

public class Grab extends Command {
    public Grab() {
        this.name = "grab";
        this.description = "send the currently playing song to your dm's";
        this.triggers = new String[]{"grab"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        if (scheduler.getPlayer().getPlayingTrack() != null) {
            String uri = scheduler.getPlayer().getPlayingTrack().getInfo().uri;
            User author = event.getAuthor();
            try {
                author.openPrivateChannel().complete().sendMessage("Here is a copy of the currently playing track\n" + uri).complete();
                event.getMessage().addReaction(Emoji.SUCCESS.toString()).queue();
            } catch (Exception e) {
                event.getMessage().reply("Hi there, I tried to send the link to you privately, but it seems that failed, so I'm sending it here instead.\n" + uri).mentionRepliedUser(true).queue();
            }
        } else {
            event.getMessage().reply("Nothing is currently playing, so there was nothing to grab.").mentionRepliedUser(true).queue();
        }
    }

    @Override
    protected void executeSlash(SlashEvent event) {

    }
}
