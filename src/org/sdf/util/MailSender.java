/*
 * Created on 2005. 2. 14.
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sdf.util;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.sdf.tool.MailTool;

/**
 * @author 박성호
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class MailSender {
	String smtpServer;

	public MailSender(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public boolean send(String from_name, String from_mail, String to_name,
			String to_mail, String title, String content) {
		String mailfrom = get(from_name) + "<" + from_mail + ">";
		String mailto = get(to_name) + "<" + to_mail + ">";
		return send(mailfrom, mailto, title, content);
	}

	public boolean send(String mailfrom, String mailto, String title,
			String content) {
		if (mailto == null || mailto.trim().equals(""))
			return false;
		Properties msgProperties = new Properties();

		msgProperties.put("mail.smtp.host", smtpServer);
		System.out.println("SMTP : " + smtpServer);
		Session msgSession = Session.getDefaultInstance(msgProperties, null);

		try {
			MimeMessage msg = new MimeMessage(msgSession);

			InternetAddress from = new InternetAddress(mailfrom);
			msg.setFrom(from);

			InternetAddress to = new InternetAddress(mailto);
			msg.setRecipient(Message.RecipientType.TO, to);

			msg.setSubject(title, "EUC_KR");

			msg.setContent(content, "text/html; charset=EUC-KR");
			msg.setHeader("Content-type", "text/html; charset=EUC-KR");

			Transport.send(msg);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean send(String from_name, String from_mail, String[] to_name,
			String[] to_mail, String title, String content) {
		if (to_name == null || to_name.length == 0)
			return false;

		for (int i = 0; i < to_name.length; i++) {
			send(from_name, from_mail, to_name[i], to_mail[i], title, content);
		}
		return true;
	}

	public boolean send(String from_mail, String[] to_mail, String title,
			String content) {
		if (to_mail == null || to_mail.length == 0)
			return false;

		for (int i = 0; i < to_mail.length; i++) {

			send(from_mail, to_mail[i], title, content);
		}

		return true;
	}

	boolean valid(String email) {
		if (email == null || email.trim().length() == 0
				|| email.indexOf('@') == -1)
			return false;
		return true;
	}

	String get(String s) {
		try {
			return new String(s.getBytes("euc-kr"), "8859_1");
		} catch (Exception e) {
		}
		return s;
	}

	public String getSmtpServer() {
		return smtpServer;
	}

	public static void main(String[] args) {

		MailSender m = new MailSender("smtp.gmail.com");
		try {
			boolean b = m.send("shpark@steg.co.kr", "shpark@steg.co.kr",
					"test", "Test");
			System.out.println("> End." + b);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
