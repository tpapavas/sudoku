package puzzle;

public class Representation {
    private DisplayType type;
    private char[] format;
    private int size;

    public Representation(int length, DisplayType type) {
        this.size = length + 2;
        this.type = type;
        this.format = new char[size];
        setTable();
    }

    private void setTable(){
        int offset = 0;
        switch (type) {
            case NUMBERS:
                offset = '\u0030'; //One before '1';
                break;

            case CAPITALS:
                offset = '\u0040'; //One before 'A';
                break;

            case LOWERCASE:
                offset = '\u0060'; //One before 'a';
                break;
        }

        format[0] = ' ';
        for(int i = 1; i < size-1; i++)
            format[i] = (char) (i+offset);
        format[size-1] = '-';
    }

    public void changeType(DisplayType dType) {
        type = dType;
        setTable();
    }

    public char[] getFormat() { return format; }
    public DisplayType getType() { return type; }
}
