package model;

import java.time.LocalDate;

// [REQ17] Model class representing one customer row.
public class Customer {
    private int customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private LocalDate birthDate;
    private LocalDate joinDate;

    // [REQ17] Default constructor used by DAO or menu code when needed.
    public Customer() {}

    // [REQ17] Full constructor maps customer table columns to Java fields.
    public Customer(int customerId, String firstName, String lastName,
                    String email, String password, String phone, LocalDate birthDate, LocalDate joinDate) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthDate = birthDate;
        this.joinDate = joinDate;
    }

    // [REQ17] Standard getters and setters expose customer fields to service/menu code.
    public int getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
