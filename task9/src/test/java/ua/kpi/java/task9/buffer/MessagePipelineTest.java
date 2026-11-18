package ua.kpi.java.task9.buffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
 * Тести конвеєра «генератори → буфер 1 → перекладачі → буфер 2».
 */
class MessagePipelineTest {

    private static final Pattern FORMAT = Pattern.compile(
            "Потік № ([67]) переклав повідомлення «Потік № ([1-5]) згенерував повідомлення № (\\d+)»");

    @Test
    void allWorkerThreadsAreDaemons() {
        MessagePipeline pipeline = new MessagePipeline(4);
        pipeline.start();

        assertEquals(MessagePipeline.PRODUCERS + MessagePipeline.MOVERS, pipeline.threads().size());
        assertTrue(pipeline.threads().stream().allMatch(Thread::isDaemon));
    }

    @Test
    void messagesHaveRequiredFormatAndAreNotDuplicated() {
        MessagePipeline pipeline = new MessagePipeline(4);
        pipeline.start();
        Set<String> originals = new HashSet<>();

        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            for (int i = 0; i < 500; i++) {
                String message = pipeline.take();
                Matcher m = FORMAT.matcher(message);
                assertTrue(m.matches(), message);
                // пара (генератор, номер повідомлення) має бути унікальною
                assertTrue(originals.add(m.group(2) + "/" + m.group(3)), "повтор: " + message);
            }
        });
    }

    @Test
    void pipelineCannotBeStartedTwice() {
        MessagePipeline pipeline = new MessagePipeline(2);
        pipeline.start();

        assertThrows(IllegalStateException.class, pipeline::start);
    }
}
