package dev.gigafyde.apollo.core.command;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.utils.Emoji;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Command {
    private static final Logger log = LoggerFactory.getLogger("command");
    protected String name = null;
    protected String[] triggers = null;
    protected String description = "No info provided";
    protected boolean hidden = false;
    protected boolean ownerOnly = false;
    protected boolean guildOnly = false;

    protected abstract void execute(CommandEvent event);

    public final void run(CommandEvent event) {
        try {
            if (ownerOnly && !event.getClient().isOwner(event.getAuthor())) return;
            if (guildOnly && !event.isFromGuild()) {
                event.getMessage().getChannel().sendMessage(Emoji.ERROR + " **This command cannot be used in Direct Messages.**").queue();
                return;
            }
            execute(event);
        } catch (Exception e) {
            event.getChannel().sendMessage("**Apologies, something went wrong internally**\n Error encountered was: " + e.getMessage()).queue();
            log.warn("Unexpected exception!", e);
        }
    }
}
