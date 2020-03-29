package com.sap.cp.appsec.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@Version
	@Column(name = "version")
	protected long version;

	@Column(name = "createdat", nullable = false, updatable = false)
	@CreatedDate
	protected Timestamp createdAt;

	@Column(name = "modifiedat", insertable = false)
	@LastModifiedDate
	protected Timestamp modifiedAt;

	@Column(name = "createdby", nullable = false, updatable = false)
	@CreatedBy
	protected String createdBy;

	@Column(name = "modifiedby", insertable = false)
	@LastModifiedBy
	protected String modifiedBy;

	public long getVersion() {
		return version;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public Timestamp getModifiedAt() {
		return modifiedAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	// use only in tests or when you need to map DTO to Entity
	public void setVersion(long version) {
		this.version = version;
	}

}
