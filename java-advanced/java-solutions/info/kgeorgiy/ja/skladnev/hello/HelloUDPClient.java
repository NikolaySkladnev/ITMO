//package info.kgeorgiy.ja.skladnev.hello;
//
//import info.kgeorgiy.java.advanced.hello.HelloServer;
//import info.kgeorgiy.java.advanced.hello.NewHelloClient;
//
//import java.io.IOException;
//import java.net.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import static info.kgeorgiy.ja.skladnev.hello.UdpUtils.*;
//
///**
// * Multi-server interface for {@link HelloServer}.
// */
//public class HelloUDPClient implements NewHelloClient {
//
//    private static final int TIMEOUT = 500;
//    public static final int WAIT = 10;
//
//    /**
//     * Runs Hello client.
//     * This method should return when all requests are completed.
//     *
//     * @param requests requests to perform in each thread.
//     * @param threads  number of request threads.
//     */
//    @Override
//    public void newRun(final List<Request> requests, final int threads) {
//        List<SingleRequest> result = new ArrayList<>();
//        for (Request r : requests) {
//            result.add(new SingleRequest(new InetSocketAddress(r.host(), r.port()), r.template()));
//        }
//        runTasks(threads, result);
//    }
//
//    /**
//     * Runs Hello client.
//     * This method should return when all requests are completed.
//     *
//     * @param host     server host.
//     * @param port     server port.
//     * @param prefix   request prefix.
//     * @param requests number of requests per thread.
//     * @param threads  number of request threads.
//     */
//    @Override
//    public void run(final String host, final int port, final String prefix, final int requests, final int threads) {
//        InetSocketAddress address = new InetSocketAddress(host, port);
//        List<SingleRequest> result = new ArrayList<>();
//        for (int i = 1; i <= requests; i++) {
//            result.add(new SingleRequest(address, prefix + i + "_$"));
//        }
//        runTasks(threads, result);
//    }
//
//    private void runTasks(int threads, List<SingleRequest> requests) {
//        ExecutorService workers = Executors.newFixedThreadPool(threads);
//        for (int i = 1; i <= threads; i++) {
//            final int threadId = i;
//            workers.submit(() -> processRequests(threadId, requests));
//        }
//        close(workers);
//    }
//
//    private void processRequests(int threadId, List<SingleRequest> requests) {
//        try (DatagramSocket socket = new DatagramSocket()) {
//            socket.setSoTimeout(TIMEOUT);
//            byte[] buffer = new byte[socket.getReceiveBufferSize()];
//            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
//            for (SingleRequest request : requests) {
//                sendRequest(socket, response, request, threadId);
//            }
//        } catch (SocketException e) {
//            System.err.println("Socket exception: " + e.getMessage());
//        }
//    }
//
//    private void sendRequest(DatagramSocket socket, DatagramPacket response, SingleRequest request, int threadId) {
//        try {
//            String requestText = getText(request, threadId);
//            byte[] reqBytes = bytes(requestText);
//            DatagramPacket requestPacket = new DatagramPacket(reqBytes, reqBytes.length, request.server());
//            socket.setSoTimeout(TIMEOUT);
//            while (true) {
//                try {
//                    socket.send(requestPacket);
//                    socket.receive(response);
//                    if (printResponse(response, requestText)) break;
//                } catch (SocketTimeoutException ignored) {
//                } catch (IOException e) {
//                    System.err.println("IO error: " + e.getMessage());
//                }
//            }
//        } catch (SocketException e) {
//            System.err.println("Socket error: " + e.getMessage());
//        }
//    }
//
//    private static boolean printResponse(DatagramPacket response, String requestText) {
//        String answer = string(response.getData(), response.getOffset(), response.getLength());
//        if (validResponse(requestText, answer)) {
//            System.out.println(requestText);
//            System.out.println(answer);
//            return true;
//        }
//        return false;
//    }
//
//    private static String getText(SingleRequest request, int threadId) {
//        return request.template().replace("$", String.valueOf(threadId));
//    }
//
//    private static void close(ExecutorService workers) {
//        workers.shutdown();
//        try {
//            workers.awaitTermination(WAIT, TimeUnit.SECONDS);
//        } catch (InterruptedException ignored) {
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    public static void main(final String[] args) {
//        if (args == null || args.length != 5) {
//            System.err.println("Usage: <host> <port> <prefix> <requests> <threads>");
//            return;
//        }
//
//        try {
//            new HelloUDPClient().run(
//                    args[0],
//                    Integer.parseInt(args[1]),
//                    args[2],
//                    Integer.parseInt(args[3]),
//                    Integer.parseInt(args[4]));
//        } catch (NumberFormatException e) {
//            System.err.println("Invalid number format: " + e.getMessage());
//        }
//    }
//
//    private record SingleRequest(InetSocketAddress server, String template) {
//    }
//}
