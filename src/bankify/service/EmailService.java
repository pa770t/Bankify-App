package bankify.service;

import bankify.Agent;
import bankify.Customer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {

    private static final String SENDER_EMAIL = "bankifyroot@gmail.com";
    private static final String SENDER_PASSWORD = "nrrl rigm uavb spav";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String imagePath = "src/Resources/bankifylogo.jpg";

    private static void sendAsync(String recipient, String subject, String htmlContent) {
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL, "Bankify Team"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject(subject);

                // Allow HTML content
                message.setContent(htmlContent, "text/html; charset=utf-8");

                Transport.send(message);
                System.out.println("Email sent successfully to: " + recipient);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }).start();
    }

    public void sendWelcomeEmail(Customer customer) {
        String encodedImage = "";
        String toEmail = customer.getEmail();
        String subject = "Registration Successful! Welcome to Bankify!";

        // 1. Read and Encode the Image
        try {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                byte[] fileContent = Files.readAllBytes(Paths.get(imagePath));
                encodedImage = Base64.getEncoder().encodeToString(fileContent);
                System.out.println("Image processed successfully from: " + imagePath);
            } else {
                System.out.println("Warning: Image file not found at " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("Error reading image: " + e.getMessage());
            return;
        }

        // 2. Construct the HTML Content
        // - Text color set to #000000 (Black)
        String htmlTemplate =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "<title>Welcome to Bankify</title>\n" +
                        "<style>\n" +
                        "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #ffffff; margin: 0; padding: 0; }\n" +
                        "  .email-wrapper { width: 100%; background-color: #ffffff; padding: 20px 0; }\n" +
                        "  .email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #eeeeee; border-radius: 8px; overflow: hidden; }\n" +
                        "  .header { background-color: #ffffff; padding: 30px; text-align: center; }\n" +
                        "  .header img { max-width: 150px; height: auto; }\n" +
                        "  .content { padding: 40px; line-height: 1.6; text-align: center; background-color: #ffffff; }\n" +
                        "  \n" +
                        "  /* HEADLINE: Black */\n" +
                        "  .content h1 { color: #000000; margin-bottom: 20px; font-size: 28px; font-weight: 600; }\n" +
                        "  \n" +
                        "  /* TEXT: Black (#000000) per request */\n" +
                        "  .content p { font-size: 16px; margin-bottom: 30px; color: #000000; font-weight: 400; }\n" +
                        "  \n" +
                        "  .footer { background-color: #ffffff; padding: 20px; text-align: center; font-size: 12px; color: #666666; border-top: 1px solid #f0f0f0; }\n" +
                        "  .footer a { color: #000000; text-decoration: underline; }\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  <div class=\"email-wrapper\">\n" +
                        "    <div class=\"email-container\">\n" +
                        "      <div class=\"header\">\n" +
                        "        <img src=\"data:image/jpeg;base64," + encodedImage + "\" alt=\"Bankify Logo\">\n" +
                        "      </div>\n" +
                        "      <div class=\"content\">\n" +
                        "        <h1>Welcome " + customer.getFirstName() + " " + customer.getLastName() + "!🚀" +
                        "</h1>\n" +
                        "        <p>Hi there,</p>\n" +
                        "        <p>We are thrilled to let you know that your <strong>Bankify</strong> account registration was successful!</p>\n" +
                        "        <p>Get ready to experience banking like never before. Secure, fast, and tailored just for you.</p>\n" +
                        "        <br>\n" +
                        "        <p style=\"font-size: 14px; margin-top: 20px; color: #000000;\">If you have any questions, feel free to reply to this email.</p>\n" +
                        "      </div>\n" +
                        "      <div class=\"footer\">\n" +
                        "        &copy; 2026 Bankify Inc. All rights reserved.<br>\n" +
                        "        <a href=\"#\">Privacy Policy</a> | <a href=\"#\">Terms of Service</a>\n" +
                        "      </div>\n" +
                        "    </div>\n" +
                        "  </div>\n" +
                        "</body>\n" +
                        "</html>";

        // 3. Send the email
        sendAsync(toEmail, subject, htmlTemplate);
    }

    public void sendCustomerCreatedEmail(Customer customer, String tempPassword) {
        String encodedImage = "";
        String toEmail = customer.getEmail();
        String subject = "Action Required: Complete Your Bankify Account Setup";

        // 1. Read and Encode the Image
        try {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                byte[] fileContent = Files.readAllBytes(Paths.get(imagePath));
                encodedImage = Base64.getEncoder().encodeToString(fileContent);
                System.out.println("Image processed successfully from: " + imagePath);
            } else {
                System.out.println("Warning: Image file not found at " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("Error reading image: " + e.getMessage());
            return;
        }

        // 2. Construct the HTML Content
        String htmlTemplate =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "<title>Welcome to Bankify</title>\n" +
                        "<style>\n" +
                        "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #ffffff; margin: 0; padding: 0; }\n" +
                        "  .email-wrapper { width: 100%; background-color: #ffffff; padding: 20px 0; }\n" +
                        "  .email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #eeeeee; border-radius: 8px; overflow: hidden; }\n" +
                        "  .header { background-color: #ffffff; padding: 30px; text-align: center; }\n" +
                        "  .header img { max-width: 150px; height: auto; }\n" +
                        "  .content { padding: 40px; line-height: 1.6; text-align: center; background-color: #ffffff; }\n" +
                        "  .content h1 { color: #000000; margin-bottom: 20px; font-size: 28px; font-weight: 600; }\n" +
                        "  .content p { font-size: 16px; margin-bottom: 30px; color: #000000; font-weight: 400; }\n" +
                        "  .footer { background-color: #ffffff; padding: 20px; text-align: center; font-size: 12px; color: #666666; border-top: 1px solid #f0f0f0; }\n" +
                        "  .footer a { color: #000000; text-decoration: underline; }\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  <div class=\"email-wrapper\">\n" +
                        "    <div class=\"email-container\">\n" +
                        "      <div class=\"header\">\n" +
                        "        <img src=\"data:image/jpeg;base64," + encodedImage + "\" alt=\"Bankify Logo\">\n" +
                        "      </div>\n" +
                        "      <div class=\"content\">\n" +
                        "        <h1>Welcome " + customer.getFirstName() + " " + customer.getLastName() + "!🚀</h1>\n" +
                        "        <p>To complete your customer account creation, Please login with your email & " +
                        "temporary password (<strong>" + tempPassword + "</strong>).</p>\n" +
                        "        <p>After login, please fill in the profile and you can use Bankify services.</p>\n" +
                        "<p><strong>Please change your password immediately in Settings -> Change " +
                        "Password</p></strong>" +
                        "        <p>Enjoy 🚀!</p>\n" +
                        "      </div>\n" +
                        "      <div class=\"footer\">\n" +
                        "        &copy; 2026 Bankify Inc. All rights reserved.<br>\n" +
                        "        <a href=\"#\">Privacy Policy</a> | <a href=\"#\">Terms of Service</a>\n" +
                        "      </div>\n" +
                        "    </div>\n" +
                        "  </div>\n" +
                        "</body>\n" +
                        "</html>";

        // 3. Send the email
        sendAsync(toEmail, subject, htmlTemplate);
    }

    public void sendAgentCreatedEmail(Agent agent, String tempPassword) {
        String encodedImage = "";
        String toEmail = agent.getEmail();
        String subject = "Action Required: Complete Your Bankify Account Setup";

        // 1. Read and Encode the Image
        try {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                byte[] fileContent = Files.readAllBytes(Paths.get(imagePath));
                encodedImage = Base64.getEncoder().encodeToString(fileContent);
                System.out.println("Image processed successfully from: " + imagePath);
            } else {
                System.out.println("Warning: Image file not found at " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("Error reading image: " + e.getMessage());
            return;
        }

        // 2. Construct the HTML Content
        String htmlTemplate =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "<title>Welcome to Bankify</title>\n" +
                        "<style>\n" +
                        "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #ffffff; margin: 0; padding: 0; }\n" +
                        "  .email-wrapper { width: 100%; background-color: #ffffff; padding: 20px 0; }\n" +
                        "  .email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #eeeeee; border-radius: 8px; overflow: hidden; }\n" +
                        "  .header { background-color: #ffffff; padding: 30px; text-align: center; }\n" +
                        "  .header img { max-width: 150px; height: auto; }\n" +
                        "  .content { padding: 40px; line-height: 1.6; text-align: center; background-color: #ffffff; }\n" +
                        "  .content h1 { color: #000000; margin-bottom: 20px; font-size: 28px; font-weight: 600; }\n" +
                        "  .content p { font-size: 16px; margin-bottom: 30px; color: #000000; font-weight: 400; }\n" +
                        "  .footer { background-color: #ffffff; padding: 20px; text-align: center; font-size: 12px; color: #666666; border-top: 1px solid #f0f0f0; }\n" +
                        "  .footer a { color: #000000; text-decoration: underline; }\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  <div class=\"email-wrapper\">\n" +
                        "    <div class=\"email-container\">\n" +
                        "      <div class=\"header\">\n" +
                        "        <img src=\"data:image/jpeg;base64," + encodedImage + "\" alt=\"Bankify Logo\">\n" +
                        "      </div>\n" +
                        "      <div class=\"content\">\n" +
                        "        <h1>Welcome " + agent.getFullName() + "!🚀</h1>\n" +
                        "        <p>To complete your agent account creation, Please login with your email & temporary" +
                        " password (<strong>" + tempPassword + "</strong>).</p>\n" +
                        "        <p>After login, please fill in the profile and you can use Bankify services.</p>\n" +
                        "<p><strong>Please change your password immediately in Change Password</strong></p>" +
                        "        <p>Enjoy 🚀!</p>\n" +
                        "      </div>\n" +
                        "      <div class=\"footer\">\n" +
                        "        &copy; 2026 Bankify Inc. All rights reserved.<br>\n" +
                        "        <a href=\"#\">Privacy Policy</a> | <a href=\"#\">Terms of Service</a>\n" +
                        "      </div>\n" +
                        "    </div>\n" +
                        "  </div>\n" +
                        "</body>\n" +
                        "</html>";

        // 3. Send the email
        sendAsync(toEmail, subject, htmlTemplate);
    }

    public void sendPasswordChangeSuccessEmail(Customer customer) {
        String encodedImage = "";
        String subject = "Security Alert: Password Changed Successfully";

        // 1. Read and Encode the Image
        try {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                byte[] fileContent = Files.readAllBytes(Paths.get(imagePath));
                encodedImage = Base64.getEncoder().encodeToString(fileContent);
            } else {
                System.out.println("Warning: Image file not found at " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("Error reading image: " + e.getMessage());
            return;
        }

        // 2. Construct the HTML Content
        String htmlTemplate =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "<title>Password Changed</title>\n" +
                        "<style>\n" +
                        "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #ffffff; margin: 0; padding: 0; }\n" +
                        "  .email-wrapper { width: 100%; background-color: #ffffff; padding: 20px 0; }\n" +
                        "  .email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #eeeeee; border-radius: 8px; overflow: hidden; }\n" +
                        "  .header { background-color: #ffffff; padding: 30px; text-align: center; }\n" +
                        "  .header img { max-width: 150px; height: auto; }\n" +
                        "  .content { padding: 40px; line-height: 1.6; text-align: center; background-color: #ffffff; }\n" +
                        "  \n" +
                        "  /* HEADLINE: Black */\n" +
                        "  .content h1 { color: #000000; margin-bottom: 20px; font-size: 24px; font-weight: 600; }\n" +
                        "  \n" +
                        "  /* TEXT: Black (#000000) */\n" +
                        "  .content p { font-size: 16px; margin-bottom: 20px; color: #000000; font-weight: 400; }\n" +
                        "  \n" +
                        "  /* WARNING TEXT: Slightly bold for emphasis */\n" +
                        "  .warning-text { font-weight: 600; color: #000000; }\n" +
                        "  \n" +
                        "  .footer { background-color: #ffffff; padding: 20px; text-align: center; font-size: 12px; color: #666666; border-top: 1px solid #f0f0f0; }\n" +
                        "  .footer a { color: #000000; text-decoration: underline; }\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  <div class=\"email-wrapper\">\n" +
                        "    <div class=\"email-container\">\n" +
                        "      <div class=\"header\">\n" +
                        "        <img src=\"data:image/jpeg;base64," + encodedImage + "\" alt=\"Bankify Logo\">\n" +
                        "      </div>\n" +
                        "      <div class=\"content\">\n" +
                        "        <h1>Password Changed Successfully</h1>\n" +
                        "        <p>Dear " + customer.getFirstName() + " " + customer.getLastName() + ",</p>\n" +
                        "        <p>The password for your Bankify account has been updated.</p>\n" +
                        "        <p>If you made this change, you can safely ignore this email.</p>\n" +
                        "        <br>\n" +
                        "        <p class=\"warning-text\">If you did NOT make this change, please contact our support team immediately.</p>\n" +
                        "      </div>\n" +
                        "      <div class=\"footer\">\n" +
                        "        &copy; 2026 Bankify Inc. All rights reserved.<br>\n" +
                        "        <a href=\"#\">Contact Support</a>\n" +
                        "      </div>\n" +
                        "    </div>\n" +
                        "  </div>\n" +
                        "</body>\n" +
                        "</html>";

        // 3. Send the email
        sendAsync(customer.getEmail(), subject, htmlTemplate);
    }

    public void sendAgentPasswordChangeSuccessEmail(Agent agent) {
        String encodedImage = "";
        String subject = "Security Alert: Password Changed Successfully";

        // 1. Read and Encode the Image
        try {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                byte[] fileContent = Files.readAllBytes(Paths.get(imagePath));
                encodedImage = Base64.getEncoder().encodeToString(fileContent);
            } else {
                System.out.println("Warning: Image file not found at " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("Error reading image: " + e.getMessage());
            return;
        }

        // 2. Construct the HTML Content
        String htmlTemplate =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "<title>Password Changed</title>\n" +
                        "<style>\n" +
                        "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #ffffff; margin: 0; padding: 0; }\n" +
                        "  .email-wrapper { width: 100%; background-color: #ffffff; padding: 20px 0; }\n" +
                        "  .email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #eeeeee; border-radius: 8px; overflow: hidden; }\n" +
                        "  .header { background-color: #ffffff; padding: 30px; text-align: center; }\n" +
                        "  .header img { max-width: 150px; height: auto; }\n" +
                        "  .content { padding: 40px; line-height: 1.6; text-align: center; background-color: #ffffff; }\n" +
                        "  \n" +
                        "  /* HEADLINE: Black */\n" +
                        "  .content h1 { color: #000000; margin-bottom: 20px; font-size: 24px; font-weight: 600; }\n" +
                        "  \n" +
                        "  /* TEXT: Black (#000000) */\n" +
                        "  .content p { font-size: 16px; margin-bottom: 20px; color: #000000; font-weight: 400; }\n" +
                        "  \n" +
                        "  /* WARNING TEXT: Slightly bold for emphasis */\n" +
                        "  .warning-text { font-weight: 600; color: #000000; }\n" +
                        "  \n" +
                        "  .footer { background-color: #ffffff; padding: 20px; text-align: center; font-size: 12px; color: #666666; border-top: 1px solid #f0f0f0; }\n" +
                        "  .footer a { color: #000000; text-decoration: underline; }\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  <div class=\"email-wrapper\">\n" +
                        "    <div class=\"email-container\">\n" +
                        "      <div class=\"header\">\n" +
                        "        <img src=\"data:image/jpeg;base64," + encodedImage + "\" alt=\"Bankify Logo\">\n" +
                        "      </div>\n" +
                        "      <div class=\"content\">\n" +
                        "        <h1>Password Changed Successfully</h1>\n" +
                        "        <p>Dear " + agent.getFullName() + ",</p>\n" +
                        "        <p>The password for your Bankify account has been updated.</p>\n" +
                        "        <p>If you made this change, you can safely ignore this email.</p>\n" +
                        "        <br>\n" +
                        "        <p class=\"warning-text\">If you did NOT make this change, please contact our support team immediately.</p>\n" +
                        "      </div>\n" +
                        "      <div class=\"footer\">\n" +
                        "        &copy; 2026 Bankify Inc. All rights reserved.<br>\n" +
                        "        <a href=\"#\">Contact Support</a>\n" +
                        "      </div>\n" +
                        "    </div>\n" +
                        "  </div>\n" +
                        "</body>\n" +
                        "</html>";

        // 3. Send the email
        sendAsync(customer.getEmail(), subject, htmlTemplate);
    }
}
