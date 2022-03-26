package less7.server.models;

public class Command {
    public static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    public static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    public static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    public static final String USERADD_CMD_PREFIX = "/add"; // + username
    public static final String USERREM_CMD_PREFIX = "/remove"; // + username
    public static final String USERLIST_CMD_PREFIX = "/userlist"; // + userList from Server
    public static final String CLIENT_MSG_CMD_PREFIX = "/clientMsg"; // + msg
    public static final String SERVER_MSG_CMD_PREFIX = "/serverMsg"; // + msg
    public static final String PRIVATE_MSG_CMD_PREFIX = "/privateMsg"; // + msg
    public static final String STOP_SERVER_CMD_PREFIX = "/stop";
    public static final String END_CLIENT_CMD_PREFIX = "/end";
}
