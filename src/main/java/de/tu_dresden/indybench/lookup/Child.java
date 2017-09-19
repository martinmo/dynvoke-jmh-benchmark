package de.tu_dresden.indybench.lookup;

public class Child extends Parent {
    public String childOnly() {
        return "Child";
    }

    @Override
    public String overridden() {
        return "Child";
    }
}
