package com.murilonerdx.encurtador.controller;

import com.murilonerdx.encurtador.model.ShortUrl;
import com.murilonerdx.encurtador.request.CreeateShortUrlRequest;
import com.murilonerdx.encurtador.service.ShortUrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class ShortUrlController {
	private final ShortUrlService shortUrlService;

	public ShortUrlController(ShortUrlService shortUrlService) {
		this.shortUrlService = shortUrlService;
	}

	@GetMapping("/get/{id}")
	public ShortUrl getById(@PathVariable String id) {
		return shortUrlService.getById(id);
	}

	@GetMapping("/{urlShort}")
	public void getUrlDestiny(@PathVariable String urlShort, HttpServletResponse response) throws IOException {
		if ("favicon.ico".equals(urlShort)) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		ShortUrl byUrlShort = shortUrlService.getByUrlShort(urlShort);
		response.sendRedirect(byUrlShort.getUrlDestination());
	}

	@GetMapping("/original/{urlShort}")
	public String getUrlOriginal(@PathVariable String urlShort, HttpServletResponse response) throws IOException {
		ShortUrl byUrlShort = shortUrlService.getByUrlShort(urlShort);
		return byUrlShort.getUrlDestination();
	}

	@PostMapping()
	public ShortUrl shortUrl(@RequestBody CreeateShortUrlRequest destiny) throws IOException {
		String id = UUID.randomUUID().toString().substring(0, 8);
		ShortUrl shortUrl = new ShortUrl(null, destiny.getUrlDestiny(), id);
		return shortUrlService.save(shortUrl);
	}
}
