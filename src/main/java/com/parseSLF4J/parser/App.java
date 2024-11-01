package com.parseSLF4J.parser;

import java.io.File;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 3) {
            if (args[0].equals("-a") && args[1].endsWith(".txt") &&
                    args[2].endsWith(".txt")) {
                File plogs = new File(args[1]);
                File hlogs = new File(args[2]);
                if (plogs.exists() && hlogs.exists()) {
                    Parser.main(new String[] { plogs.getName(), hlogs.getName() });
                }
            }
        } else {
            DownloadPapertrailArchive.main(new String[] {});
        }
    }
}
