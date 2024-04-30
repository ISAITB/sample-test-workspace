package org.test.state;

/**
 * Information on a pending 'receive' step.
 *
 * @param sessionId The test session identifier.
 * @param callId The 'receive' step's call identifier.
 * @param callbackAddress The Test Bed's callback address.
 * @param vatNumber The VAT number for the expected received message.
 */
public record PendingReceiveStep(String sessionId, String callId, String callbackAddress, String vatNumber) {
}
