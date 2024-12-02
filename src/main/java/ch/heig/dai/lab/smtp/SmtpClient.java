package ch.heig.dai.lab.smtp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class SmtpClient {
    private final String serverAddress;
    private final int serverPort;
    private static final String END_OF_LINE = "\r\n";

    /**
     * Create a new SMTP client
     * @param serverAddress the address of the SMTP server
     * @param serverPort the port of the SMTP server
     */
    public SmtpClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /**
     * read a response from the server
     * @return the response from the server
     * @throws IOException
     */
    private String readResponse(BufferedReader reader) throws IOException {
        String response = reader.readLine();
        System.out.println("Server: " + response);
        return response;
    }

    /**
     * Send a command to the server
     * @param command the command to send
     * @throws IOException if an I/O error occurs
     */
    private void sendCommand(String command, BufferedReader reader, PrintWriter writer) throws IOException {
        System.out.println("Client: " + command);
        writer.write(command + END_OF_LINE);
        writer.flush();
        String response = readResponse(reader);

        if (!response.startsWith("2") && !response.startsWith("3")) {
            throw new IOException("SMTP error: " + response);
        }
    }

    /**
     * Send an email
     * @param from the sender of the email
     * @param to the recipient of the email
     * @param subject the subject of the email
     * @param body the body of the email
     * @throws IOException if an I/O error occurs
     */
    public void sendEmail(String from, List<String> to, String subject, String body,
                          BufferedReader reader, PrintWriter writer) throws IOException {
        // Read greeting
        readResponse(reader);

        // HELO command
        sendCommand("HELO localhost", reader, writer);

        // MAIL FROM command
        sendCommand("MAIL FROM:<" + from + ">", reader, writer);

        // RCPT TO command
        for (String recipient : to) {
            sendCommand("RCPT TO:<" + recipient + ">", reader, writer);
        }

        // DATA command
        sendCommand("DATA", reader, writer);
        String data = "Subject: " + subject + END_OF_LINE + body + END_OF_LINE + END_OF_LINE + ".";
        sendCommand(data, reader, writer);

        // QUIT command
        sendCommand("QUIT", reader, writer);

    }

    /**
     * Send a prank
     * @param prank the prank to send
     */
    public void sendPrank(Email prank) {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            sendEmail(prank.getSender(), prank.getRecipients(), prank.getSubject(),
                prank.getBody(), in, out);
            // Close the socket
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
