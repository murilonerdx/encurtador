package com.murilonerdx.encurtador.repository;

import com.murilonerdx.encurtador.model.ShortUrl;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortUrlRepository extends MongoRepository<ShortUrl, String> {
	ShortUrl findByShortenedUrl(String shortUrl);
	ShortUrl findLastByUrlDestiny(String shortUrl);
}
