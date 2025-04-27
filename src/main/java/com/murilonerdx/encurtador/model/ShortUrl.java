package com.murilonerdx.encurtador.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShortUrl {
	private String id;
	private String urlDestiny;
	private String shortenedUrl;
	private LocalDate dateCreated = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
	private LocalDate lastAccess = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

	@Indexed(expireAfter = "P2D") // 2 dias
	private Instant createdAt = Instant.now();

	public ShortUrl(String id, String urlDestiny, String shortenedUrl) {
		this.id = id;
		this.urlDestiny = urlDestiny;
		this.shortenedUrl = shortenedUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrlDestiny() {
		return urlDestiny;
	}

	public void setUrlDestiny(String urlDestiny) {
		this.urlDestiny = urlDestiny;
	}

	public String getShortenedUrl() {
		return shortenedUrl;
	}

	public void setShortenedUrl(String shortenedUrl) {
		this.shortenedUrl = shortenedUrl;
	}

	public LocalDate getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDate dateCreated) {
		this.dateCreated = dateCreated;
	}

	public LocalDate getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(LocalDate lastAccess) {
		this.lastAccess = lastAccess;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
