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
     * Edits an account
     *
     * @param editedAccount the account to edit
     *
     * @return the edited account
     */
    AccountDTO editAccount(AccountDTO editedAccount) throws RestConsumerException, UnsupportedEncodingException;

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
