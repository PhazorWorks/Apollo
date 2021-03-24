package dev.gigafyde.apollo.core.command;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandRegistry {
    private final static Logger LOGGER = LoggerFactory.getLogger("CommandRegistry");
    private final Map<String, Command> commands = new HashMap<>();

    public Set<Command> getCommands() {
        return Set.copyOf(commands.values());
    }

    public void addCommand(Command... commandsToAdd) {
        CommandsLoop:
        for (Command command : commandsToAdd) {
            if (command.name == null) {
                LOGGER.warn(String.format("Command, %s, doesn't have a name", command.getClass().getSimpleName()));
                continue;
            }
            if (command.triggers == null || command.triggers.length == 0) {
                LOGGER.warn(String.format("Command, %s, doesn't have any triggers", command.name));
                continue;
            }
            if (commands.containsValue(command)) {
                LOGGER.warn(String.format("Command, %s, already exists", command.name));
                continue;
            }
            for (String trigger : command.triggers) {
                if (!commands.containsKey(trigger)) commands.put(trigger, command);
                else {
                    LOGGER.warn(String.format("Command, %s, trigger, %s, already exists", command.name, trigger));
                    continue CommandsLoop;
                }
            }
        }
    }

    Command getCommand(String trigger) {
        return commands.getOrDefault(trigger, null);
    }
}
