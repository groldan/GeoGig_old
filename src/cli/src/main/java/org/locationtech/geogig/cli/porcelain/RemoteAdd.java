/* Copyright (c) 2013 OpenPlans. All rights reserved.
 * This code is licensed under the BSD New License, available at the root
 * application directory.
 */

package org.locationtech.geogig.cli.porcelain;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.geogig.api.porcelain.RemoteAddOp;
import org.locationtech.geogig.api.porcelain.RemoteException;
import org.locationtech.geogig.cli.AbstractCommand;
import org.locationtech.geogig.cli.CLICommand;
import org.locationtech.geogig.cli.CommandFailedException;
import org.locationtech.geogig.cli.GeogigCLI;
import org.locationtech.geogig.cli.annotation.ReadOnly;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Adds a remote for the repository with the given name and URL.
 * <p>
 * With {@code -t <branch>} option, instead of the default global refspec for the remote to track
 * all branches under the refs/remotes/<name>/ namespace, a refspec to track only <branch> is
 * created.
 * <p>
 * CLI proxy for {@link RemoteAddOp}
 * <p>
 * Usage:
 * <ul>
 * <li> {@code geogit remote add [-t <branch>] <name> <url>}
 * </ul>
 * 
 * @see RemoteAddOp
 */
@ReadOnly
@Parameters(commandNames = "remote add", commandDescription = "Add a remote for the repository")
public class RemoteAdd extends AbstractCommand implements CLICommand {

    @Parameter(names = { "-t", "--track" }, description = "branch to track")
    private String branch = "*";

    @Parameter(names = { "-u", "--username" }, description = "user name")
    private String username = null;

    @Parameter(names = { "-p", "--password" }, description = "password")
    private String password = null;

    @Parameter(description = "<name> <url>")
    private List<String> params = new ArrayList<String>();

    /**
     * Executes the remote add command using the provided options.
     */
    @Override
    public void runInternal(GeogigCLI cli) {
        if (params == null || params.size() != 2) {
            printUsage(cli);
            throw new CommandFailedException();
        }

        try {
            cli.getGeogit().command(RemoteAddOp.class).setName(params.get(0)).setURL(params.get(1))
                    .setBranch(branch).setUserName(username).setPassword(password).call();
        } catch (RemoteException e) {
            switch (e.statusCode) {
            case REMOTE_ALREADY_EXISTS:
                throw new CommandFailedException("Could not add, a remote called '" + params.get(0)
                        + "' already exists.", e);
            default:
                throw new CommandFailedException(e);
            }
        }

    }

}