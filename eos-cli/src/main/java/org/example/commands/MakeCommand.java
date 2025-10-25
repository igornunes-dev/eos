package org.example.commands;

import org.example.commands.make.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

@Command(
        name = "eos",
        description = "Generate Spring Boot components",
        subcommands = {
                HelpCommand.class,
                MakeControllerCommand.class,
                MakeServiceCommand.class
        }
)
public class MakeCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Use 'eos:controller <name>' to generate components");
        System.out.println("\nAvailable types:");
        System.out.println("  controller  - Generate a REST controller");
        System.out.println("  service     - Generate a service class");
    }
}