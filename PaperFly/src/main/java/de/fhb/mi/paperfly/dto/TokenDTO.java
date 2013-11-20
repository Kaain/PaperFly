/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhb.mi.paperfly.dto;

import lombok.*;

/**
 * @author MacYser
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TokenDTO extends BaseDTO {

    private String consumerKey;
    private String consumerSecret;
}
