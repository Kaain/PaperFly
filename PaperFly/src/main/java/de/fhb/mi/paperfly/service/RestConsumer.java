package de.fhb.mi.paperfly.service;

import de.fhb.mi.paperfly.dto.AccountDTO;
import de.fhb.mi.paperfly.dto.RegisterAccountDTO;
import de.fhb.mi.paperfly.dto.RoomDTO;

import java.util.List;

/**
 * @author Christoph Ott
 */
public interface RestConsumer {

    AccountDTO editAccount(AccountDTO account);

    AccountDTO getAccount(String mail);

    AccountDTO getAccountByUsername(String username);

    List<AccountDTO> getAccountsInRoom(long roomID);

    RoomDTO locateAccount(String username);

    boolean login(String mail, String password);

    AccountDTO register(RegisterAccountDTO account);

    List<AccountDTO> searchAccount(String query);
}
