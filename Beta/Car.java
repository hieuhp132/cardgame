package Beta;

import java.io.Serializable;

public class Car implements Serializable {
    private static final long serialVersionUID = 1L; // Optional but recommended for version control

    private String name;   // Made private for encapsulation
    private int year;       // Made private for encapsulation
    private boolean activ; // Made private for encapsulation

    // Constructor
    public Car(String name, int year, boolean activ) {
        this.name = name;
        this.year = year;
        this.activ = activ;
    }

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isActive() {
        return activ;
    }

    public void setActive(boolean activ) {
        this.activ = activ;
    }

    // Sample methods for demonstration
    public void move1() {
        System.out.println("1. Moving....");
    }

    public void move2() {
        System.out.println("2. Moving....");
    }

    public void move3() {
        System.out.println("3. Moving....");
    }

    @Override
    public String toString() {
        return "Car{name='" + name + "', old=" + year + ", activ=" + activ + "}";
    }
}
