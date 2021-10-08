package dev.gigafyde.apollo;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import dev.gigafyde.apollo.commands.CommandList;
import dev.gigafyde.apollo.core.Client;
import dev.gigafyde.apollo.core.LavalinkManager;
import dev.gigafyde.apollo.core.SlashRegister;
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
    public static String BOT_ID = System.getenv("BOT_ID");
    public static String BOT_PREFIX = System.getenv("BOT_PREFIX");
    public static String OWNER_ID = System.getenv("BOT_OWNER_ID");
    public static String BOT_TOKEN = System.getenv("BOT_TOKEN");
    public static String LAVALINK_URL = System.getenv("LAVALINK_URL");
    public static String LAVALINK_PASS = System.getenv("LAVALINK_PASS");
    public static String SPOTIFY_WEB_SERVER = System.getenv("SPOTIFY_WEB_SERVER");
    public static String LYRICS_WEB_SERVER = System.getenv("LYRICS_WEB_SERVER");
    public static String LYRICS_API_KEY = System.getenv("LYRICS_API_KEY");
    public static Boolean USE_IMAGE_GEN = Boolean.valueOf(System.getenv("USE_IMAGE_GEN"));
    public static int SHARDS_TOTAL = Integer.parseInt(System.getenv("SHARDS_TOTAL"));
    private static final String SENTRY_DSN = System.getenv("SENTRY_DSN");

    public static ShardManager SHARD_MANAGER;
    public static LavalinkManager LAVALINK;
    public static OkHttpClient httpClient = new OkHttpClient();
    private static final Logger log = LoggerFactory.getLogger("Apollo");

    public static void main(String[] args) throws LoginException {
        if (SPOTIFY_WEB_SERVER == null) log.warn("SPOTIFY_WEB_SERVER was not defined, Spotify support will not be available!");
        if (LYRICS_WEB_SERVER == null || LYRICS_API_KEY == null) log.warn("LYRICS_WEB_SERVER/LYRICS_API_KEY was not defined, Lyrics will not be available!");
        if (SENTRY_DSN != null) Sentry.init(SENTRY_DSN);
        LAVALINK = new LavalinkManager();
        Client client = new Client(LAVALINK.getLavalink());
        new CommandList(client);
        SHARD_MANAGER = DefaultShardManagerBuilder.createDefault(BOT_TOKEN)
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                .setVoiceDispatchInterceptor(LAVALINK.getLavalink().getVoiceInterceptor())
                .addEventListeners(client, LAVALINK.getLavalink(), new SlashRegister())
                .setShardsTotal(SHARDS_TOTAL)
                .setShards(SHARDS_TOTAL - 1)
                .build();
    }
}
