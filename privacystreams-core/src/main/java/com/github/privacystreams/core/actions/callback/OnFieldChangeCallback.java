package com.github.privacystreams.core.actions.callback;

import java.util.ArrayList;
import java.util.List;

import com.github.privacystreams.core.Function;
import com.github.privacystreams.core.MultiItemStream;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.utils.Assertions;

/**
 * Created by yuanchun on 28/12/2016.
 * Callback with a field value of an item
 * if the field value is different from the field value of the former item.
 */

class OnFieldChangeCallback<TValue, Void> extends AsyncMultiItemStreamAction<Void> {
    private final String fieldToSelect;
    private final Function<TValue, Void> callback;

    OnFieldChangeCallback(String fieldToSelect, Function<TValue, Void> callback) {
        this.fieldToSelect = Assertions.notNull("fieldToSelect", fieldToSelect);
        this.callback = Assertions.notNull("callback", callback);
        this.addParameters(fieldToSelect, callback);
    }

    @Override
    protected Void init(MultiItemStream input) {
        return null;
    }

    @Override
    protected void applyInBackground(MultiItemStream input, Void output) {
        TValue lastFieldValue = null;
        while (!this.isCancelled()) {
            Item item = input.read();
            if (item == null) break;
            TValue fieldValue = item.getValueByField(this.fieldToSelect);
            if (!fieldValue.equals(lastFieldValue))
                this.callback.apply(this.getUQI(), fieldValue);
            lastFieldValue = fieldValue;
        }
    }

}
