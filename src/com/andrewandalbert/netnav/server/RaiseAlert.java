package com.andrewandalbert.netnav.server;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.andrewandalbert.netnav.dao.SiteConfigDAO;
import com.andrewandalbert.netnav.model.Device;
import com.andrewandalbert.netnav.model.SiteConfig;
import com.sun.mail.smtp.SMTPTransport;

public class RaiseAlert {

	private enum ALERT_TYPE{EMAIL,LOG};
	
	public static void sendEmail(Device device) {
		
		Properties props = System.getProperties();
		String smtpServer = (String)NetNavServer.props.get("email.smtp.server");
		props.put("mail.smtp.host",smtpServer);
		props.put("mail.smtp.port", (String)NetNavServer.props.get("email.smtp.port"));
		props.put("mail.smtps.auth", "true");
		Session session = Session.getInstance(props,null);
		javax.mail.Message message = new MimeMessage(session);
		
		try {
			message.setFrom(new InternetAddress("support@net-nav.co.uk"));
			
			//get site config,should only be one of these
			SiteConfig config = SiteConfigDAO.instance.getModel().get(new Long(1));
			//InternetAddress.parse takes comma seperated list of email addresses
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse((config.getEmailAddress()),false));
			
			message.setSubject("NetNav Offline Alert for " + config.getSiteName() );
			message.setText("Device: " +device.getDeviceName()+" on the " + device.getFloor() + " floor went offline at " + Calendar.getInstance().getTime()+"\n\r Device Details: " + device.toString());
			message.setHeader("X-Mailer", "smtpsend");
		    message.setSentDate(new Date());
		    
		    SMTPTransport transport = (SMTPTransport)session.getTransport("smtps");
			transport.connect(smtpServer, (String)NetNavServer.props.get("email.user"), (String)NetNavServer.props.get("email.password"));
			transport.sendMessage(message, message.getAllRecipients());
			System.out.println(transport.getLastServerResponse());
			transport.close();
			
		} catch (AddressException ate){
			ate.printStackTrace();
		} catch (MessagingException mee){
			mee.printStackTrace();
		}
		
		
	}
}

