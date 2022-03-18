package less7.server.authentication;

import less7.server.models.User;

import java.util.List;

public class BaseAuthentication implements AuthenticationService {

    private static final List<User> clients = List.of(
            new User("martin", "1111", "Martin_Cat"),
            new User("batman", "2222", "Брюс_Уэйн"),
            new User("gena", "3333", "Гендальф_Серый")
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
