package com.djrapitops.plan.system.tasks;

import com.djrapitops.plan.system.webserver.cache.PageId;
import com.djrapitops.plan.system.webserver.cache.ResponseCache;
import com.djrapitops.plugin.task.AbsRunnable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayersPageRefreshTask extends AbsRunnable {

    @Inject
    public PlayersPageRefreshTask() {
    }

    @Override
    public void run() {
        ResponseCache.clearResponse(PageId.PLAYERS.id());
    }
}