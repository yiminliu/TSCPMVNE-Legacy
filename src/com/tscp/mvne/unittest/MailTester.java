package com.tscp.mvne.unittest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import com.tscp.mvne.notification.NotificationSystemImpl;

public class MailTester {

  NotificationSystemImpl notificationSystemImpl;

  List<InternetAddress> recipients;
  List<InternetAddress> ccList;

  InternetAddress from;

  public MailTester() {
    notificationSystemImpl = new NotificationSystemImpl();
    recipients = new Vector<InternetAddress>();
    try {
      from = new InternetAddress("no-reply@truconnect.com", "TruConnect");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public void doTest() {
    try {
      notificationSystemImpl.postMail(recipients, ccList, notificationSystemImpl.getBccList(), "Test Email",
          "Ping Pong", from);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    System.out.println("Testing Email Functionality");
    MailTester mt = new MailTester();
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    try {
      System.out.println("Add a recipient email");
      String emailAddress = br.readLine();
      System.out.println("Please provide a contact name");
      String contactName = br.readLine();
      InternetAddress address = new InternetAddress(emailAddress, contactName);
      mt.recipients.add(address);
      mt.doTest();

    } catch (IOException io_ex) {
      io_ex.printStackTrace();
    }

    System.out.println("Exiting...");
  }
}
