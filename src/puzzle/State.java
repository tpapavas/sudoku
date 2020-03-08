package puzzle;

import java.io.Serializable;

public enum State implements Serializable {
    NEGATED(0),
    ACCESSIBLE(1);

    int code;

    State(int code) { this.code = code; }

    int getCode() { return code; }
}
