package com.tscp.mvne.unittest.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.customer.dao.GeneralSPResponse;
import com.tscp.mvne.hibernate.HibernateUtil;

public class ReservedMDN {

  private String MDN;
  private Date insertDate;
  private Date releaseDate;
  private String notes;

  public ReservedMDN() {

  }

  public String getMDN() {
    return MDN;
  }

  public void setMDN(String mDN) {
    MDN = mDN;
  }

  public Date getInsertDate() {
    return insertDate;
  }

  public void setInsertDate(Date insertDate) {
    this.insertDate = insertDate;
  }

  public Date getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(Date releaseDate) {
    this.releaseDate = releaseDate;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public void update() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("sp_update_reserved_mdns");
    q.setParameter("in_mdn", getMDN());
    q.setParameter("in_release_date", getReleaseDate() == null ? "" : getReleaseDate());
    q.setParameter("in_notes", getNotes());
    List<GeneralSPResponse> generalSPResponseList = q.list();

    if (generalSPResponseList != null && generalSPResponseList.size() > 0) {
      for (GeneralSPResponse generalSPResponse : generalSPResponseList) {
        System.out.println("Response :: " + generalSPResponse.toString());
      }
    } else {
      System.out.println("No General Response returned...");
    }

    session.getTransaction().commit();
  }
}
