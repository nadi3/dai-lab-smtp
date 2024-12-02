package ch.heig.dai.lab.smtp;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String SMTP_SERVER = "localhost";   // This is where MailDev is running
        int SMTP_PORT = 1025;               // MailDev's SMTP port

        SmtpClient client = new SmtpClient(SMTP_SERVER, SMTP_PORT);
        List<Email> emailList = PrankGenerator.generatePranks(1); //TODO read argument
        for (Email email : emailList) {
            client.sendPrank(email);
        }
    }
}
