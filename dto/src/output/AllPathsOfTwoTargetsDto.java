package output;

import java.util.List;

public class AllPathsOfTwoTargetsDto {
    public AllPathsOfTwoTargetsDto(final List<List<String>> allThePaths){allPaths= allThePaths;}
    final private List<List<String>> allPaths;
    final public List<List<String>> getAllPaths(){return allPaths;}
}

