package com.djrapitops.plan.utilities.formatting.time;

import com.djrapitops.plan.utilities.formatting.Formatter;
import com.djrapitops.plan.data.store.objects.DateHolder;

/**
 * //TODO Class Javadoc Comment
 *
 * @author Rsl1122
 */
public class DateHolderFormatter implements Formatter<DateHolder> {

    private final Formatter<Long> formatter;

    public DateHolderFormatter(Formatter<Long> formatter) {
        this.formatter = formatter;
    }

    @Override
    public String apply(DateHolder dateHolder) {
        return formatter.apply(dateHolder.getDate());
    }
}