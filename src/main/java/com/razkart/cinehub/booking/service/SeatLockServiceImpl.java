package com.razkart.cinehub.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis-based seat locking service using SETNX for atomic operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeatLockServiceImpl implements SeatLockService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String SEAT_LOCK_PREFIX = "cinehub:seat:lock:";
    private static final String USER_SEATS_PREFIX = "cinehub:user:seats:";

    @Override
    public List<Long> lockSeats(Long showId, List<Long> seatIds, Long userId, int expiryMinutes) {
        List<Long> lockedSeats = new ArrayList<>();
        Duration expiry = Duration.ofMinutes(expiryMinutes);

        for (Long seatId : seatIds) {
            String lockKey = buildSeatLockKey(showId, seatId);

            // SETNX - atomic operation, only succeeds if key doesn't exist
            Boolean locked = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, userId.toString(), expiry);

            if (Boolean.TRUE.equals(locked)) {
                lockedSeats.add(seatId);
                log.debug("Locked seat {} for user {} in show {}", seatId, userId, showId);
            } else {
                log.debug("Seat {} already locked in show {}", seatId, showId);
            }
        }

        // Track user's locked seats
        if (!lockedSeats.isEmpty()) {
            String userSeatsKey = buildUserSeatsKey(showId, userId);
            redisTemplate.opsForSet().add(userSeatsKey,
                    lockedSeats.stream().map(String::valueOf).toArray(String[]::new));
            redisTemplate.expire(userSeatsKey, expiry);
        }

        return lockedSeats;
    }

    @Override
    public void releaseSeats(Long showId, List<Long> seatIds) {
        List<String> keys = seatIds.stream()
                .map(seatId -> buildSeatLockKey(showId, seatId))
                .toList();
        redisTemplate.delete(keys);
        log.info("Released {} seats for show {}", seatIds.size(), showId);
    }

    @Override
    public void releaseSeatsByUser(Long showId, Long userId) {
        String userSeatsKey = buildUserSeatsKey(showId, userId);
        Set<String> seatIds = redisTemplate.opsForSet().members(userSeatsKey);

        if (seatIds != null && !seatIds.isEmpty()) {
            List<String> lockKeys = seatIds.stream()
                    .map(seatId -> buildSeatLockKey(showId, Long.parseLong(seatId)))
                    .toList();

            // Lua script: only delete if lock belongs to this user
            String luaScript = """
                local userId = ARGV[1]
                local deleted = 0
                for i, key in ipairs(KEYS) do
                    if redis.call('GET', key) == userId then
                        redis.call('DEL', key)
                        deleted = deleted + 1
                    end
                end
                return deleted
                """;

            redisTemplate.execute(
                    RedisScript.of(luaScript, Long.class),
                    lockKeys,
                    userId.toString()
            );

            redisTemplate.delete(userSeatsKey);
        }
        log.info("Released all seats for user {} in show {}", userId, showId);
    }

    @Override
    public Set<Long> getLockedSeats(Long showId) {
        String pattern = SEAT_LOCK_PREFIX + showId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            return Collections.emptySet();
        }

        return keys.stream()
                .map(key -> Long.parseLong(key.substring(key.lastIndexOf(":") + 1)))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isSeatAvailable(Long showId, Long seatId) {
        String lockKey = buildSeatLockKey(showId, seatId);
        return !Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    private String buildSeatLockKey(Long showId, Long seatId) {
        return SEAT_LOCK_PREFIX + showId + ":" + seatId;
    }

    private String buildUserSeatsKey(Long showId, Long userId) {
        return USER_SEATS_PREFIX + showId + ":" + userId;
    }
}
