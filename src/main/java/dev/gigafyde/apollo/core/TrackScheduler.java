package dev.gigafyde.apollo.core;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

public class TrackScheduler extends PlayerEventListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger("TrackScheduler");
    private final LavalinkPlayer player;
    private final AudioPlayerManager audioPlayerManager;
    private boolean looped;
    private TextChannel boundChannel;
    private Message nowPlaying;
    private AudioTrack loopedTrack;
    private AudioTrack previousTrack;
    private Queue<AudioTrack> queue = new LinkedBlockingDeque<>();

    TrackScheduler(LavalinkPlayer player, AudioPlayerManager manager, boolean start) {
        this.player = player;
        this.audioPlayerManager = manager;
        if (start) nextSong();
    }

    public AudioPlayerManager getManager() {
        return audioPlayerManager;
    }

    public LavalinkPlayer getPlayer() {
        return player;
    }

    public TextChannel getBoundChannel() {
        return boundChannel;
    }

    public void setBoundChannel(TextChannel channel) {
        boundChannel = channel;
    }


    public void nextSong() {
        AudioTrack track = queue.poll();

    public AudioTrack getPreviousTrack() {
        return previousTrack;
    }

    public void setPreviousTrack(AudioTrack track) {
        previousTrack = track;
    }

    public AudioPlayerManager getManager() {
        return manager;
    }

    public void nextSong(AudioTrack previousTrack) {
        AudioTrack nextTrack = queue.poll();

        if (looped) {
            track = loopedTrack;
        }
        if (track == null)
            return;

        player.playTrack(track);
        if (player.getPlayingTrack() == track) setPreviousTrack(track);

        if (boundChannel != null) {
            try {
                // Try to delete the previous now-playing message
                nowPlaying.delete().complete();
            } catch (Exception ignored) {
                // If it fails. it'll most likely be because of something on discord's end. so it's not our problem.
            }
            if (player.getPlayingTrack() == track)
                try {
                    boundChannel.sendFile(SongUtils.generateNowPlaying(track, 1), "nowplaying.png").queue(msg -> nowPlaying = msg);
                } catch (Exception e) {
                    boundChannel.sendMessage("**Something went wrong trying to generate the image. " + e + "**").queue();
                    boundChannel.sendMessage(track.getInfo().author + " - " + track.getInfo().title + " - " + SongUtils.getSongProgress(player.getLink().getPlayer())).queue(msg -> nowPlaying = msg);
                }
        }
    }

    public AudioTrack getPreviousTrack() {
        return previousTrack;
    }

    public void setPreviousTrack(AudioTrack track) {
        previousTrack = track;
    }

    public void setLoopedTrack(AudioTrack track) {
        loopedTrack = track;
    }

    public boolean addTrack(AudioTrack track, String author) {
        track.setUserData(author);
        if (queue.contains(track))
            return false;
        queue.add(track);
        if (player.getPlayingTrack() == null)
            nextSong();
        return true;
    }

    public boolean addTopSong(AudioTrack track) {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        tracks.add(0, track);
        queue = new LinkedBlockingDeque<>(tracks);
        return true;
    }

    public boolean isLooped() {
        return looped;
    }

    public void setLooped(boolean active) {
        looped = active;
    }

//    public int addTracks(String author, AudioTrack... tracks) {
//        return addTracks(author, Arrays.asList(tracks));
//    }

    public int addTracks(String author, List<AudioTrack> tracks) {
        int added = 0;
        for (AudioTrack track : tracks) {
            if (addTrack(track, author)) added++;
        }
        return added;
    }

    public void removeTrack(int position) {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        tracks.remove(position);
        queue = new LinkedBlockingDeque<>(tracks);
    }

    public String getTrackTitleByPosition(int position) {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        return tracks.get(position).getInfo().title;
    }

    public void shuffleQueue() {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        Collections.shuffle(tracks);
        queue.clear();
        queue.addAll(tracks);
        queue = new LinkedBlockingDeque<>(tracks);
    }

    public void moveTrack(int oldPosition, int newPostition) {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        AudioTrack oldPos = tracks.remove(oldPosition);
        tracks.add(newPostition, oldPos);
        queue = new LinkedBlockingDeque<>(tracks);
    }


    public void skip() {
        skip(1);
    }

    public void skip(int amount) {
        if (queue.isEmpty()) {
            player.stopTrack();
        }
        if (amount == 1) {
            nextSong();
            return;
        }
        if (queue.size() > amount) {
            for (int i = 0; i < amount; i++) {
                queue.remove();
            }
        } else {
            queue.clear();
            player.stopTrack();
        }

    }

    public void clear() {
        queue.clear();
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }


    @Override
    public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextSong();
        }
    }

    @Override
    public void onTrackException(IPlayer player, AudioTrack track, Exception exception) {
        nextSong();
    }


    @Override
    public void onTrackStuck(IPlayer player, AudioTrack track, long thresholdMs) {
        nextSong();
    }
}
