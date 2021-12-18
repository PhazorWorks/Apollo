package dev.gigafyde.apollo.core;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class TrackScheduler extends PlayerEventListenerAdapter {
    private final LavalinkPlayer player;
    private final AudioPlayerManager manager;
    private boolean looped;
    private TextChannel boundChannel;
    private Message nowPlaying;
    private AudioTrack loopedTrack;
    private Queue<AudioTrack> queue = new LinkedBlockingDeque<>();

    TrackScheduler(LavalinkPlayer player, AudioPlayerManager manager, Guild guild, boolean start) {
        this.player = player;
        this.manager = manager;
        if (start) nextSong(null);
    }

    public TextChannel getBoundChannel() {
        return boundChannel;
    }

    public void setBoundChannel(TextChannel channel) {
        boundChannel = channel;
    }

    public AudioPlayerManager getManager() {
        return manager;
    }

    public void nextSong(AudioTrack previousTrack) {
        AudioTrack nextTrack = queue.poll();
        if (looped) {
            nextTrack = loopedTrack;
        }
        if (nextTrack == null)
            return;
        player.playTrack(nextTrack);
        try {
            // Try to delete the previous now playing message
            nowPlaying.delete().complete();
        } catch (Exception ignored) {
            // Do nothing
        }
        nowPlaying = boundChannel.sendMessage("Now playing " + nextTrack.getInfo().title).complete();
    }

    public void setLoopedSong(AudioTrack track) {
        loopedTrack = track;
    }

    public boolean addSong(AudioTrack track) {
        if (queue.contains(track))
            return false;
        queue.add(track);
        if (player.getPlayingTrack() == null)
            nextSong(null);
        return true;
    }

    public boolean addTopSong(AudioTrack track) {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        tracks.set(0, track);
        queue = new LinkedBlockingDeque<>(tracks);
        return true;
    }

    public void setLooped(boolean active) {
        looped = active;
    }

    public boolean isLooped() {
        return looped;
    }

    public int addSongs(AudioTrack... tracks) {
        return addSongs(Arrays.asList(tracks));
    }

    public int addSongs(List<AudioTrack> tracks) {
        int added = 0;
        for (AudioTrack track : tracks) {
            if (addSong(track)) added++;
        }
        return added;
    }

    public void removeSong(int position) {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        tracks.remove(position);
        queue = new LinkedBlockingDeque<>(tracks);
    }

    public String getSongTitleByPosition(int position) {
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

    public void moveSong(int oldPosition, int newPostition) {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        AudioTrack oldPos = tracks.remove(oldPosition);
        tracks.add(newPostition, oldPos);
        queue = new LinkedBlockingDeque<>(tracks);
    }

    public LavalinkPlayer getPlayer() {
        return player;
    }

    public void skip() {
        skip(1);
    }

    public void skip(int amount) {
        if (queue.isEmpty()) {
            player.stopTrack();
        }
        if (amount == 1) {
            nextSong(null);
            return;
        }
        if (queue.size() > amount) {
            for (int i = 0; i < amount; i++) {
                queue.remove();
            }
        } else {
            queue.clear();
            player.stopTrack();
            return;
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
            nextSong(track);
        }
    }

    @Override
    public void onTrackException(IPlayer player, AudioTrack track, Exception exception) {
        nextSong(track);
    }


    @Override
    public void onTrackStuck(IPlayer player, AudioTrack track, long thresholdMs) {
        nextSong(track);
    }
}
