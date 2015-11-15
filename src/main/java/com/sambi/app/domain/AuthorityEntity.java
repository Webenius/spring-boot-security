package com.sambi.app.domain;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "authorities")
public class AuthorityEntity implements Serializable {

	private static final long serialVersionUID = -1716629025718240719L;

	@Id
	@Column(name = "authority", nullable = false)
	private String authority;

	@ManyToOne
	@JoinColumn(name="username", nullable=false)
	@Fetch(FetchMode.SELECT)
	private UserEntity user;

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}
}
