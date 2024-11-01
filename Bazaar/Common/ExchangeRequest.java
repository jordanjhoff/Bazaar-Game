package Common;

/**
 * An interface to represent the union between the player requests of exchanging pebbles and
 * requesting to draw a pebble
 */
public sealed interface ExchangeRequest permits PebbleDrawRequest, PebbleExchangeSequence {}