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
 * @author MacYser
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
