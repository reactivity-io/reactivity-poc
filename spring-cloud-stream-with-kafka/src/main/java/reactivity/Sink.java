/*
 * The MIT License (MIT) Copyright (c) 2016 The reactivity authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package reactivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * A sink that consumes processed messages.
 *
 * @author Guillaume DROUET
 */
@EnableBinding(Sink.class)
public class Sink {

    /**
     * Logger.
     */
    private static Logger logger = LoggerFactory.getLogger(Sink.class);

    /**
     * Receives the processed message.
     *
     * @param input the message
     */
    @StreamListener
    public void receive(@Input(Source.OUTPUT) final Flux<Message<Object>> input) {
        input.subscribe((f) -> {
            String payload = (String) f.getPayload();
            logger.info("Timestamp: {} / ID: {} / Payload: {}", f.getHeaders().get("timestamp"), f.getHeaders().get("id"), payload);
        });
    }
}
