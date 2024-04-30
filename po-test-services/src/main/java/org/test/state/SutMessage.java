package org.test.state;

/**
 * Information on a message received from a SUT.
 *
 * @param vatNumber The relevant VAT number.
 * @param content The message's content.
 */
public record SutMessage(String vatNumber, String content) {
}
