package de.fhb.mi.paperfly.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.dto.RegisterAccountDTO;
import de.fhb.mi.paperfly.dto.RoomDTO;
import de.fhb.mi.paperfly.dto.Status;
import de.fhb.mi.paperfly.dto.TokenDTO;

/**
 * The interface for consuming the rest service.
 *
 * @author Christoph Ott
 * @author Andy Klay (klay@fh-brandenburg.de)
 */
public interface RestConsumer {

    /**
     * Get all accounts in room
     *
     *
     * @param roomID the room
     *
     * @return all accounts in room
     */
    List<AccountDTO> getUsersInRoom(Long roomID) throws RestConsumerException;

    RoomDTO getRoom(String roomID) throws RestConsumerException;

    /**
     * Edits an account
     *
     * @param editedAccount the account to edit
     *
     * @return the edited account
     */
    AccountDTO editAccount(AccountDTO editedAccount) throws RestConsumerException, UnsupportedEncodingException;

    /**
     * Gets the acutal account with the actual friends with online status
     *
     * @throws RestConsumerException
     * @throws UnsupportedEncodingException
     */
    void updateMyAccount() throws RestConsumerException, UnsupportedEncodingException;

    /**
     * Adds a friend to the actual logged in account.
     *
     * @param friendUsername the user to be added as friend
     * @return the actual logged in account with the updated friend list
     * @throws RestConsumerException
     */
    AccountDTO addFriend(String friendUsername) throws RestConsumerException;


    /**
     * Removes a friend from the actual logged in account.
     *
     * @param friendUsername the user to be removed from the friend list
     *
     * @return the actual logged in account with the updated friend list
     * @throws RestConsumerException
     */
    AccountDTO removeFriend(String friendUsername) throws RestConsumerException;

    /**
     * sets the Account status
     *
     * @param status
     *
     * @return
     * @throws RestConsumerException
     */
    AccountDTO setMyAccountStatus(Status status) throws RestConsumerException;

    /**
     * Gets the account by the given username.
     *
     * @param username the username
     *
     * @return the account
     * @throws RestConsumerException
     */
    AccountDTO getAccountByUsername(String username) throws RestConsumerException;

    /**
     * Gets all accounts in the given room.
     *
     * @param roomID the room
     *
     * @return a list of accounts in the given room
     */
    List<AccountDTO> getAccountsInRoom(long roomID) throws RestConsumerException;

    /**
     * Locates an account.
     *
     * @param username the username of the account
     *
     * @return the room where the user is located
     */
    RoomDTO locateAccount(String username) throws RestConsumerException;

    /**
     * Tries to login.
     *
     * @param mail     the mail address of the account
     * @param password the password of the account
     *
     * @return the tokens if the login was successful, null if not
     * @throws RestConsumerException
     */
    TokenDTO login(String mail, String password) throws RestConsumerException;

    /**
     * Registers an account.
     *
     * @param account the account to register
     *
     * @return the tokens if the registration was successful, null if not
     * @throws UnsupportedEncodingException
     * @throws RestConsumerException
     */
    TokenDTO register(RegisterAccountDTO account) throws UnsupportedEncodingException, RestConsumerException;

    /**
     * Searches for accounts.
     *
     * @param query the string to search for
     *
     * @return a list of accounts
     */
    List<AccountDTO> searchAccount(String query) throws RestConsumerException;
}
