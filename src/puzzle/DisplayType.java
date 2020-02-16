package puzzle;

public enum DisplayType {
    NUMBERS(0),
    CAPITALS(1),
    LOWERCASE(2);

    int code;

    DisplayType(int code) { this.code = code; }

    int getCode() { return code; }
}
