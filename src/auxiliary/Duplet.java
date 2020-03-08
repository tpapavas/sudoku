package auxiliary;

import java.io.Serializable;

public class Duplet implements Serializable {
    int index;
    GameType type;

    public Duplet(int index, GameType type) {
        this.index = index;
        this.type = type;
    }

    public int hashCode() {
        int hashCode = 17;
        hashCode += 31*hashCode + index;
        hashCode += 31*hashCode + (type == GameType.CLASSIC_SUDOKU ? 5 : 24);
        return hashCode;
    }

    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;
        Duplet duplet = (Duplet) obj;
        return index == duplet.index && type == duplet.type;
    }
}
