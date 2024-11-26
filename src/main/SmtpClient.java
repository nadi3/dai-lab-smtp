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
    private String readResponse() throws IOException {
        String response = reader.readLine();
        System.out.println("Server: " + response);
        return response;
    }

    /**
     * Send a command to the server
     * @param command the command to send
     * @throws IOException if an I/O error occurs
     */
    private void sendCommand(String command) throws IOException {
        System.out.println("Client: " + command);
        writer.write(command + END_OF_LINE);
        writer.flush();
        String response = readResponse();

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
    public void sendEmail(String from, String to, String subject, String body) throws IOException {
        // Read greeting
        readResponse();

        // HELO command
        sendCommand("HELO localhost");

        // MAIL FROM command
        sendCommand("MAIL FROM:<" + from + ">");

        // RCPT TO command
        sendCommand("RCPT TO:<" + to + ">");

        // DATA command
        sendCommand("DATA");
        writer.write("Subject: " + subject + END_OF_LINE);
        writer.write(body + END_OF_LINE);
        writer.flush();
        writer.write("." + END_OF_LINE);
        writer.flush();

        // QUIT command
        sendCommand("QUIT");

        // Close the socket
        socket.close();
    }

    /**
     * Send a prank
     * @param prank the prank to send
     * @throws IOException if an I/O error occurs
     */
    public void sendPrank(Prank prank) throws IOException {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            client.sendEmail(prank.getSender(), prank.getRecipients(), prank.getMessage().getSubject(), prank.getMessage().getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
