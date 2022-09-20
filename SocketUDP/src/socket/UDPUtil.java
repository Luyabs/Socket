package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPUtil {
    public static void UDPSend(DatagramSocket UDPSocket, String message, int destPort) {
        byte[] buffer = message.getBytes();
        try {
            UDPSocket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), destPort));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String UDPReceive(DatagramSocket UDPSocket) {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            UDPSocket.receive(packet);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(buffer, 0, packet.getLength());
    }
}
