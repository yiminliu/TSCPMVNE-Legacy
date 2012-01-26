package com.tscp.mvne.unittest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.mail.internet.InternetAddress;

import com.tscp.mvne.notification.NotificationCategory;
import com.tscp.mvne.notification.NotificationSystemImpl;
import com.tscp.mvne.notification.NotificationType;
import com.tscp.mvne.notification.dao.EmailNotification;
import com.tscp.mvne.notification.dao.NotificationParameter;

public class NotificationTester {

  public static void main(String[] args) {

    System.out.println("Testing Notification Unit");

    NotificationSystemImpl nsi = new NotificationSystemImpl();
    NotificationTester nt = new NotificationTester();
    EmailNotification email = new EmailNotification();

    List<NotificationParameter> npList = new Vector<NotificationParameter>();

    boolean running = true;
    String command = "";
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    while (running) {
      System.out.println("Please enter a command...");
      try {
        System.out.println("Send Template or Exit");
        command = br.readLine();
        if (command.substring(0).equalsIgnoreCase("Y")) {

        } else if (command.equalsIgnoreCase("exit")) {
          running = false;
          break;
        } else {
          throw new IOException("Invalid input");
        }
        System.out.println("Use default email?");
        command = br.readLine();
        if (command.substring(0).equalsIgnoreCase("y")) {
          email = nt.getEmail();
          System.out.println("default email information loaded");
        } else {
          throw new IOException("Invalid entry, only Y or N is accepted");
        }

        System.out.println("Which Template are you sending?");
        nt.genTemplateMenu();
        command = br.readLine();
        try {
          switch (Integer.parseInt(command)) {
          case 1:

            email.setSubject("Your Service has been Suspended");
            // email.setTemplate("notification_hotlined_account");

            email.setNotificationCategory(NotificationCategory.WARNING);
            npList = nt.getSuspendedParameters();
            break;
          case 2:
          case 3:
          case 4:
            email.setSubject("Your payment has been processed");
            // email.setTemplate("notification_payment_success");

            email.setNotificationCategory(NotificationCategory.INFO);
            npList = nt.getPaymentSuccessParameters();
            break;
          default:
            throw new IOException("Invalid command number");
          }

        } catch (NumberFormatException nfe) {
          System.out.println("Please enter a numeric command number");
        }

        // email.setTemplate("notifications_hotlined_account");
        // email.setTemplate("mytemplate");
        // email.setTemplate("nha");

        email.setNotificationParameters(npList);

        System.out.println("Send the notification?");
        command = br.readLine();
        if (command.substring(0).equalsIgnoreCase("y")) {
          System.out.println("Sending email notification");
          nsi.sendNotification(email);
        }

      } catch (IOException io_ex) {
        System.out.println("Error: " + io_ex.getMessage());
        System.out.println("Invalid Command...Please try again...");
      }
    }

    System.out.println("Exiting Notification Tester");
  }

  void genTemplateMenu() {
    System.out.println("Suspend Template.................001");
    System.out.println("Restore Template.................002");
    System.out.println("Payment Success Template.........003");
    System.out.println("Payment Failure Template.........004");
  }

  EmailNotification getEmail() {

    EmailNotification email = new EmailNotification();
    InternetAddress ia = new InternetAddress();
    // ia.setAddress("dta@telscape.net");
    ia.setAddress("ixdta39@gmail.co");
    Vector<InternetAddress> toList = new Vector<InternetAddress>();
    toList.add(ia);
    email.setToList(toList);

    ia = new InternetAddress();
    ia.setAddress("omssupport@telscape.net");
    Vector<InternetAddress> bccList = new Vector<InternetAddress>();
    bccList.add(ia);
    email.setBccList(bccList);

    ia = new InternetAddress();
    ia.setAddress("no-reply@truconnect.com");
    try {
      ia.setPersonal("TruConnect");
    } catch (UnsupportedEncodingException ue_ex) {
      ue_ex.printStackTrace();
      System.exit(-1);
    }
    email.setFrom(ia);

    email.setNotificationType(NotificationType.EMAIL);
    email.setCustId(1);

    return email;
  }

  List<NotificationParameter> getSuspendedParameters() {

    Vector<NotificationParameter> npList = new Vector<NotificationParameter>();

    NotificationParameter np = new NotificationParameter();
    np.setKey("userName");
    np.setValue("Dan");
    npList.add(np);

    np = new NotificationParameter("accountno", "123456");
    npList.add(np);

    np = new NotificationParameter("mdn", "2138675309");
    npList.add(np);

    np = new NotificationParameter("esn", "09119126311");
    npList.add(np);

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    np = new NotificationParameter("suspendDate", sdf.format(new Date()));
    npList.add(np);

    // np = new
    // NotificationParameter("truconnectManageSite","http://test-activate.truconnect.com/truconnect/login");
    // npList.add(np);

    return npList;
  }

  List<NotificationParameter> getPaymentSuccessParameters() {
    /**
     * getPaymentSuccessBody( tscpMvneAccount.getFirstname(), /* userName //
     * Integer.toString(tscpMvneAccount.getAccountno()), /* accountNo //
     * account.getMdn(),/*MDN // networkInfo.getEsnmeiddec(), /*ESN //
     * confirmationNumber, /* confirmationNumber // topup.getTopupAmount(), /*
     * Pmt Amount // paymentMethod, /* paymentMethod // paymentSource, /* Pmt
     * Source // legibleDate.format(new Date()) /* pmt date //
     * ,tscpMvneAccount.getBalance()); //
     */

    Vector<NotificationParameter> npList = new Vector<NotificationParameter>();

    NotificationParameter np = new NotificationParameter("userName", "Dan");
    npList.add(np);

    np = new NotificationParameter("accountNo", "123456");
    npList.add(np);

    np = new NotificationParameter("tn", "2138675309");
    npList.add(np);

    np = new NotificationParameter("esn", "09111964523");
    npList.add(np);

    np = new NotificationParameter("confirmationNumber", "CC0TID213312");
    npList.add(np);

    np = new NotificationParameter("amount", "10.00");
    npList.add(np);

    np = new NotificationParameter("paymentMethod", "Visa");
    npList.add(np);

    np = new NotificationParameter("paymentSource", "0026");
    npList.add(np);

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    np = new NotificationParameter("paymentDate", sdf.format(new Date()));
    npList.add(np);

    np = new NotificationParameter("balance", "$0.00");
    npList.add(np);

    return npList;
  }
}
