/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Patterns.Memento;

/**
 *
 * @author BRKsCosta and danielcordeiro
 */
public interface IOriginator {
    
     /**
     * Request of memento for current state.
     * 
     * @return      the memento state 
     */
    public IMemento save();
    
    /**
     * Request to change state for this memento.
     * 
     * @param savedState    the memento state to restore
     */
    public void restore(IMemento savedState);
    
}
