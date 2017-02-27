package com.github.privacystreams.core.transformations;

import com.github.privacystreams.core.SingleItemStream;

/**
 * Created by yuanchun on 28/11/2016.
 * Transform a stream to a stream
 */

public abstract class S2STransformation extends StreamTransformation<SingleItemStream, SingleItemStream> {

    protected SingleItemStream initOutput(SingleItemStream input) {
        return new SingleItemStream(this.getUQI(), input.getStreamProvider().compound(this));
    }

}
