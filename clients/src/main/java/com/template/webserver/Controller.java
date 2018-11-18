package com.template.webserver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.template.Bond;
import com.template.UKTFBondState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    /**
     * Returns all the registered peers
     */
    @RequestMapping(value = "/bonds", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JSONObject getBonds() {

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();

        Iterator<StateAndRef<UKTFBondState>> i = getUKTFBonds().iterator();
        int j = 0;
        while (i.hasNext()) {
            JSONObject item = new JSONObject();
            UKTFBondState bond = i.next().getState().getData();
            item.put("n", j);
            item.put("id", bond.getBondID());
            item.put("value", bond.getBondValue());
            item.put("creditScore", bond.getCreditScore());
            item.put("ukef", bond.getUKEFSupported());
            array.add(item);
            j++;
        }


        json.put("bonds", array);

        logger.info("result values" +  json.toJSONString());

        return json;

    }

    private List<StateAndRef<UKTFBondState>> getUKTFBonds () {

        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Vault.Page<UKTFBondState> results = proxy.vaultQueryByCriteria(criteria, UKTFBondState.class);
        List<StateAndRef<UKTFBondState>> bonds = results.getStates();

        logger.info("Number of UNCONSUMED bonds " +  bonds.toArray().length);

        return bonds;
    }

}