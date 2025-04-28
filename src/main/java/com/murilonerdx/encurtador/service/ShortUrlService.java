package com.murilonerdx.encurtador.service;

import com.murilonerdx.encurtador.model.ShortUrl;
import com.murilonerdx.encurtador.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class ShortUrlService {

	private final ShortUrlRepository shortUrlRepository;

	public ShortUrlService(ShortUrlRepository shortUrlRepository) {
		this.shortUrlRepository = shortUrlRepository;
	}

	public ShortUrl getById(String id) {
		Optional<ShortUrl> shortUrlFound = shortUrlRepository.findById(id);
		shortUrlFound.ifPresent((shortUrl) -> {
			shortUrl.setLastAccess(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
		});

		return shortUrlFound.orElse(null);
	}

	public ShortUrl getByUrlDestiny(String url) {
		return shortUrlRepository.findLastByUrlDestination(url);
	}

	public ShortUrl getByUrlShort(String url) {
		return shortUrlRepository.findByShortenedUrl(url);
	}

	public ShortUrl save(ShortUrl shortUrl) {
		shortUrl.setId(null);
		return shortUrlRepository.save(shortUrl);
	}
}
