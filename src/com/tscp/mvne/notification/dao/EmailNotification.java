package com.tscp.mvne.notification.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.persistence.Enumerated;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.customer.dao.GeneralSPResponse;
import com.tscp.mvne.hibernate.HibernateUtil;
import com.tscp.mvne.notification.EmailTemplate;
import com.tscp.mvne.notification.Notification;
import com.tscp.mvne.notification.NotificationCategory;
import com.tscp.mvne.notification.NotificationException;
import com.tscp.mvne.notification.NotificationType;

@SuppressWarnings("unchecked")
public class EmailNotification implements Notification, Serializable {
  private static final long serialVersionUID = 1L;

  private int notificationId;
  private int custId;
  private NotificationCategory notificationCategory;

  private NotificationType notificationType;
  private Date createDate;

  private int attemptNo;

  private Date sentDate;

  private List<NotificationParameter> notificationParametersList;

  private EmailTemplate template;
  private List<InternetAddress> toList;
  private InternetAddress from;
  private String subject;
  private String body;
  private List<InternetAddress> bccList;
  private List<InternetAddress> ccList;

  private String bcc;
  private String cc;
  private String to;

  public EmailNotification() {
    setNotificationType(NotificationType.EMAIL);
  }

  public int getCustId() {
    return custId;
  }

  public void setCustId(int custId) {
    this.custId = custId;
  }

  @Enumerated
  public EmailTemplate getTemplate() {
    return template;
  }

  public void setTemplate(EmailTemplate template) {
    this.template = template;
  }

  public String getTo() {
    if (to == null && getToList() != null) {
      if (getToList() != null) {
        StringBuffer sb = new StringBuffer();
        for (InternetAddress address : getToList()) {
          sb.append(address.getAddress());
          sb.append("; ");
        }
        setTo(sb.toString());
      } else {
        setTo("");
      }
    }
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public String getCc() {
    if (cc == null) {
      if (getCcList() != null) {
        StringBuffer sb = new StringBuffer();
        for (InternetAddress address : getCcList()) {
          sb.append(address.getAddress());
          sb.append("; ");
        }
        setCc(sb.toString());
      } else {
        setCc("");
      }
    }
    return cc;
  }

  public void setCc(String cc) {
    this.cc = cc;
  }

  public String getBcc() {
    if (bcc == null) {
      if (getBccList() != null) {
        StringBuffer sb = new StringBuffer();
        for (InternetAddress address : getBccList()) {
          sb.append(address.getAddress());
          sb.append("; ");
        }
        setBcc(sb.toString());
      } else {
        setBcc("");
      }
    }
    return bcc;
  }

  public void setBcc(String bcc) {
    this.bcc = bcc;
  }

  public List<InternetAddress> getToList() {
    return toList;
  }

  public void setToList(List<InternetAddress> toList) {
    this.toList = toList;
  }

  public InternetAddress getFrom() {
    return from;
  }

  public void setFrom(InternetAddress from) {
    this.from = from;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public List<InternetAddress> getBccList() {
    return bccList;
  }

  public void setBccList(List<InternetAddress> bccList) {
    this.bccList = bccList;
  }

  public List<InternetAddress> getCcList() {
    return ccList;
  }

  public void setCcList(List<InternetAddress> ccList) {
    this.ccList = ccList;
  }

  @Override
  public int getNotificationId() {
    return notificationId;
  }

  @Override
  public void setNotificationId(int notificationId) {
    this.notificationId = notificationId;
  }

  @Override
  @Enumerated
  public NotificationCategory getNotificationCategory() {
    return notificationCategory;
  }

  @Override
  public void setNotificationCategory(NotificationCategory notificationCategory) {
    this.notificationCategory = notificationCategory;
  }

  @Override
  @Enumerated
  public NotificationType getNotificationType() {
    return notificationType;
  }

  @Override
  public void setNotificationType(NotificationType notificationType) {
    this.notificationType = notificationType;
  }

  @Override
  public Date getCreateDate() {
    return createDate;
  }

  @Override
  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  @Override
  public int getAttemptNo() {
    return attemptNo;
  }

  @Override
  public void setAttemptNo(int attemptNo) {
    this.attemptNo = attemptNo;
  }

  @Override
  public Date getSentDate() {
    return sentDate;
  }

  @Override
  public void setSentDate(Date sentDate) {
    this.sentDate = sentDate;
  }

  @Override
  public void loadNotification() {
    // TODO Auto-generated method stub
  }

  @Override
  public void saveNotification() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    boolean freshInsert = false;

    String queryName = "upd_notification_email";

    Query q = session.getNamedQuery(queryName);

    q.setParameter("in_notification_id", getNotificationId());
    q.setParameter("in_cust_id", getCustId());
    q.setParameter("in_notification_type", getNotificationType().toString());
    q.setParameter("in_notification_category", getNotificationCategory().toString());
    q.setParameter("in_attempt_no", getAttemptNo());
    q.setParameter("in_email_template", getTemplate().toString());
    q.setParameter("in_email_to", getTo());
    q.setParameter("in_email_cc", getCc());
    q.setParameter("in_email_bcc", getBcc());
    q.setParameter("in_email_from", getFrom().getAddress());
    q.setParameter("in_email_subject", getSubject());
    q.setParameter("in_email_body", getBody());
    q.setParameter("in_sent_date", getSentDate() == null ? "" : getSentDate());

    // System.out.println("(:CURSOR,"+getNotificationId()+","+getCustId()+",'"+
    // getNotificationType()+"','"+
    // getNotificationCategory()+"',"+
    // getAttemptNo()+",'"+
    // getTemplate()+"','"+
    // getTo()+"','"+
    // getCc()+"','"+
    // getBcc()+"','"+
    // getFrom()+"','"+
    // getSubject()+"','"+
    // getBody()+"',"+
    // getSentDate()+")");

    try {
      List<GeneralSPResponse> response = q.list();
      if (response != null) {
        for (GeneralSPResponse spResponse : response) {
          System.out.println("response object...");
          System.out.println(spResponse.toString());
          if (!spResponse.getStatus().equals("Y")) {
            throw new NotificationException("Error saving Email Notification");
          } else {
            if (getNotificationId() == 0) {
              setNotificationId(spResponse.getMvnemsgcode());
              freshInsert = true;
            }
          }
        }
      }

      queryName = "ins_notification_param";

      if (getNotificationParameters() != null) {
        for (NotificationParameter notificationParameter : getNotificationParameters()) {
          if (notificationParameter.getValue() != null) {
            q = session.getNamedQuery(queryName);
            q.setParameter("in_notification_id", getNotificationId());
            q.setParameter("in_param_key", notificationParameter.getKey());
            q.setParameter("in_param_value", notificationParameter.getValue());
            List<GeneralSPResponse> parameterResponseList = q.list();
            if (parameterResponseList != null) {
              for (GeneralSPResponse spResponse : parameterResponseList) {
                if (!spResponse.getStatus().equals("Y")) {
                  System.out.println(spResponse.getMvnemsgcode() + " " + spResponse.getMvnemsg());
                  System.out.println("Error saving Parameter " + notificationParameter.toString());
                }
              }
            }
          }
        }
      }

      session.getTransaction().commit();

    } catch (NotificationException ne) {
      session.getTransaction().rollback();
    }
  }

  @Override
  public List<NotificationParameter> getNotificationParameters() {
    return notificationParametersList;
  }

  @Override
  public void setNotificationParameters(List<NotificationParameter> notificationParametersList) {
    this.notificationParametersList = notificationParametersList;
  }

  public void loadNotificationParameters() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    String spName = "fetch_notification_parameters";

    Query q = session.getNamedQuery(spName);
    q.setParameter("in_notification_id", getNotificationId());

    List<NotificationParameter> notificationParametersList = q.list();
    setNotificationParameters(notificationParametersList);

    session.getTransaction().commit();
  }

}
