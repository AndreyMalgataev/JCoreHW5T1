package classes;

public class Employee {
    public long id;
    public String firstName;
    public String lastName;
    public String country;
    public int age;

    public Employee() {
        // Пустой конструктор
    }

    public Employee(long id, String firstName, String lastName, String country, int age) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format("Employee{id=%s, firstName='%s', lastname='%s', country='%s', age=%s}",
                this.id,
                this.firstName,
                this.lastName,
                this.country,
                this.age);
    }
}