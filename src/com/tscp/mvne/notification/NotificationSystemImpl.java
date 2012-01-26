package com.tscp.mvne.notification;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.tscp.mvne.billing.Account;
import com.tscp.mvne.customer.Customer;
import com.tscp.mvne.network.NetworkInfo;
import com.tscp.mvne.notification.dao.EmailNotification;
import com.tscp.mvne.notification.dao.NotificationParameter;

public class NotificationSystemImpl implements NotificationSystem {

  public static String smtpHost;
  static String inputPropertyFile = "com/tscp/mvne/config/connection.tscpmvne.properties";
  Properties props;

  InternetAddress from;
  Vector<InternetAddress> bccList;

  public static String truconnectManageSite;
  public static String truconnectSupportSite;
  public static String truconnectTermsAndConditionsSite;

  public NotificationSystemImpl() {
    init();
  }

  @Override
  public void init() {
    ClassLoader cl = NotificationSystemImpl.class.getClassLoader();
    System.out.println("Loading Email Properties file...");
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    props = new Properties();
    try {
      props.load(in);

      smtpHost = props.getProperty("email.smtphost");
      System.out.println("Setting SMTP Host to " + smtpHost);
      truconnectManageSite = props.getProperty("truconnect.manage.site");
      truconnectSupportSite = props.getProperty("truconnect.support.site");
      truconnectTermsAndConditionsSite = props.getProperty("truconnect.terms.and.conditions.site");

      from = new InternetAddress("no-reply@truconnect.com", "TruConnect");
      // from = new InternetAddress("no-reply@telscape.net","Telscape");

      InternetAddress address = new InternetAddress("dta@telscape.net", "Dan Ta");
      bccList = new Vector<InternetAddress>();
      bccList.add(address);

      // address = new
      // InternetAddress("peter.maas@truconnect.com","Peter Maas");
      // bccList.add(address);

      // address = new InternetAddress("jholop@telscape.net","Joseph Holop");
      // bccList.add(address);

    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

  };

  public InternetAddress getFrom() {
    return from;
  }

  public List<InternetAddress> getBccList() {
    return bccList;
  }

  @Override
  public List<Notification> getFailedNotifications() {
    // TODO unimplemented
    return null;
  }

  @Override
  public void sendNotification(Notification notification) {
    // if(notificationsList != null ) {
    // for( Notification notification : notificationsList ) {
    if (notification instanceof EmailNotification) {
      EmailNotification email = (EmailNotification) notification;
      // email.get
      email.setBody(getBodyFromTemplate(email.getTemplate().toString(), email.getNotificationParameters()));
      if (email.getNotificationId() == 0) {
        email.saveNotification();
      }
      try {
        email.setAttemptNo(email.getAttemptNo() + 1);
        postMail(email.getToList(), email.getCcList(), email.getBccList(), "Test-Server::" + email.getSubject(), email
            .getBody(), email.getFrom());

        email.setSentDate(new Date());
      } catch (AddressException addr_ex) {
      } catch (MessagingException msg_ex) {
      } finally {
        email.saveNotification();
      }
    }
    // }
    // }
  }

  private String getBodyFromTemplate(String templateName, List<NotificationParameter> notificationParametersList) {
    // Velocity.init();
    Properties properties = new Properties();
    properties.setProperty("resource.loader", "class");
    // properties.setProperty("file.resource.loader.class",
    // "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
    // properties.setProperty("file.resource.loader.path",
    // "resources/emailTemplates");
    properties.setProperty("class.resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    // properties.setProperty();s
    Velocity.init(properties);
    VelocityContext context = new VelocityContext();

    for (NotificationParameter notificationParameter : notificationParametersList) {
      context.put(notificationParameter.getKey(), notificationParameter.getValue());
    }
    context.put("truconnectManageSite", truconnectManageSite);
    context.put("truconnectSupportSite", truconnectSupportSite);
    context.put("truconnectTermsAndConditionsSite", truconnectTermsAndConditionsSite);
    Template template = null;

    try {
      template = Velocity.getTemplate("/emailTemplates/" + templateName + ".vm");
    } catch (ResourceNotFoundException rnfe) {
      System.out.println("Resource " + templateName + " could not be found..." + rnfe.getMessage());
      rnfe.printStackTrace();
    } catch (ParseErrorException pee) {
      System.out.println("Parse Exception occurred : " + pee.getMessage());
      pee.printStackTrace();
    } catch (MethodInvocationException mie) {
      System.out.println("Method Invocation exception :: " + mie.getMessage());
      mie.printStackTrace();
    }
    StringWriter sw = new StringWriter();

    template.merge(context, sw);
    return sw.toString();
  }

  public void postMail(List<InternetAddress> recipients, List<InternetAddress> ccList, List<InternetAddress> bccList,
      String subject, String message, InternetAddress from) throws MessagingException {
    boolean debug = false;

    // Set the host smtp address
    Properties emailProps = new Properties();
    emailProps.put("mail.smtp.host", smtpHost);

    // create some properties and get the default Session
    Session session = Session.getDefaultInstance(emailProps, null);
    session.setDebug(debug);

    // create a message
    Message msg = new MimeMessage(session);

    // set the from and to address
    // InternetAddress addressFrom = new InternetAddress(from);
    msg.setFrom(from);
    // Set the TO
    for (InternetAddress recipient : recipients) {
      msg.addRecipient(RecipientType.TO, recipient);
    }
    // Set the BCC
    if (bccList != null) {
      for (InternetAddress recipient : bccList) {
        msg.addRecipient(RecipientType.BCC, recipient);

      }
    }

    // Set the CC
    if (ccList != null) {
      InternetAddress[] ccArray = new InternetAddress[ccList.size()];
      for (InternetAddress recipient : ccList) {
        msg.addRecipient(RecipientType.CC, recipient);
      }
    }

    // Optional : You can also set your custom headers in the Email if you Want
    msg.addHeader("MyHeaderName", "myHeaderValue");

    // Setting the Subject and Content Type
    msg.setSubject(subject);
    msg.setContent(message, "text/html");
    try {
      Transport.send(msg);
    } catch (SendFailedException send_ex) {
      System.out.println("***** Send Failure Exception Thrown *****");
      send_ex.printStackTrace();
      Address[] validAddressList = send_ex.getValidSentAddresses();
      Address[] invalidAddressList = send_ex.getInvalidAddresses();
      String invalidEmailString = "";
      for (Address address : invalidAddressList) {
        invalidEmailString += address.toString() + "; ";
      }
      AddressException address_ex = new AddressException("The Following Email Address is invalid :: "
          + invalidEmailString);
      throw address_ex;
    }
  }

  public void sendNotification(String notificationType, String notificationCategory, String notificationTemplate,
      Customer customer, Account account, NetworkInfo networkInfo) throws NotificationException {
    if (customer == null || customer.getId() <= 0) {
      throw new NotificationException("Customer must be specified when sending notifications...");
    }
    if (account == null) {
      throw new NotificationException("Account object must be set when sending notifications...");
    } else if (account.getContact_email() == null || account.getContact_email().trim().isEmpty()) {
      throw new NotificationException("Contact Email address must be populated in the account object");
    }
    if (networkInfo == null) {
      throw new NotificationException("Network Information cannot be empty");
    } else if (networkInfo.getEsnmeiddec() == null || networkInfo.getEsnmeiddec().trim().isEmpty()) {

    }
    if (notificationType == null || notificationType.trim().isEmpty()) {
      throw new NotificationException("NotificationType must be specified");
    } else {
      try {
        NotificationType.valueOf(notificationType);
      } catch (IllegalArgumentException ia_ex) {
        throw new NotificationException(notificationType + " is not a valid NotificationType");
      }
    }
    if (notificationCategory == null || notificationCategory.trim().isEmpty()) {
      throw new NotificationException("NotificationCategory must be designated");
    } else {
      try {
        NotificationCategory.valueOf(notificationCategory);
      } catch (IllegalArgumentException ia_ex) {
        throw new NotificationException(notificationCategory + " is not a valid NotificationCategory");
      }
    }

    Vector<NotificationParameter> notificationParameterList = new Vector<NotificationParameter>();

  }

}
