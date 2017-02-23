package com.github.privacystreams.core.utilities.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.utilities.ItemFunction;
import com.github.privacystreams.core.Item;

/**
 * Created by yuanchun on 27/12/2016.
 * A function that gets the item map of an item
 */

class ItemToMapFunction extends ItemFunction<Map<String, Object>> {

    @Override
    public Map<String, Object> apply(UQI uqi, Item input) {
        return input.toMap();
    }
}
