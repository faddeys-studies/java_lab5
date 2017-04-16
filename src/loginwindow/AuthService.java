package loginwindow;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javenue.csv.Csv;


public class AuthService {

    private HashMap<String, String> passwordStorage;

    public AuthService() {
        passwordStorage = new HashMap<>();
    }

    public boolean authenticate(String login, String password) {
        password = hashString(password);
        String hashInStorage = passwordStorage.get(login);
        return hashInStorage!=null && hashInStorage.equals(password);
    }

    public boolean register(String login, String password) {
        password = hashString(password);
        String previous = passwordStorage.putIfAbsent(login, password);
        return previous == null;
    }

    public void save(Writer writer) {
        Csv.Writer csvWriter = new Csv.Writer(writer);
        for(Map.Entry<String, String> e : passwordStorage.entrySet()) {
            csvWriter.value(e.getKey()).value(e.getValue()).newLine();
        }
    }

    public void load(Reader reader) {
        Csv.Reader csvReader = new Csv.Reader(reader);
        List<String> line;
        while((line=csvReader.readLine()) != null) {
            if (line.size() != 2) {
                throw new RuntimeException("Malformed credentials file");
            }
            passwordStorage.put(line.get(0), line.get(1));
        }
    }

    private String hashString(String str) {
        String hash = null;
        try {
            byte digest[] = MessageDigest.getInstance("MD5").digest(str.getBytes());
            hash = Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException nsa) {}
        return hash;
    }

}
