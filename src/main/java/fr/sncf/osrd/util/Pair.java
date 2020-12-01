package fr.sncf.osrd.util;

import java.util.Objects;

public final class Pair<U, V>  {
    public final U first;
    public final V second;

    public Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (this.getClass() != obj.getClass())
            return false;

        var o = (Pair) obj;
        return first.equals(o.first) && second.equals(o.second);
    }
}
