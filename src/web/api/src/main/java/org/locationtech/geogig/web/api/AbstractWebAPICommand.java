/* Copyright (c) 2013 OpenPlans. All rights reserved.
 * This code is licensed under the BSD New License, available at the root
 * application directory.
 */
package org.locationtech.geogig.web.api;

import java.util.UUID;

import org.locationtech.geogig.api.Context;
import org.locationtech.geogig.api.GeogigTransaction;

/**
 * An abstract command that allows WebAPICommands to support long transactions.
 */

public abstract class AbstractWebAPICommand implements WebAPICommand {

    private UUID transactionId = null;

    /**
     * Accessor for the transactionId
     * 
     * @return the id of the transaction to run commands off of
     */
    public UUID getTransactionId() {
        return transactionId;
    }

    /**
     * Mutator for the transactionId
     * 
     * @param transactionId - the transaction id to run commands off of
     */
    public void setTransactionId(String transactionId) {
        if (transactionId != null) {
            this.transactionId = UUID.fromString(transactionId);
        }
    }

    /**
     * This function either builds a GeoGitTransaction to run commands off of if there is a
     * transactionId to build off of or the GeoGit commandLocator otherwise.
     * 
     * @param context - the context to get the information needed to get the commandLocator
     * @return
     */
    public Context getCommandLocator(CommandContext context) {
        if (transactionId != null) {
            return new GeogigTransaction(context.getGeoGIT().getContext(), transactionId);
        }
        return context.getGeoGIT().getContext();
    }

    public abstract void run(CommandContext context);

}