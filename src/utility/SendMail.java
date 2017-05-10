package utility;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

import config.Constants;

import java.util.*;
import utility.Zip;


public class SendMail

{
	public static void send() throws Exception, MessagingException

	{
		// Stop the antivirus and firewall then it will works.
		
		Zip.zipDir(System.getProperty("user.dir") + "\\Report", System.getProperty("user.dir") + "\\email_xlst_reports.zip");
		//Zip.zipDir(System.getProperty("user.dir") + "\\GalenReport\\galen-html-reports", System.getProperty("user.dir") + "\\email_xlst_reports.zip");

		String[] to = {Constants.EmailId_To};
		String host = "smtp.gmail.com";
		String[] cc = {};
		String[] bcc = {};

		// This is for google 587

		SendMail.sendMail(Constants.EmailId_From, Constants.Password, host, "465", "true", "true", true,
				"javax.net.ssl.SSLSocketFactory", "false", to, cc, bcc, "Automation test Reports",
				"Please find the emailable-reports attached.\n\n Regards\nWebMaster",
				System.getProperty("user.dir") + "//email_xlst_reports.zip",

				"email_xlst_reports.zip");

	}

	public static boolean sendMail(String userName, String passWord, String host, String port, String starttls,
			String auth, boolean debug, String socketFactoryClass, String fallback, String[] to, String[] cc,
			String[] bcc, String subject, String text, String attachmentPath, String attachmentName) {

		Properties props = new Properties();

		props.put("mail.smtp.user", userName);

		props.put("mail.smtp.host", host);

		if (!"".equals(port))

			props.put("mail.smtp.port", port);

		if (!"".equals(starttls))

			props.put("mail.smtp.starttls.enable", starttls);
		props.put("mail.smtp.ssl.trust","*");

		props.put("mail.smtp.auth", auth);
		props.put("mail.smtps.auth", "true");

		if (debug) {

			props.put("mail.smtp.debug", "true");

		} else {

			props.put("mail.smtp.debug", "false");

		}

		if (!"".equals(port))

			props.put("mail.smtp.socketFactory.port", port);

		if (!"".equals(socketFactoryClass))

			props.put("mail.smtp.socketFactory.class", socketFactoryClass);

		if (!"".equals(fallback))

			props.put("mail.smtp.socketFactory.fallback", fallback);
		try

		{
			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(debug);
			MimeMessage msg = new MimeMessage(session);
			msg.setText(text);
			msg.setSubject(subject);
			// attachment start
			// create the message part
			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(attachmentPath);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(attachmentName);
			multipart.addBodyPart(messageBodyPart);

			// attachment ends

			// Put parts in message
			msg.setContent(multipart);
			msg.setFrom(new InternetAddress(Constants.EmailId_From));

			for (int i = 0; i < to.length; i++) {

				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));

			}

			for (int i = 0; i < cc.length; i++) {

				msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));

			}

			for (int i = 0; i < bcc.length; i++) {

				msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));

			}

			msg.saveChanges();

			Transport transport = session.getTransport("smtp");

			transport.connect(host, userName, passWord);

			transport.sendMessage(msg, msg.getAllRecipients());

			transport.close();

			return true;

		}

		catch (Exception mex)

		{

			mex.printStackTrace();

			return false;
		}

	}
}