package dev.gigafyde.apollo.utils;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.core.command.CommandEvent;
import lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

        switch (event.getCommandType()) {
            case REGULAR -> {
                VoiceChannel vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
                if (vc == null) {
                    event.getMessage().reply("**You have to join a voice channel before you can use this command!**").mentionRepliedUser(true).queue();
                    return false;
                }
                EnumSet<Permission> voicePermissions = event.getSelfMember().getPermissions(vc);
                if (!voicePermissions.contains(Permission.VIEW_CHANNEL)) {
                    event.getMessage().reply("**I do not have permission to `view` or `connect` to your voice channel!**").mentionRepliedUser(true).queue();
                    return false;
                }
                if (!voicePermissions.contains(Permission.VOICE_SPEAK)) {
                    event.getMessage().reply("**I do not have permission to `speak` in your voice channel!**").mentionRepliedUser(true).queue();
                    return false;
                }
                return true;
            }
            case SLASH -> {
                VoiceChannel vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
                InteractionHook hook = event.getHook();
                if (vc == null) {
                    hook.editOriginal("**You have to join a voice channel before you can use this command!**").queue();
                    return false;
                }
                EnumSet<Permission> voicePermissions = event.getGuild().getSelfMember().getPermissions(vc);
                if (!voicePermissions.contains(Permission.VIEW_CHANNEL)) {
                    hook.editOriginal("**I do not have permission to `view` or `connect` to your voice channel!**").queue();
                    return false;
                }
                if (!voicePermissions.contains(Permission.VOICE_SPEAK)) {
                    hook.editOriginal("**I do not have permission to `speak` in your voice channel!**").queue();
                    return false;
                }
                return true;
            }
            case CONTEXT -> {
                VoiceChannel vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
                if (vc == null) {
                    event.reply("**You have to join a voice channel before you can use this command!**").setEphemeral(true).queue();
                    return false;
                }
                EnumSet<Permission> voicePermissions = event.getGuild().getSelfMember().getPermissions(vc);
                if (!voicePermissions.contains(Permission.VIEW_CHANNEL)) {
                    event.reply("**I do not have permission to `view` or `connect` to your voice channel!**").setEphemeral(true).queue();
                    return false;
                }
                if (!voicePermissions.contains(Permission.VOICE_SPEAK)) {
                    event.reply("**I do not have permission to `speak` in your voice channel!**").setEphemeral(true).queue();
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    public static String getStrippedSongUrl(String url) {
        return url.replace("<", "").replace(">", "");
    }

    public static String calculateSongLength(AudioTrack track) {
        if (track.getInfo().isStream) return "Livestream";
        long minutes = TimeUnit.MILLISECONDS.toMinutes(track.getDuration());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(track.getDuration()) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public static String getSongProgress(LavalinkPlayer player) {
        long dms = player.getPlayingTrack().getDuration();
        long pms = player.getTrackPosition();
        long pmin = TimeUnit.MILLISECONDS.toMinutes(pms);
        long psec = TimeUnit.MILLISECONDS.toSeconds(pms) % 60;
        String duration = calculateSongLength(player.getPlayingTrack());
        return String.format("%d:%02d/%s", pmin, psec, duration);
    }

    public static InputStream generateAndSendImage(AudioTrack track, String author) {
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject().put("title", track.getInfo().title).put("author", author).put("duration", track.getDuration()).put("uri", track.getInfo().uri).put("identifier", track.getInfo().identifier);
            RequestBody body = RequestBody.create(String.valueOf(jsonObject), JSON); // new
            Response response = Main.httpClient.newCall(
                    new Request.Builder()
                            .url(Main.IMAGE_API_SERVER + "add")
                            .post(body)
                            .build()).execute();
            return Objects.requireNonNull(response.body()).byteStream();
        } catch (Exception ignored) {
        }
        return null;
    }

    public static boolean userConnectedToBotVC(CommandEvent event) {
        VoiceChannel vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
        VoiceChannel selfVC = Objects.requireNonNull(event.getSelfMember().getVoiceState()).getChannel();
        if (vc != selfVC) event.sendError(Constants.userNotInVC);
        return vc == selfVC;
    }

    public static boolean botAloneInVC(CommandEvent event) {
        VoiceChannel selfVC = Objects.requireNonNull(event.getSelfMember().getVoiceState()).getChannel();
        VoiceChannel vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
        if (vc == selfVC) return true;
        List<Member> members = selfVC.getMembers().stream().filter(member -> !member.getUser().isBot()).filter(member -> !member.getId().equals(event.getMember().getId())).collect(Collectors.toList());
        if (members.size() >= 1) {
            event.sendError(Constants.usedElsewhere);
            return false;
        }
        return true;
    }

    /**
     * Returns a list with all links contained in the input
     */
    public static String extractUrl(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|http?):((\\/\\/)|(\\\\))+[\\w\\d:#@%\\/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }
        if (containedUrls.isEmpty()) return text;
        return containedUrls.get(0);
    }

}
