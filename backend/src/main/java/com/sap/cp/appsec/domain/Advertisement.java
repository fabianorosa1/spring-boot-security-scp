package com.sap.cp.appsec.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "my.sample.Advertisement")
public class Advertisement extends BaseEntity {
    /**
     ** technical fields
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_advertisement")
    @SequenceGenerator(name="seq_advertisement", sequenceName="SeqAdvertisement", allocationSize=1)    
    protected Long id;
    
	/**
	 * mandatory fields
	 **/
	@NotBlank
	@Column(name = "title")
	private String title;

	@NotBlank
	@Column(name = "contact")
	private String contact;

	@NotNull
	@Column(name = "confidentiality_level")
	private ConfidentialityLevel confidentialityLevel;

	/**
	 * Any JPA Entity needs a default constructor.
	 */
	public Advertisement() {
	}

	public Advertisement(String title, String contact, ConfidentialityLevel confidentialityLevel) {
		this.title = title;
		this.contact = contact;
		if (confidentialityLevel != null) {
			this.confidentialityLevel = confidentialityLevel;
		} else {
			this.confidentialityLevel = ConfidentialityLevel.STRICTLY_CONFIDENTIAL;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ConfidentialityLevel getConfidentialityLevel() {
		return confidentialityLevel;
	}

	public String getContact() {
		return contact;
	}

	@Override
	public String toString() {
		return "Advertisement [id=" + id + ", title=" + title + "]";
	}
}
