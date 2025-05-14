import java.io.*;
import java.nio.file.*;
import java.util.*;

public class UserManager {
    private static final String USER_FILE = "users.txt";

    public static User signUp(String password) {
        try {
            String id = "UID" + (new Random().nextInt(900000) + 100000);
            User newUser = new User(id, password, 0.0);
            File file = new File(USER_FILE);
            if (!file.exists()) file.createNewFile();
            try (FileWriter fw = new FileWriter(file, true)) {
                fw.write(id + "," + password + ",0.0\n");
            }
            return newUser;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static User login(String id, String password) {
        try {
            File file = new File(USER_FILE);
            if (!file.exists()) return null;
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts[0].equals(id) && parts[1].equals(password)) {
                    double balance = Double.parseDouble(parts[2]);
                    return new User(id, password, balance);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateUser(User user) {
        try {
            File file = new File(USER_FILE);
            List<String> lines = Files.readAllLines(file.toPath());
            try (FileWriter fw = new FileWriter(file, false)) {
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(user.getId())) {
                        fw.write(user.getId() + "," + user.getPassword() + "," + user.getBalance() + "\n");
                    } else {
                        fw.write(line + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}