package dev.gigafyde.apollo.utils;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.MusicManager;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.CommandEvent;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class SongUtils {
    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
        return true;
    }

    public static boolean passedVoiceChannelChecks(CommandEvent event) {
        MusicManager musicManager = event.getClient().getMusicManager();
        TrackScheduler scheduler = musicManager.getScheduler(event.getGuild());
        VoiceChannel vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
        if (scheduler == null) {
            if (vc == null) {
                event.getTrigger().reply("**Please join a voice channel first!**").mentionRepliedUser(true).queue();
                return true;
            }
            EnumSet<Permission> voicePermissions = event.getSelfMember().getPermissions(vc);
            if (voicePermissions.contains(Permission.VOICE_CONNECT)) {
                if (!voicePermissions.contains(Permission.VOICE_SPEAK)) {
                    event.getTrigger().reply("**I am unable to speak in this voice channel!**").mentionRepliedUser(true).queue();
                    return true;
                }
            } else {
                event.getTrigger().reply("**I am unable to connect to this voice channel**").mentionRepliedUser(true).queue();
                return true;
            }
        }
        if (vc == null) {
            event.getTrigger().reply("**Please join a voice channel first!**").queue();
            return true;
        }
        return false;
    }

    public static String calculateSongLength(AudioTrack track) {
        if (track.getInfo().isStream) return "Livestream";
        long minutes = TimeUnit.MILLISECONDS.toMinutes(track.getDuration());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(track.getDuration()) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public static String getSongProgress(AudioTrack track) {
        long dms = track.getDuration();
        long pms = track.getPosition();
        long pmin = TimeUnit.MILLISECONDS.toMinutes(pms);
        long psec = TimeUnit.MILLISECONDS.toSeconds(pms) % 60;
        String duration = calculateSongLength(track);
        return String.format("%d:%02d/%s", pmin, psec, duration);
    }
}
