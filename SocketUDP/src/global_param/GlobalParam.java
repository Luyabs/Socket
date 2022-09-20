package global_param;

public class GlobalParam {
    public static int WAITING_TIME = 10000; //服务端没有连接时最大等待时间
    public static int MAX_LOGIN_TIMES = 3;  //客户端单次连接时最大登录失败次数
    public static int DEFAULT_PORT = 6789;  //默认端口号
    public static int MAX_ROW = 25;       //最大行数
    public static String EXIT_STR = "______exit______";       //最大行数

    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
}
