package org.undergroundbunker.harshworld.library.materials;

import java.util.List;

public interface IMaterialStats {

    String getIdentifier();
    String getLocalisedName();
    List<String> getLocalisedInfo();
    List<String> getLocalisedDesc();
}
