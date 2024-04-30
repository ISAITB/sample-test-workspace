package org.test.gitb;

import com.gitb.core.LogLevel;
import com.gitb.core.ValueEmbeddingEnumeration;
import com.gitb.tr.TAR;
import com.gitb.tr.TestResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.test.state.PendingReceiveStep;
import org.test.state.SutMessage;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Component used to store sessions and their state.
 * <p/>
 * This class is key in maintaining an overall context across a request and one or more
 * responses. It allows mapping of received data to a given test session running in the
 * test bed.
 * <p/>
 * This implementation stores session information in memory. An alternative solution
 * that would be fault-tolerant could store test session data in a DB.
 */
@Component
public class StateManager {

    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(StateManager.class);

    /** The map of in-memory active sessions. */
    private final Map<String, Map<String, Object>> sessions = new HashMap<>();
    /** Parked SUT messages for later matching against test sessions. */
    private final List<SutMessage> sutMessages = new ArrayList<>();
    /** Lock object to use for synchronisation. */
    private final Object lock = new Object();

    @Autowired
    private TestBedNotifier testBedNotifier = null;
    @Autowired
    private Utils utils = null;

    /**
     * Create a new session.
     *
     * @param sessionId The session ID to use (if null a new one will be generated).
     * @param callbackURL The callback URL to set for this session.
     * @return The session ID that was generated (generated if not provided).
     */
    public String createSession(String sessionId, String callbackURL) {
        if (callbackURL == null) {
            throw new IllegalArgumentException("A callback URL must be provided");
        }
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
        }
        synchronized (lock) {
            Map<String, Object> sessionInfo = new HashMap<>();
            sessionInfo.put(SessionData.CALLBACK_URL, callbackURL);
            sessions.put(sessionId, sessionInfo);
        }
        return sessionId;
    }

    /**
     * Remove the provided session from the list of tracked sessions.
     *
     * @param sessionId The session ID to remove.
     */
    public void destroySession(String sessionId) {
        synchronized (lock) {
            sessions.remove(sessionId);
            // If we have no more active test sessions discard all parked SUT messages.
            if (sessions.isEmpty()) {
                sutMessages.clear();
            }
        }
    }

    /**
     * Handle a received SUT message.
     *
     * @param messageInfo The message information.
     */
    public void handleSutMessage(SutMessage messageInfo) {
        synchronized (lock) {
            if (sessions.isEmpty()) {
                // Ignore messages coming when we have no ongoing test sessions.
                LOG.info("Ignoring message received for VAT number {} as no sessions were active", messageInfo.vatNumber());
            } else {
                boolean sessionFound = false;
                for (var sessionEntry: sessions.entrySet()) {
                    List<PendingReceiveStep> pendingSteps = (List<PendingReceiveStep>) sessionEntry.getValue().get("pendingSteps");
                    if (pendingSteps != null && !pendingSteps.isEmpty()) {
                        // We have a test session with pending 'receive' steps - look for a match.
                        OptionalInt foundStepIndex = IntStream.range(0, pendingSteps.size())
                                .filter(i -> messageInfo.vatNumber().equalsIgnoreCase(pendingSteps.get(i).vatNumber()))
                                .findFirst();
                        if (foundStepIndex.isPresent()) {
                            // Matching 'receive' step found = notify Test Bed.
                            sessionFound = true;
                            PendingReceiveStep matchedStep = pendingSteps.remove(foundStepIndex.getAsInt());
                            completeReceiveStepWithPurchaseOrder(matchedStep, messageInfo.content());
                            LOG.info("Found session [{}] expecting a message for VAT number [{}]", matchedStep.sessionId(), matchedStep.vatNumber());
                        }
                    }
                }
                if (!sessionFound) {
                    LOG.info("No test session was found to be expecting message for VAT number [{}]", messageInfo.vatNumber());
                    sutMessages.add(messageInfo);
                }
            }
        }
    }

    /**
     * Handle a received 'receive' step.
     *
     * @param stepInfo The step's information.
     */
    public void handleReceiveStep(PendingReceiveStep stepInfo) {
        synchronized (lock) {
            // Check to see if we have an already received SUT message for the expected VAT number.
            OptionalInt foundMessageIndex = IntStream.range(0, sutMessages.size())
                    .filter(i -> stepInfo.vatNumber().equalsIgnoreCase(sutMessages.get(i).vatNumber()))
                    .findFirst();
            if (foundMessageIndex.isPresent()) {
                // Found matching SUT message - notify Test Bed.
                LOG.info("Found matching SUT message for test session [{}]", stepInfo.sessionId());
                SutMessage matchedMessage = sutMessages.remove(foundMessageIndex.getAsInt());
                completeReceiveStepWithPurchaseOrder(stepInfo, matchedMessage.content());
            } else {
                // SUT message not found - park step for later.
                LOG.info("Parking for later step expecting message for VAT number [{}] in session [{}]", stepInfo.vatNumber(), stepInfo.sessionId());
                if (sessions.containsKey(stepInfo.sessionId())) {
                    List<PendingReceiveStep> pendingSteps = (List<PendingReceiveStep>) sessions.get(stepInfo.sessionId()).computeIfAbsent("pendingSteps", key -> new ArrayList<PendingReceiveStep>());
                    pendingSteps.add(stepInfo);
                }
                testBedNotifier.sendLogMessage(stepInfo.sessionId(), stepInfo.callbackAddress(), "Ready to receive SUT message for VAT number [%s].".formatted(stepInfo.vatNumber()), LogLevel.INFO);
            }
        }
    }

    /**
     * Complete a 'receive' step by notifying the Test Bed.
     *
     * @param stepInfo The 'receive' step's information.
     * @param purchaseOrder The purchase order to return.
     */
    private void completeReceiveStepWithPurchaseOrder(PendingReceiveStep stepInfo, String purchaseOrder) {
        TAR report = utils.createReport(TestResultType.SUCCESS);
        report.getContext().getItem().add(utils.createAnyContentSimple("purchaseOrder", purchaseOrder, ValueEmbeddingEnumeration.STRING));
        testBedNotifier.notifyTestBed(stepInfo.sessionId(), stepInfo.callId(), stepInfo.callbackAddress(), report);
    }

    /**
     * Constants used to identify data maintained as part of a session's state.
     */
    public static class SessionData {

        /** The URL on which the test bed is to be called back. */
        public static final String CALLBACK_URL = "callbackURL";

    }

}
