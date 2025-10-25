package org.example;

import org.example.commands.MakeCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

@Command(
        name = "eos",
        mixinStandardHelpOptions = true,
        version = "Spring Eos 1.0.0",
        description = "CLI tool for Spring Boot project generation",
        subcommands = {
                HelpCommand.class,
                MakeCommand.class
        }
)
public class Main implements Runnable {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].contains(":")) {
            String[] parts = args[0].split(":", 2);
            if (parts[0].equals("eos")) {
                String[] newArgs = new String[args.length + 1];
                newArgs[0] = "eos";
                newArgs[1] = parts[1];
                System.arraycopy(args, 1, newArgs, 2, args.length - 1);
                args = newArgs;
            }
        }

        System.out.println("Args processados: " + String.join(", ", args));

        int exitCode = new CommandLine(new Main())
                .setExecutionStrategy(new CommandLine.RunLast())
                .execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println("Spring Eos - Use 'eos help' for commands");
    }
}