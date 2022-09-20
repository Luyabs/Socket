package console_menu;

import java.util.Scanner;

import static global_param.GlobalParam.DEFAULT_PORT;

public class Menu {
    private final Scanner scanner;
    private int port;
    private SocketServerCopy server;

    public Menu() {
        scanner = new Scanner(System.in);
        port = DEFAULT_PORT;
        server = null;
    }

    private void mainMenu() {
        System.out.println("""
                当前位于: 主菜单
                 -- 按 1 进入服务端控制界面
                 -- 按 2 [推荐]一键运行服务端 + 客户端(通讯 + 发送信息)
                 -- 按 0 退出""");
        switch (scanner.nextInt()) {
            case 1 -> {
                System.out.println("正在进入服务端控制界面");
                serverMenu();
            }
            case 2 -> {
                System.out.println("正在进入一键运行界面");
                clientMenu();
            }
            case 0 -> {
                System.out.println("已退出...");
                return;
            }
            default -> System.out.println("无效输入 请重试");
        }
        System.out.println("-------------------------------------------------------------------------------");
    }

    private void serverMenu() {
        while (true) {
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println("""
                    当前位于: 服务端控制界面
                     -- 按 1 为服务端程序指定端口号
                     -- 按 2 创建并运行服务端程序
                     -- 按 3 查询服务端连接状态
                     -- 按 9 终止服务端程序
                     -- 按 0 回到上一界面(会中断服务端的运行)""");
            switch (scanner.nextInt()) {
                case 1 -> {
                    System.out.print("请为服务端程序指定端口号(当前端口号为" + port + "): ");
                    port = scanner.nextInt();
                }
                case 2 -> {
                    System.out.println("正在运行服务端程序");
                    if (server == null) {
                        server = new SocketServerCopy(port);
                        server.start();
                    }
                    else
                        System.out.println("不能重复创建服务端程序");
                }
                case 3 -> {
                    System.out.println("正在查询服务端连接状态");
                    System.out.println("没做");
                }
                case 9 -> {
                    System.out.println("正在终止服务端程序");   //疑似没bug
                    if (server != null) {
                        server.exit();
                        server = null;
                    }
                    else
                        System.out.println("服务端未被创建");
                }
                case 0 -> {
                    System.out.println("已回到上一界面...");
                    mainMenu();
                }
                default -> System.out.println("无效输入 请重试");
            }
        }
    }

    private void clientMenu() {
        System.out.println("正在一键运行");
        Entrance.main(null);
    }

    public static void main(String[] args) {
        Menu app = new Menu();
        app.mainMenu();
    }
}
