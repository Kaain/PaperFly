package de.fhb.mi.paperfly.util;

/**
 * Interface for simplifying the use of AsyncTasks
 *
 * @author Christoph Ott
 */
public interface GetRoomAsyncDelegate {

    /**
     * Can be called if the AsyncTask is complete.
     *
     * @param success true if the AsyncTask was completed, false if not
     */
    public void getRoomAsyncComplete(boolean success);
}
