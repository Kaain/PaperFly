/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.mi.paperfly.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a mapped room.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class RoomDTO {

    private Long id;
    private String name;
    private CoordinateDTO coordinate;
    private String building;
}
