import java.io.*;
import java.util.*;

public class User {
    private String id;
    private String password;
    private double balance;

    public User(String id, String password, double balance) {
        this.id = id;
        this.password = password;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}