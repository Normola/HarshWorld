package org.undergroundbunker.harshworld.library;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {

    public static final String ModID = "harshworld";
    public static final String ModName = "Harsh World";

    public static Logger getLogger(String type) {
        String log = ModID;

        return LogManager.getLogger(log + "-" + type);
    }
}
