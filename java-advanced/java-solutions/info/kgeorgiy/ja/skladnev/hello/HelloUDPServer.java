//package info.kgeorgiy.ja.skladnev.hello;
//
//import info.kgeorgiy.java.advanced.hello.HelloServer;
//import info.kgeorgiy.java.advanced.hello.HelloServerTest;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.SocketException;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
///**
// * Test interface for {@link HelloServerTest}.
// */
//public class HelloUDPServer implements HelloServer {
//    public static final int WAIT = 10;
//    private DatagramSocket socket;
//    private ExecutorService receiver;
//    private ExecutorService workers;
//
//    /**
//     * Starts a new Hello server.
//     * This method should return immediately.
//     *
//     * @param port    server port.
//     * @param threads number of working threads.
//     */
//    @Override
//    public void start(int port, int threads) {
//        try {
//            socket = new DatagramSocket(port);
//        } catch (SocketException e) {
//            System.err.println("Socket exception: " + e.getMessage());
//        }
//
//        workers = Executors.newFixedThreadPool(threads);
//        receiver = Executors.newSingleThreadExecutor();
//
//        Runnable task = () -> {
//            while (!socket.isClosed()) {
//                try {
//                    byte[] buffer = new byte[socket.getReceiveBufferSize()];
//                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
//
//                    socket.receive(request);
//                    workers.submit(() -> respond(request));
//                } catch (IOException _) {
//                }
//            }
//        };
//
//        receiver.submit(task);
//    }
//
//    private void respond(DatagramPacket request) {
//        String text = new String(Arrays.copyOf(request.getData(), request.getLength()), StandardCharsets.UTF_8);
//        byte[] answer = ("Hello, " + text).getBytes(StandardCharsets.UTF_8);
//        DatagramPacket response = new DatagramPacket(answer, answer.length, request.getSocketAddress());
//
//        try {
//            socket.send(response);
//        } catch (IOException _) {
//        }
//    }
//
//    /**
//     * Stops server and deallocates all resources.
//     */
//    @Override
//    public void close() {
//        if (socket != null && !socket.isClosed()) {
//            socket.close();
//        }
//        if (receiver != null && !receiver.isShutdown()) {
//            receiver.shutdownNow();
//        }
//        if (workers != null && !workers.isShutdown()) {
//            workers.shutdown();
//            try {
//                workers.awaitTermination(WAIT, TimeUnit.SECONDS);
//            } catch (InterruptedException _) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        if (args == null || args.length != 2) {
//            System.err.println("Usage: <port> <threads>");
//            return;
//        }
//        HelloUDPServer server = new HelloUDPServer();
//        server.start(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
//    }
//}
