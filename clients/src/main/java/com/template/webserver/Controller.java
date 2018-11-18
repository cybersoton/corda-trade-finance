package com.template.webserver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/uktf") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
    private final CordaX500Name myLegalName;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    private final List<String> serviceNames = ImmutableList.of("Notary");


    public Controller(NodeRPCConnection rpc) {

        this.proxy = rpc.proxy;
        this.myLegalName = proxy.nodeInfo().getLegalIdentities().get(0).getName();

    }

    /**
     * Return the name of the node
     */
    @RequestMapping(value = "/me", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, CordaX500Name> whoami(){
        return ImmutableMap.of("me", myLegalName);
    }


    /**
     * Returns all the registered peers
     */
    @RequestMapping(value = "/peers", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, List<CordaX500Name>> getPeers() {
        List<NodeInfo> nodeInfoSnapshot = proxy.networkMapSnapshot();
        return ImmutableMap.of("peers", nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName())
                .filter(name -> !name.equals(myLegalName) && !serviceNames.contains(name.getOrganisation()))
                .collect(toList()));
    }


}