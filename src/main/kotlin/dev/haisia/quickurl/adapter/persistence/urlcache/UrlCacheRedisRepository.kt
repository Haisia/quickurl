package dev.haisia.quickurl.adapter.persistence.urlcache

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UrlCacheRedisRepository : CrudRepository<UrlCache, String>
