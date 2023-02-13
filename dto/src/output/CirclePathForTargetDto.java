package output;

import java.util.LinkedList;

public class CirclePathForTargetDto {
    final private LinkedList<String> res;

    public CirclePathForTargetDto(LinkedList<String> r) {
        res = r;
    }

    final public LinkedList<String> getResult() {
        return res;
    }
}
