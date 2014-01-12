/*
 * Copyright (C) 2013 Michael Koppen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhb.mi.paperfly.dto;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AccountDTO extends BaseDTO {

    private String email;
    private String username;
    private String lastName;
    private String firstName;
    private List<AccountDTO> friendList;

    public AccountDTO(final AccountDTO account) {
        this.setEmail(account.getEmail());
        this.setUsername(account.getUsername());
        this.setLastName(account.getLastName());
        this.setFirstName(account.getFirstName());
        this.setFriendList(account.getFriendList());
        this.setCreated(account.getCreated());
        this.setLastModified(account.getLastModified());
        this.setEnabled(account.isEnabled());
    }
}
