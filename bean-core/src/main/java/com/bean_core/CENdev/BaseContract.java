package com.bean_core.CENdev;

import org.iq80.leveldb.DB;

import com.bean_core.TXs.CENCALL;

/**
 * The {@code BaseContract} interface defines the required structure for all 
 * Java-based smart contracts executed by a Contract Execution Node (CEN) 
 * in the BeanChain ecosystem.
 * 
 * <p>All contracts must implement both the {@link #init()} and 
 * {@link #execute(CENCALL)} methods. These define the contract's lifecycle:
 * 
 * <ul>
 *   <li><strong>{@code init()}:</strong> Called once immediately after contract deployment. 
 *   This method should initialize any required contract state (e.g., metadata, owner keys, 
 *   token creation, or config variables).</li>
 * 
 *   <li><strong>{@code execute(CENCALL call)}:</strong> Called whenever a user or system 
 *   triggers the contract with a {@code CENCALL}. This method contains the core contract logic 
 *   for responding to specific method calls and parameters.</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * public class MyContract implements BaseContract {
 * 
 *     @Override
 *     public void init() {
 *         // Set up contract owner or mint initial tokens
 *         System.out.println("Contract initialized.");
 *     }
 * 
 *     @Override
 *     public void execute(CENCALL call) {
 *         String method = call.getMethod();
 *         if ("mintToken".equals(method)) {
 *             // Handle token minting logic
 *             System.out.println("Token minted by " + call.getCaller());
 *         }
 *     }
 * }
 * }</pre>
 * 
 * <p><strong>Contract Execution Notes:</strong> 
 * Java smart contracts on BeanChain are executed exclusively by Contract Execution Nodes (CENs). 
 * While contracts can generate and return transactions (e.g., {@code TX}, {@code MintTX}, {@code TokenTX}), 
 * those transactions must still pass full network validation before they are accepted into a block.
 * 
 * <p>Developers are encouraged to write deterministic, state-aware contract logic that avoids side effects 
 * and follows BeanChain's standard transaction format and signature requirements.
 */
public interface BaseContract {

    /**
     * used internally by the contract to find its own identity in the DB 
     * loads contract address, hash, and pubKey from storage after contract is loaded and initialized by a CEN
     * 
     */
    void loadIndentity(DB db, String contractName, String devKey);

    /**
     * Called once at deployment time to initialize the contract state.
     * This may include minting tokens, setting parameters, or writing default values 
     * to the contract memory store.
     */
    void init();

    /**
     * Called on each user or system interaction with the contract.
     * Handles logic for the given method name and parameters provided in the {@code CENCALL}.
     *
     * @param call A {@code CENCALL} object containing method name, caller address, 
     *             and parameter payload for the contract invocation.
     */
    void execute(CENCALL call);
}
