package com.ikabanov.cache

/**
 * Reason is an enum, that represents response after using CacheInteractor.
 */
enum class Reason {
    DONE,
    DONE_NEGATIVE,
    ABORTED,
    NONE
}