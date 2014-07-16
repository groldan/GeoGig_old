/* Copyright (c) 2013 OpenPlans. All rights reserved.
 * This code is licensed under the BSD New License, available at the root
 * application directory.
 */
package org.locationtech.geogig.web.api.commands;

import org.locationtech.geogig.api.Context;
import org.locationtech.geogig.api.porcelain.FetchOp;
import org.locationtech.geogig.api.porcelain.FetchResult;
import org.locationtech.geogig.api.porcelain.SynchronizationException;
import org.locationtech.geogig.web.api.AbstractWebAPICommand;
import org.locationtech.geogig.web.api.CommandContext;
import org.locationtech.geogig.web.api.CommandResponse;
import org.locationtech.geogig.web.api.CommandSpecException;
import org.locationtech.geogig.web.api.ResponseWriter;

/**
 * This is the interface for the Fetch operation in GeoGit.
 * 
 * Web interface for {@link FetchOp}
 */

public class FetchWebOp extends AbstractWebAPICommand {
    private boolean prune;

    private boolean fetchAll;

    private String remote;

    /**
     * Mutator for the prune variable
     * 
     * @param prune - true to prune remote tracking branches locally that no longer exist
     */
    public void setPrune(boolean prune) {
        this.prune = prune;
    }

    /**
     * Mutator for the fetchAll variable
     * 
     * @param fetchAll - true to fetch all
     */
    public void setFetchAll(boolean fetchAll) {
        this.fetchAll = fetchAll;
    }

    /**
     * Mutator for the remote variable
     * 
     * @param remotes - the remote to fetch from
     */
    public void setRemote(String remote) {
        this.remote = remote;
    }

    /**
     * Runs the command and builds the appropriate response
     * 
     * @param context - the context to use for this command
     * 
     * @throws CommandSpecException
     */
    @Override
    public void run(CommandContext context) {
        final Context geogit = this.getCommandLocator(context);

        FetchOp command = geogit.command(FetchOp.class);

        command.addRemote(remote);

        try {
            final FetchResult result = command.setAll(fetchAll).setPrune(prune).call();
            context.setResponseContent(new CommandResponse() {
                @Override
                public void write(ResponseWriter out) throws Exception {
                    out.start();
                    out.writeFetchResponse(result);
                    out.finish();
                }
            });
        } catch (SynchronizationException e) {
            switch (e.statusCode) {
            case HISTORY_TOO_SHALLOW:
            default:
                context.setResponseContent(CommandResponse
                        .error("Unable to fetch, the remote history is shallow."));
            }
        }
    }

}