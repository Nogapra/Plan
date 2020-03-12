/*
 *  This file is part of Player Analytics (Plan).
 *
 *  Plan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License v3 as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Plan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Plan. If not, see <https://www.gnu.org/licenses/>.
 */
package utilities;

import com.djrapitops.plan.delivery.domain.Nickname;
import com.djrapitops.plan.delivery.domain.WebUser;
import com.djrapitops.plan.delivery.rendering.json.graphs.line.Point;
import com.djrapitops.plan.gathering.domain.*;
import com.djrapitops.plan.storage.database.sql.tables.KillsTable;
import com.djrapitops.plan.utilities.PassEncryptUtil;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RandomData {

    private RandomData() {
        /* Static method class */
    }

    private static final Random r = new Random();

    public static int randomInt(int rangeStart, int rangeEnd) {
        return ThreadLocalRandom.current().nextInt(rangeStart, rangeEnd);
    }

    public static long randomTime() {
        return randomTimeAfter(0);
    }

    public static long randomTimeAfter(long after) {
        return randomLong(after, System.currentTimeMillis());
    }

    public static long randomLong(long rangeStart, long rangeEnd) {
        return ThreadLocalRandom.current().nextLong(rangeStart, rangeEnd);
    }

    public static String randomString(int size) {
        return RandomStringUtils.randomAlphanumeric(size);
    }

    public static List<Nickname> randomNicknames(UUID serverUUID) {
        return pickMultiple(randomInt(15, 30), () -> randomNickname(serverUUID));
    }

    public static Nickname randomNickname(UUID serverUUID) {
        return new Nickname(randomString(randomInt(50, 100)), randomTime(), serverUUID);
    }

    public static List<WebUser> randomWebUsers() throws PassEncryptUtil.CannotPerformOperationException {
        List<WebUser> test = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            test.add(new WebUser(randomString(5), PassEncryptUtil.createHash(randomString(7)), r.nextInt()));
        }
        return test;
    }

    public static List<TPS> randomTPS() {
        List<TPS> test = new ArrayList<>();
        for (int i = 0; i < randomInt(5, 100); i++) {
            int randInt = r.nextInt();
            long randLong = Math.abs(r.nextLong());
            test.add(new TPS(randLong, randLong, randInt, randLong, randLong, randInt, randInt, randLong));
        }
        return test;
    }

    public static List<Session> randomSessions() {
        return pickMultiple(randomInt(15, 30),
                () -> randomSession(
                        TestConstants.SERVER_UUID,
                        pickMultiple(4, () -> randomString(5)).toArray(new String[0]),
                        pickMultiple(5, UUID::randomUUID).toArray(new UUID[0])
                )
        );
    }

    public static String randomGameMode() {
        return pickAtRandom(GMTimes.getGMKeyArray());
    }

    public static <T> T pickAtRandom(T[] from) {
        return from[randomInt(0, from.length)];
    }

    public static <T> List<T> pickMultiple(int howMany, Supplier<T> supplier) {
        List<T> picked = new ArrayList<>();
        for (int i = 0; i < howMany; i++) {
            picked.add(supplier.get());
        }
        return picked;
    }

    public static Session randomSession(UUID serverUUID, String[] worlds, UUID... uuids) {
        Session session = new Session(uuids[0], serverUUID, RandomData.randomTime(), pickAtRandom(worlds), randomGameMode());
        session.endSession(RandomData.randomTimeAfter(session.getDate()));
        session.setWorldTimes(RandomData.randomWorldTimes(worlds));
        if (uuids.length >= 2) {
            session.setPlayerKills(RandomData.randomKills(pickAtRandom(Arrays.copyOfRange(uuids, 1, uuids.length))));
        }
        return session;
    }

    public static List<Point> randomPoints() {
        List<Point> test = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            test.add(new Point(r.nextLong(), r.nextLong()));
        }
        return test;
    }

    public static List<GeoInfo> randomGeoInfo() {
        return pickMultiple(randomInt(15, 30), () -> new GeoInfo(randomString(10), randomTime()));
    }

    public static WorldTimes randomWorldTimes(String... worlds) {
        Map<String, GMTimes> times = new HashMap<>();
        for (String world : worlds) {
            Map<String, Long> gmTimes = new HashMap<>();
            for (String gm : GMTimes.getGMKeyArray()) {
                gmTimes.put(gm, randomLong(0, TimeUnit.HOURS.toMillis(2)));
            }
            times.put(world, new GMTimes(gmTimes));
        }
        return new WorldTimes(times);
    }

    public static List<PlayerKill> randomKills(UUID... victimUUIDs) {
        if (victimUUIDs == null || victimUUIDs.length == 1 && victimUUIDs[0] == null) return Collections.emptyList();

        return pickMultiple(randomInt(3, 15), () -> new PlayerKill(
                pickAtRandom(victimUUIDs),
                randomString(randomInt(10, KillsTable.WEAPON_COLUMN_LENGTH)),
                randomTime()
        ));
    }
}
