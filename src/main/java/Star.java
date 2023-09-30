package main.java;
public class Star {

    private final String name;

    private final String birthYear;

    public Star(String name, String birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getName() {
        return name;
    }

    public String toString() {

        return "Name:" + getName() + ", " +
                "Birth Year:" + getBirthYear();
    }
}