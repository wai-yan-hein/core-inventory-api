/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="usr_comp_role")
public class UsrCompRole implements java.io.Serializable{
    private UsrCompRoleKey key;

    @EmbeddedId
    public UsrCompRoleKey getKey() {
        return key;
    }

    public void setKey(UsrCompRoleKey key) {
        this.key = key;
    }
}
