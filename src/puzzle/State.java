package puzzle;

public enum State {
    NEGATED(0),
    ACCESSIBLE(1);

    int code;

    State(int code) { this.code = code; }

    int getCode() { return code; }
}
