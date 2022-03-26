package less7.server.authentication;

import less7.server.models.User;

import java.util.List;

public class BaseAuthentication implements AuthenticationService {

    private static final List<User> clients = List.of(
            new User("user1", "1111", "Mike"),
            new User("user2", "2222", "Merry"),
            new User("user3", "3333", "Tommy"),
            new User("user4", "4444", "Jane")
    );
    @Override
    public synchronized String getUsernameByLoginAndPassword(String login, String password) {
        for (User client : clients) {
            if (client.getLogin().equals(login) && client.getPassword().equals(password)) {
                return client.getUsername();
            }
        }
        return null;
    }

    @Override
    public void startAuthentication() {
        System.out.println(">>>Start of the authentication");
    }

    @Override
    public void endAuthentication() {
        System.out.println(">>>End of the authentication");
    }
}
