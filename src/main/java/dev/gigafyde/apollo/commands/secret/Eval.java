package dev.gigafyde.apollo.commands.secret;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.Haste;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class Eval extends Command {
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Eval Pool");
    private static final Executor POOL = Executors.newCachedThreadPool(r -> new Thread(THREAD_GROUP, r, THREAD_GROUP.getName() + " " + THREAD_GROUP.activeCount()));
    private static final Logger log = LoggerFactory.getLogger(Eval.class);
    private static final String imports;

    static {
        imports = """
                import net.dv8tion.jda.core.*
                import net.dv8tion.jda.core.entities.*
                """;
        THREAD_GROUP.setMaxPriority(Thread.MIN_PRIORITY);
    }

    public Eval() {
        this.name = "Eval";
        this.description = "Evaluate and run the inputted code";
        this.triggers = new String[]{"eval"};
        this.hidden = true;
        this.ownerOnly = true;
        this.guildOnly = false;
    }

    protected void execute(CommandEvent event) {
        MessageChannel channel = event.getTextChannel();
        if (event.getArgument().isEmpty()) {
            try {
                event.getMessage().addReaction(Emoji.ERROR.toString()).queue();
            } catch (Exception e) {
                event.getMessage().reply(Emoji.ERROR + " **Please enter something to evaluate!**").queue();
            }
            return;
        }
        String arg = imports + event.getArgument();
        GroovyShell shell = createShell(event);
        POOL.execute(() -> {
            try {
                Object result = shell.evaluate(arg);
                if (result == null) {
                    try {
                        event.getMessage().addReaction(Emoji.SUCCESS.toString()).queue();
                    } catch (Exception e) {
                        channel.sendMessage(Emoji.SUCCESS + " **Executed successfully**").queue();
                    }
                    return;
                }
                String resultString = result.toString();
                if (resultString.length() > 1900) {
                    String url = Haste.paste(resultString);
                    if (url == null) url = "Failed to generate error report!";
                    channel.sendMessage(Emoji.SUCCESS + " **Executed successfully:** " + url).queue();
                } else {
                    event.getMessage().addReaction(Emoji.SUCCESS.toString()).queue();
                    channel.sendMessage("```\n" + resultString + "```").queue();
                }
            } catch (Throwable t) {
                channel.sendMessage(Emoji.ERROR + " **Failed to execute ** -- " + Haste.paste(t)).queue();
            }
        });
    }

    private GroovyShell createShell(CommandEvent event) {
        Binding binding = new Binding();
        binding.setVariable("api", event.getJDA());
        binding.setVariable("jda", event.getJDA());
        binding.setVariable("channel", event.getChannel());
        binding.setVariable("author", event.getAuthor());
        binding.setVariable("member", event.getMember());
        binding.setVariable("message", event.getMessage());
        binding.setVariable("msg", event.getMessage());
        binding.setVariable("guild", event.getGuild());
        binding.setVariable("server", event.getGuild());
        binding.setVariable("event", event);
        binding.setVariable("selfMember", event.getSelfMember());
        binding.setVariable("selfUser", event.getSelfUser());
        binding.setVariable("client", event.getClient());
        return new GroovyShell(binding);
    }
}
