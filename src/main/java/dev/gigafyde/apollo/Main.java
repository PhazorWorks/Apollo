package dev.gigafyde.apollo;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import dev.gigafyde.apollo.commands.CommandList;
import dev.gigafyde.apollo.core.Client;
import dev.gigafyde.apollo.core.GuildMusicManager;
import dev.gigafyde.apollo.core.SlashRegister;
import dev.gigafyde.apollo.utils.SongUtils;
import io.sentry.Sentry;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    // Load configuration values from system environment variables
    public static final String BOT_ID = System.getenv("BOT_ID");
    public static final String BOT_PREFIX = System.getenv("BOT_PREFIX");
    public static final String OWNER_ID = System.getenv("BOT_OWNER_ID");
    public static final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    public static final String LYRICS_API_KEY = System.getenv("LYRICS_API_KEY");
    public static final String IMAGE_API_SERVER = System.getenv("IMAGE_API_SERVER");
    public static final String PLAYLISTS_API_KEY = System.getenv("PLAYLISTS_API_KEY");
    public static final Boolean USE_IMAGE_API = Boolean.valueOf(System.getenv("USE_IMAGE_API"));
    public static final int SHARDS_TOTAL = Integer.parseInt(System.getenv("SHARDS_TOTAL"));
    public static final OkHttpClient httpClient = new OkHttpClient();
    private static final String SENTRY_DSN = System.getenv("SENTRY_DSN");
    private static final Logger log = LoggerFactory.getLogger("Apollo");
    public static String SPOTIFY_WEB_SERVER = System.getenv("SPOTIFY_WEB_SERVER");
    public static String SPOTIFY_CLIENT_ID = System.getenv("SPOTIFY_CLIENT_ID");
    public static String SPOTIFY_CLIENT_SECRET = System.getenv("SPOTIFY_CLIENT_SECRET");
    public static String DEEZER_KEY = System.getenv("DEEZER_KEY");
    public static String LYRICS_WEB_SERVER = System.getenv("LYRICS_WEB_SERVER");
    public static String PLAYLISTS_WEB_SERVER = System.getenv("PLAYLISTS_WEB_SERVER");
    public static ShardManager SHARD_MANAGER;

    public static void main(String[] args) throws LoginException {
        if (!SongUtils.isValidURL(SPOTIFY_WEB_SERVER) || SPOTIFY_WEB_SERVER == null) {
            log.warn("SPOTIFY_WEB_SERVER was not set, Spotify support will not be available!");
            SPOTIFY_WEB_SERVER = null;
        }
        if (!SongUtils.isValidURL(LYRICS_WEB_SERVER) || LYRICS_WEB_SERVER == null) {
            log.warn("LYRICS_WEB_SERVER was not set, Lyrics will not be available!");
            LYRICS_WEB_SERVER = null;
        }
        if (!SongUtils.isValidURL(PLAYLISTS_WEB_SERVER) || PLAYLISTS_WEB_SERVER == null) {
            log.warn("PLAYLISTS_WEB_SERVER was not set, Playlists will not be available!");
            PLAYLISTS_WEB_SERVER = null;
        }
        if (SENTRY_DSN != null) Sentry.init(SENTRY_DSN);
        Client client = new Client();
        new CommandList(client);
        SHARD_MANAGER = DefaultShardManagerBuilder.createDefault(BOT_TOKEN)
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_PRESENCES)
                .addEventListeners(client, new SlashRegister())
                .setShardsTotal(SHARDS_TOTAL)
                .setShards(SHARDS_TOTAL - 1)
                .build();
    }
}
